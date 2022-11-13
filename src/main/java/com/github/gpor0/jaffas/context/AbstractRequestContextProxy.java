package com.github.gpor0.jaffas.context;

import com.github.gpor0.jooreo.RequestContextProxy;
import org.eclipse.microprofile.config.Config;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.*;

import static com.github.gpor0.jaffas.security.Security.SCOPE_WILDCARD;

/**
 * Service must extend this class and implement getBasePath method. Extended class must be @ApplicationScoped
 */
public abstract class AbstractRequestContextProxy implements RequestContextProxy {

    public static final UUID SYSTEM_UID = UUID.fromString("000fa15e-b00b-babe-e475-deadbeef00");
    public static final UUID PUBLIC_USER_UID = UUID.fromString("deadbeef-dead-beef-dead-beefdeadbe");

    @Inject
    protected Config config;

    @Override
    public UUID getId() {
        try {
            Optional<UUID> userId = getRequestContext().getUserId();
            if (userId != null) {
                return userId.orElse(PUBLIC_USER_UID);
            }
        } catch (ContextNotActiveException e) {
        }
        return SYSTEM_UID;
    }

    @Override
    public Set<String> getIamRoles() {

        return SYSTEM_UID.equals(getId()) ? Set.of(SCOPE_WILDCARD) : getRequestContext().getRoles();
    }

    @Override
    public boolean hasIamRole(String role) {
        return SYSTEM_UID.equals(getId()) || getIamRoles().contains(role);
    }

    @Override
    public Set<String> getScopes() {
        return getRequestContext().getScopes();
    }

    @Override
    public boolean hasScope(String s) {
        return getRequestContext().hasScope(s);
    }

    private RequestContext getRequestContext() {
        return CDI.current().select(RequestContext.class).get();
    }

    @Override
    public Optional<String> language() {
        try {
            return getRequestContext().language();
        } catch (ContextNotActiveException e) {
        }
        return Optional.empty();
    }

    public Optional<String> getQueryParamValue(String paramName) {
        try {
            return getRequestContext().getQueryParamValue(paramName);
        } catch (ContextNotActiveException e) {
        }
        return Optional.empty();
    }

    public List<String> getQueryParamValues(String paramName) {
        try {
            return getRequestContext().getQueryParamValues(paramName);
        } catch (ContextNotActiveException e) {
        }
        return Arrays.asList();
    }

    @Override
    public Optional<Boolean> hasRel(String rel) {
        try {
            return getRequestContext().hasRel(rel);
        } catch (ContextNotActiveException e) {
            return Optional.empty();
        }
    }

    @Override
    public String getCid() {
        return getRequestContext().getCid();
    }

    @Override
    public abstract String getBasePath();
}
