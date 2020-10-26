package com.github.gpor0.commons.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.gpor0.commons.endpoints.model.SyncPermission;
import com.github.gpor0.commons.endpoints.model.SyncRole;
import com.github.gpor0.commons.endpoints.model.SyncRolePermissions;
import com.github.gpor0.commons.exceptions.ForbiddenException;
import com.github.gpor0.commons.exceptions.UnauthorizedException;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.eclipse.microprofile.config.Config;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import java.io.InputStream;
import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.gpor0.commons.context.AbstractRequestContextProxy.SYSTEM_UID;

/**
 * This class is meant to be shared among all microservices.
 * <p>
 * It contains security logic
 */
public abstract class Security {

    private static final Logger LOG = Logger.getLogger(Security.class.getName());

    public static final String SCOPE_SYSTEM = "system";

    @Inject
    protected Config config;

    public abstract String getApplicationName();

    public abstract List<ApplicationRole> getRoles();

    public String getClientId() {
        return config.getValue("auth.client.clientId", String.class);
    }

    /**
     * This method check if http operation requires scope.
     * <p>
     * Returns Access token data or null when operation does not require scope
     * <p>
     * It throws
     * - Unauthorized exception if provided token is not valid
     * - Forbidden exception if token is valid but does not contain required operation scope
     *
     * @param jwkProviderUrl
     * @param httpMethod
     * @param requestUri
     * @param authorizationHeader
     * @param operation
     * @return
     */
    public static AccessToken authenticate(String jwkProviderUrl, String httpMethod, URI requestUri, String authorizationHeader, ApiOperation operation) {
        //pass through OPTIONS calls.
        if ("OPTIONS".equals(httpMethod)) {
            return null;
        }

        String path = requestUri == null ? null : requestUri.getPath();

        if (operation == null) {
            LOG.fine(() -> "URL " + path + " does not require security");
            return null;
        }

        Set<String> methodScopeSet = new HashSet<>();
        for (Authorization authorization : operation.authorizations()) {
            Set<String> scopes = Stream.of(authorization.scopes()).map(AuthorizationScope::scope)
                    .filter(Objects::nonNull).filter(Predicate.not(String::isBlank)).collect(Collectors.toSet());
            methodScopeSet.addAll(scopes);
        }
        methodScopeSet.remove(null);

        if (methodScopeSet.isEmpty()) {
            LOG.fine(() -> "Operation " + operation.value() + " does not require security");
            return null;
        }

        AccessToken accessToken = parseAndVerify(jwkProviderUrl, authorizationHeader);

        verifyMethodAccess(operation.value(), methodScopeSet, accessToken);

        LOG.fine(() -> "Data access granted to " + accessToken.getUserId());
        return accessToken;
    }

    public static Optional<String> getSubject(final String token) {
        DecodedJWT jwt = Security.parse(token);
        return jwt == null ? Optional.empty() : Optional.ofNullable(jwt.getClaim("sub").asString());
    }

    public static DecodedJWT parse(final String token) {
        final String bearer = token == null || !token.startsWith("Bearer ") ? null : token.split(" ")[1];

        if (bearer == null) {
            return null;
        }

        return JWT.decode(bearer);
    }

    public static AccessToken parseAndVerify(final String jwkProviderUrl, final String token) {

        final DecodedJWT jwt = parse(token);
        if (null == jwt) {
            throw new UnauthorizedException("errors.unauthorized");
        }

        try {

            final JwkProvider provider = new UrlJwkProvider(jwkProviderUrl);
            final Jwk jwk = provider.get(jwt.getKeyId());
            final Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            final JWTVerifier verifier = JWT
                    .require(algorithm)
                    .acceptLeeway(120)
                    .build(); //120 secs for NTP skew

            verifier.verify(jwt);

            final List<String> scopes = jwt.getClaim("scp").asList(String.class);

            UUID subjectId = SYSTEM_UID;
            UUID userId = SYSTEM_UID;
            UUID tenantId = null;
            if (!scopes.contains(SCOPE_SYSTEM)) {
                subjectId = UUID.fromString(jwt.getClaim("sub").asString());
                Map<String, Object> extClaim = jwt.getClaim("ext").asMap();
                userId = UUID.fromString((String) extClaim.get("userId"));
                tenantId = UUID.fromString((String) extClaim.get("tenantId"));
            }

            final AccessToken accessToken = new AccessToken();
            accessToken.setSubject(subjectId);
            accessToken.setUserId(userId);
            accessToken.setTenantId(tenantId);
            accessToken.setScopes(scopes);

            LOG.fine(() -> "Token valid for subject " + SYSTEM_UID);
            return accessToken;
        } catch (Exception e) {
            throw new UnauthorizedException("errors.unauthorized.sessionExpired", e);
        }
    }

