package com.github.gpor0.jaffas.exceptions.mappers;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@ApplicationScoped
public class ServiceExceptionMapper extends BaseExceptionMapper implements jakarta.ws.rs.ext.ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        return super.toResponse(e);
    }

}
