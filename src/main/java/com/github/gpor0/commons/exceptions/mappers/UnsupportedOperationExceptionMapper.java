package com.github.gpor0.commons.exceptions.mappers;

import com.github.gpor0.commons.exceptions.model.ApiFaultDetails;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
@ApplicationScoped
public class UnsupportedOperationExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<UnsupportedOperationException> {

    @Override
    public Response toResponse(UnsupportedOperationException e) {

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode("error.unsupportedOperation");

        LOG.error("Unsupported method {}", e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.NOT_FOUND, Arrays.asList(faultDetails), e);
    }
}