    /**
     * Security gets parsed from Openapi specification in yaml format.
     * <p>
     * All roles must be defined in:
     * components:
     * securitySchemes:
     * "shemeName":
     * flows:
     * "flowName":
     * scopes:
     * system: system permissions
     * accessAdmin: User access administrator
     * ....
     * <p>
     * Each operation defines scopes and permissions required to call.
     * <p>
     * security:
     * - "shemeName":
     * - 'userRole:cre'
     * - accessAdmin
     * - system
     * <p>
     * Above example shows accessAdmin and system as roles and userRole:cre as permission. Roles MUST be defined in securitySchemes. Parsed security item is defined as permission if not defined in flow scopes.
     * <p>
     * Each operation may implement further security requirements (like user check...).
     *
     * @param yamlStream
     */
    public SyncRolePermissions readYamlSecurity(InputStream yamlStream) {
        final Yaml yaml = new Yaml();
        final Map<String, Object> obj = yaml.load(yamlStream);

        final Map<String, Set<String>> scopeMap = new HashMap<>();
        final Map<String, String> scopeDescMap = new HashMap<>();
        final Map<String, String> permissionDescMap = new HashMap<>();

        this.getRoles().stream().forEach(appRole -> {
            scopeMap.put(appRole.getName(), appRole.getPermissions());
            scopeDescMap.put(appRole.getName(), appRole.getDescription());
            appRole.getPermissions().stream().forEach(perm -> permissionDescMap.put(perm, null));
        });


        final Map<String, Map> components = (Map<String, Map>) obj.get("components");
        final Map<String, Map> securitySchemes = (Map<String, Map>) components.get("securitySchemes");
        for (Map.Entry<String, Map> def : securitySchemes.entrySet()) {
            Map<String, Map> flows = (Map) def.getValue().get("flows");

            for (Map.Entry<String, Map> flow : flows.entrySet()) {
                if (flow != null) {
                    Map<String, String> scopeNameDesc = (Map<String, String>) flow.getValue().get("scopes");
                    scopeNameDesc.entrySet().stream().forEach(e -> {
                        scopeMap.put(e.getKey(), new HashSet<>());
                        scopeDescMap.put(e.getKey(), e.getValue());
                    });
                }
            }
        }

        final Map<String, Map> map = (Map<String, Map>) obj.get("paths");
        for (Map.Entry<String, Map> path : map.entrySet()) {
            Map<String, Map> api = path.getValue();
            for (Map.Entry<String, Map> method : api.entrySet()) {
                Map methodProps = method.getValue();
                String operationId = (String) methodProps.get("operationId");
                String description = (String) methodProps.get("description");
                List<Map> securities = (List<Map>) methodProps.get("security");

                if (securities == null) {
                    continue;
                }
                for (Map<String, List<String>> auth : securities) {
                    for (Map.Entry<String, List<String>> scope : auth.entrySet()) {
                        Set<String> scopeSet = new HashSet<>(scope.getValue());
                        Sets.SetView<String> roles = Sets.intersection(scopeSet, scopeMap.keySet());
                        Sets.SetView<String> permissions = Sets.difference(scopeSet, roles);
                        permissions.stream().forEach(permission -> permissionDescMap.put(permission, operationId + ": " + description));
                        roles.stream().forEach(role -> scopeMap.get(role).addAll(permissions));
                    }
                }
            }
        }

        final List<SyncRole> applicationRolePermissions = scopeMap.entrySet().stream().map(roleEntry -> {
            SyncRole applicationRole = new SyncRole();
            applicationRole.setName(roleEntry.getKey());
            applicationRole.setDescription(scopeDescMap.get(roleEntry.getKey()));
            roleEntry.getValue().stream().forEach(permission -> applicationRole.addPermissionsItem(permission));
            return applicationRole;
        }).collect(Collectors.toList());

        final List<SyncPermission> applicationPermissions = permissionDescMap.entrySet().stream().map(e -> new SyncPermission().name(e.getKey()).description(e.getValue())).collect(Collectors.toList());

        SyncRolePermissions result = new SyncRolePermissions();
        result.setPermissions(applicationPermissions);
        result.setRolePermissions(applicationRolePermissions);

        return result;
    }

    protected static void verifyMethodAccess(String operationName, Set<String> methodScopes, AccessToken accessToken) {

        if (methodScopes.isEmpty()) {
            LOG.fine(() -> "Method does not require security grants");
            return;
        }

        if (accessToken.getScopes().stream().anyMatch(methodScopes::contains)) {
            LOG.fine(() -> "Found scope, token scopes: " + accessToken.getScopes() + ", method scopes: " + methodScopes);
        } else {
            LOG.info(() -> "No scope match for operation " + operationName + ". Required: " + methodScopes + ", Found: " + accessToken.getScopes());
            throw new ForbiddenException("errors.insufficientPrivileges", methodScopes);
        }
    }

    public static class ApplicationRole {
        private String name;
        private String description;
        private Set<String> permissions;

        public ApplicationRole(String name, String description) {
            this.name = name;
            this.description = description;
            this.permissions = new HashSet<>();
        }

        public ApplicationRole addPermission(String name) {
            permissions.add(name);
            return this;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public Set<String> getPermissions() {
            return permissions;
        }
    }

    public static class AccessToken {
        private UUID subject;
        private UUID userId;
        private UUID tenantId;
        private Set<String> scopes;

        public UUID getSubject() {
            return subject;
        }

        public void setSubject(UUID subject) {
            this.subject = subject;
        }

        public UUID getUserId() {
            return userId;
        }

        public void setUserId(UUID userId) {
            this.userId = userId;
        }

        public UUID getTenantId() {
            return tenantId;
        }

        public void setTenantId(UUID tenantId) {
            this.tenantId = tenantId;
        }

        public Set<String> getScopes() {
            return scopes;
        }

        public void setScopes(List<String> scopes) {
            this.scopes = new HashSet<>(scopes);
        }
    }

}
