package com.github.gpor0.jaffas.security;

import com.github.gpor0.jaffas.endpoints.OauthApi;
import com.github.gpor0.jaffas.endpoints.PermissionsApi;
import com.github.gpor0.jaffas.endpoints.model.SyncRolePermissions;
import com.github.gpor0.jaffas.endpoints.model.Token;
import com.github.gpor0.jaffas.exceptions.IntegrationException;
import com.github.gpor0.jaffas.rest.filters.RestClientLoggingFilter;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.NoSuchElementException;

@ApplicationScoped
public class SecurityServiceBean implements SecurityService {

    @Inject
    protected Config config;

    @Override
    public Token getToken(String audience) {
        String clientId = config.getValue("auth.client.clientId", String.class);
        String clientSecret = config.getValue("auth.client.clientSecret", String.class);

        String tokenUrl = config.getValue("auth.client.tokenUrl", String.class);
        try {

            String authHeader = "Basic " + Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(StandardCharsets.UTF_8));

            if (tokenUrl.endsWith("/token")) {
                tokenUrl = tokenUrl.split("/token")[0];
            }
            final URL url = new URL(tokenUrl);

            final OauthApi api = RestClientBuilder.newBuilder()
                    .baseUrl(url)
                    .build(OauthApi.class);

            // since we implement client integration via wildcard (all permissions)
            return api.getToken(authHeader, "client_credentials", audience, Security.SCOPE_WILDCARD);
        } catch (MalformedURLException e) {
            throw new NoSuchElementException("Invalid url:" + e.getMessage());
        } catch (Exception e) {
            throw new IntegrationException("oauth", e);
        }
    }

    @Override
    public void syncRolePermissions(final SyncRolePermissions syncRolePermissions) {

        String serviceUrl = config.getValue("services.iam.url", String.class);
        String audience = config.getOptionalValue("auth.client.audience", String.class).orElse(null);

        URL url;
        try {
            url = new URL(serviceUrl);
        } catch (MalformedURLException e) {
            throw new NoSuchElementException("Invalid url:" + e.getMessage());
        }

        final PermissionsApi api = RestClientBuilder.newBuilder()
                .baseUrl(url)
                .register(RestClientLoggingFilter.class)
                .build(PermissionsApi.class);

        String clientId = config.getValue("auth.client.clientId", String.class);

        Token token = getToken(audience);
        api.syncPermissions(token.getTokenType() + " " + token.getAccessToken(), clientId, syncRolePermissions);
    }
}