package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.model.ApiFaultDetails;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotAllowedException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Provider
@ApplicationScoped
public class MethodNotAllowedExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<NotAllowedException> {

    @Override
    public Response toResponse(NotAllowedException e) {

        String faultCode = "error.resourceNotFound";

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(faultCode);

        Map<String, String> fieldMap = new HashMap<>();
        faultDetails.setFields(fieldMap);

        LOG.error(e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.NOT_FOUND, Arrays.asList(faultDetails), e);
    }
}