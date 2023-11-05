package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.ForbiddenException;
import com.github.gpor0.jaffas.exceptions.model.ApiFaultDetails;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
@ApplicationScoped
public class ForbiddenExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Override
    public Response toResponse(ForbiddenException e) {

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(e.getCauseMsg());

        LOG.warn(e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.FORBIDDEN, Arrays.asList(faultDetails), e);
    }
}
