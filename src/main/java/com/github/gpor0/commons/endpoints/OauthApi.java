package com.github.gpor0.commons.endpoints;

import com.github.gpor0.commons.endpoints.model.Token;

import javax.ws.rs.*;

@Path("/")
public interface OauthApi {

    @POST
    @Path("/token")
    @Produces("application/json")
    @Consumes("application/x-www-form-urlencoded")
    Token getToken(@HeaderParam("Authorization") String authHeaderValue, @FormParam("grant_type") String grantType, @FormParam("scope") String scope);

}
