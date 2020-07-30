package com.github.gpor0.commons.context;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RequestScoped
public class RequestContext {

    private MultivaluedMap<String, String> queryParameters;
    private Optional<UUID> userId;
    private Set<String> roles;
    private String cid;

    public void setUriInfo(UriInfo uriInfo) {
        this.queryParameters = uriInfo.getQueryParameters();
    }

    public Optional<UUID> getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = Optional.ofNullable(userId);
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public Optional<Boolean> hasRel(String rel) {
        List<String> param = queryParameters == null ? null : queryParameters.get("rel");
        if (param != null && (param.contains(rel) || param.contains("*"))) {
            return Optional.of(true);
        }
        return Optional.empty();
    }
}
