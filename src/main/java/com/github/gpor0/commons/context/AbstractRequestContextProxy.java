package com.github.gpor0.commons.context;

import com.github.gpor0.jooreo.RequestContextProxy;
import org.eclipse.microprofile.config.Config;

import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.github.gpor0.commons.security.Security.SCOPE_SYSTEM;

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
    public Set<String> getRoles() {

        return SYSTEM_UID.equals(getId()) ? Set.of(SCOPE_SYSTEM) : getRequestContext().getRoles();
    }

    @Override
    public boolean hasRole(String role) {
        return getRoles().contains(role);
    }

    private RequestContext getRequestContext() {
        return CDI.current().select(RequestContext.class).get();
    }

    @Override
    public Optional<Boolean> hasRel(String rel) {
        return SYSTEM_UID.equals(getId()) ? Optional.empty() : getRequestContext().hasRel(rel);
    }

    @Override
    public String getCid() {
        return getRequestContext().getCid();
    }

    @Override
    public abstract String getBasePath();
}
