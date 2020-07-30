package com.github.gpor0.commons.security;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.gpor0.commons.exceptions.ForbiddenException;
import com.github.gpor0.commons.exceptions.UnauthorizedException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.eclipse.microprofile.config.Config;

import javax.inject.Inject;
import java.net.URI;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is meant to be shared among all microservices.
 * <p>
 * It contains security logic
 */
public abstract class Security { //todo move this to separate project

    private static final Logger LOG = Logger.getLogger(Security.class.getName());

    public static final String SCOPE_SYSTEM = "system";

    @Inject
    protected Config config;

    public abstract String getApplicationId();

    public abstract List<ApplicationRole> getRoles();

    public void initialize() {
        List<ApplicationRole> roles = getRoles();
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
            Set<String> scopes = Stream.of(authorization.scopes()).map(AuthorizationScope::scope).collect(Collectors.toSet());
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

            JwkProvider provider = new UrlJwkProvider(jwkProviderUrl);
            Jwk jwk = provider.get(jwt.getKeyId());
            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);

            JWTVerifier verifier = JWT
                    .require(algorithm)
                    .acceptLeeway(120)
                    .build(); //120 secs for NTP skew

            verifier.verify(jwt);

            UUID subjectId = UUID.fromString(jwt.getClaim("sub").asString());
            Map<String, Object> extClaim = jwt.getClaim("ext").asMap();
            UUID userId = UUID.fromString((String) extClaim.get("userId"));
            UUID tenantId = UUID.fromString((String) extClaim.get("tenantId"));

            final List<String> scopes = jwt.getClaim("scp").asList(String.class);

            final AccessToken accessToken = new AccessToken();
            accessToken.setSubject(subjectId);
            accessToken.setUserId(userId);
            accessToken.setTenantId(tenantId);
            accessToken.setScopes(scopes);

            LOG.fine(() -> "Token valid for userId " + userId);
            return accessToken;
        } catch (Exception e) {
            throw new UnauthorizedException("errors.unauthorized.sessionExpired", e);
        }
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
        private Map<String, String> permissions;

        public ApplicationRole(String name) {
            this.name = name;
        }

        public ApplicationRole addPermission(String name, String description) {
            permissions.put(name, description);
            return this;
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
