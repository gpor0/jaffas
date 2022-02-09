package com.github.gpor0.jaffas.security;

import com.github.gpor0.jaffas.endpoints.model.SyncRolePermissions;
import com.github.gpor0.jaffas.endpoints.model.Token;

public interface SecurityService {

    /**
     * makes authorization towards oauth2 provider using client_credentials grant type and returns bearer token
     * <p>
     * set following configuration keys:
     * auth.client.clientId
     * auth.client.clientSecret
     * auth.client.tokenUrl
     *
     * @return Token
     */
    Token getToken(String audience);

    void syncRolePermissions(SyncRolePermissions syncRolePermissions);

}
