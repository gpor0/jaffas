package com.github.gpor0.jaffas.security;

import com.github.gpor0.jaffas.exceptions.ForbiddenException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;
import org.eclipse.microprofile.jwt.JsonWebToken;

import javax.enterprise.inject.spi.CDI;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.gpor0.jaffas.context.AbstractRequestContextProxy.SYSTEM_UID;
import static com.github.gpor0.jaffas.security.Security.SCOPE_WILDCARD;

/**
 * This class is meant to be shared among all microservices.
 * <p>
 * It contains security logic
 */
public abstract class BaseSecurityFilter {

    private static final Logger LOG = Logger.getLogger(BaseSecurityFilter.class.getName());

    private static final Map<String, Set<String>> METHOD_SCOPE_CACHE = new ConcurrentHashMap<>();

    public static AccessToken mapToAccessTokenData(String subject, Set<String> scopes, Map<String, Object> extClaims) {

        if (subject == null) {
            return null;
        }

        UUID userId;
        final Object userIdClaim = extClaims.get("userId");
        if (userIdClaim == null) {
            userId = SYSTEM_UID;
        } else if (userIdClaim instanceof String) {
            userId = UUID.fromString((String) userIdClaim);
        } else {
            userId = UUID.fromString(userIdClaim.toString().replace("\"", ""));
        }

        //admin user and client application does not have tenant set
        UUID tenantId = null;
        final Object tenantIdClaim = extClaims.get("tenantId");
        if (tenantIdClaim != null) {
            if (tenantIdClaim instanceof String) {
                tenantId = UUID.fromString((String) tenantIdClaim);
            } else {
                tenantId = UUID.fromString(tenantIdClaim.toString().replace("\"", ""));
            }
        }

        final AccessToken accessToken = new AccessToken();
        accessToken.setSubject(subject);
        accessToken.setUserId(userId);
        accessToken.setTenantId(tenantId);
        accessToken.setScopes(scopes);

        return accessToken;
    }

    protected static void verifyMethodAccess(String resourceName, Set<String> tokenScopes, Set<String> methodScopes) {

        if (methodScopes.isEmpty()) {
            LOG.fine(() -> "Method " + resourceName + " does not require security grants");
            return;
        }

        if (tokenScopes.contains(SCOPE_WILDCARD)) {
            LOG.fine(() -> "Found wildcard scope, token scopes: " + tokenScopes + ", method scopes: " + methodScopes);
        } else if (tokenScopes.stream().anyMatch(methodScopes::contains)) {
            LOG.fine(() -> "Found scope, token scopes: " + tokenScopes + ", method scopes: " + methodScopes);
        } else {
            LOG.info(() -> "No scope match for operation " + resourceName + ". Required: " + methodScopes + " Found: " + tokenScopes);
            throw new ForbiddenException("errors.insufficientPrivileges", methodScopes);
        }
    }

    /**
     * This method check if http operation requires scope.
     * <p>inte
     * Returns Access token data or null when operation does not require scope
     * <p>
     * It throws
     * - Unauthorized exception if provided token is not valid
     * - Forbidden exception if token is valid but does not contain required operation scope
     *
     * @param httpMethod
     * @param requestUri
     * @param operation
     * @return
     */
    public AccessToken authenticate(String httpMethod, URI requestUri, String resourceName, ApiOperation operation) {
        //pass through OPTIONS calls.
        if ("OPTIONS".equals(httpMethod)) {
            return null;
        }

        String path = requestUri == null ? null : requestUri.getPath();

        if (operation == null) {
            LOG.fine(() -> "URL " + path + " does not require security");
            return null;
        }

        final Set<String> methodScopeSet;
        String methodName = httpMethod + ":" + resourceName;
        if (METHOD_SCOPE_CACHE.containsKey(methodName)) {
            methodScopeSet = METHOD_SCOPE_CACHE.get(methodName);
        } else {
            methodScopeSet = new HashSet<>();
            for (Authorization authorization : operation.authorizations()) {
                Stream.of(authorization.scopes()).map(AuthorizationScope::scope)
                        .filter(Objects::nonNull).filter(Predicate.not(String::isBlank)).forEach(methodScopeSet::add);
            }
            METHOD_SCOPE_CACHE.put(methodName, methodScopeSet);
        }

        final JsonWebToken jsonWebToken = CDI.current().select(JsonWebToken.class).get();

        if (jsonWebToken == null) {
            //public call with no token
            verifyMethodAccess(resourceName, Set.of(), methodScopeSet);
            return null;
        }
        final String subject = jsonWebToken.getSubject();
        final Collection<Object> tokenScopes = jsonWebToken.getClaim("scp");
        final Map<String, Object> tokenExtClaims = jsonWebToken.getClaim("ext");

        final Set<String> tokenScopeSet = tokenScopes == null ? Set.of() : tokenScopes.stream().map(String::valueOf).map(e -> e.replaceAll("\"", "")).collect(Collectors.toSet());
        verifyMethodAccess(resourceName, tokenScopeSet, methodScopeSet);

        AccessToken accessToken = mapToAccessTokenData(subject, tokenScopeSet, tokenExtClaims);

        LOG.fine(() -> "Data access granted to " + accessToken.getUserId());
        return accessToken;
    }

    public static class AccessToken {
        private String subject;
        private UUID userId;
        private UUID tenantId;
        private Set<String> scopes;

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
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

        public void setScopes(Set<String> scopes) {
            this.scopes = scopes;
        }
    }

}
