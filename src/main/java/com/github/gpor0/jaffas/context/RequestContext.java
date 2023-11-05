package com.github.gpor0.jaffas.context;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.UriInfo;
import java.util.*;

@RequestScoped
public class RequestContext {

    private MultivaluedMap<String, String> queryParameters;
    private HttpHeaders headers;
    private Optional<UUID> userId;
    private Set<String> scopes;
    private Set<String> roles;
    private String cid;

    public void init(final String cid, final UriInfo uriInfo, final UUID userId, final Set<String> roles, final Set<String> scopes, final HttpHeaders headers) {
        this.cid = cid;
        this.queryParameters = uriInfo.getQueryParameters();
        this.headers = headers;
        this.userId = Optional.ofNullable(userId);
        this.roles = roles;
        this.scopes = scopes;
    }

    public Optional<UUID> getUserId() {
        return userId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public Set<String> getScopes() {
        return scopes;
    }

    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }

    public String getCid() {
        return cid;
    }

    public Optional<Boolean> hasRel(String rel) {
        List<String> param = queryParameters == null ? null : queryParameters.get("rel");
        if (param != null && (param.contains(rel) || param.contains("*"))) {
            return Optional.of(true);
        }
        return Optional.empty();
    }

    public Optional<String> getQueryParamValue(String paramName) {
        List<String> param = queryParameters == null ? null : queryParameters.get(paramName);
        return param == null || param.size() < 1 ? Optional.empty() : Optional.of(param.get(0));
    }

    public List<String> getQueryParamValues(String paramName) {
        List<String> param = queryParameters == null ? null : queryParameters.get(paramName);
        return param;
    }

    public Locale acceptableLocale() {

        if (headers == null) {
            return null;
        }

        List<Locale> acceptableLanguages = headers.getAcceptableLanguages();

        return acceptableLanguages.stream().findFirst().orElse(null);
    }

    public Optional<String> language() {

        if (headers == null) {
            //queryParameters == null ? null : queryParameters.getFirst("lang");
            return null;
        }

        final Locale acceptableLocale = acceptableLocale();

        return acceptableLocale == null ? Optional.empty() : Optional.of(acceptableLocale.getLanguage());
    }
}
