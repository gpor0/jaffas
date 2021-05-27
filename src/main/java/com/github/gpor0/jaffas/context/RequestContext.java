package com.github.gpor0.jaffas.context;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.*;

@RequestScoped
public class RequestContext {

    private MultivaluedMap<String, String> queryParameters;
    private HttpHeaders headers;
    private Optional<UUID> userId;
    private Set<String> roles;
    private String cid;

    public void init(final String cid, final UriInfo uriInfo, final UUID userId, final Set<String> roles, final HttpHeaders headers) {
        this.cid = cid;
        this.queryParameters = uriInfo.getQueryParameters();
        this.headers = headers;
        this.userId = Optional.ofNullable(userId);
        this.roles = roles;
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
