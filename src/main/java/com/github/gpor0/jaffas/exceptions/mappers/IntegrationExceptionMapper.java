package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.IntegrationException;
import com.github.gpor0.jaffas.exceptions.model.ApiFaultDetails;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Provider
@ApplicationScoped
public class IntegrationExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<IntegrationException> {

    @Override
    public Response toResponse(IntegrationException e) {

        String faultCode = e.getCustomFaultCode() == null ? "error.integrationIssue" : e.getCustomFaultCode();

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(faultCode);

        Map<String, String> fieldMap = new HashMap<>();
        faultDetails.setFields(fieldMap);

        LOG.error(e.getMessage(), e);

        return toResponse(Response.Status.INTERNAL_SERVER_ERROR, Arrays.asList(faultDetails), e);
    }
}
