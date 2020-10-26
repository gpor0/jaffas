package com.github.gpor0.commons.security;

import com.github.gpor0.commons.endpoints.OauthApi;
import com.github.gpor0.commons.endpoints.PermissionsApi;
import com.github.gpor0.commons.endpoints.model.SyncRolePermissions;
import com.github.gpor0.commons.endpoints.model.Token;
import com.github.gpor0.commons.exceptions.IntegrationException;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
    public Token getToken() {
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

            return api.getToken(authHeader, "client_credentials", "system");
        } catch (MalformedURLException e) {
            throw new NoSuchElementException("Invalid url:" + e.getMessage());
        } catch (Exception e) {
            throw new IntegrationException("oauth", e);
        }
    }

    public void syncRolePermissions(final SyncRolePermissions syncRolePermissions) {

        String serviceUrl = config.getValue("services.iam.url", String.class);

        URL url;
        try {
            url = new URL(serviceUrl);
        } catch (MalformedURLException e) {
            throw new NoSuchElementException("Invalid url:" + e.getMessage());
        }

        final PermissionsApi api = RestClientBuilder.newBuilder()
                .baseUrl(url)
                .build(PermissionsApi.class);

        String clientId = config.getValue("auth.client.clientId", String.class);

        api.syncPermissions(clientId, syncRolePermissions);
    }
}