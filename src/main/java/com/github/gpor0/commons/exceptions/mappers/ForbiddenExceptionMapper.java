package com.github.gpor0.commons.exceptions.mappers;

import com.github.gpor0.commons.exceptions.ForbiddenException;
import com.github.gpor0.commons.exceptions.model.ApiFaultDetails;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
@ApplicationScoped
public class ForbiddenExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<ForbiddenException> {

    @Override
    public Response toResponse(ForbiddenException e) {

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(e.getCauseMsg());

        LOG.error(e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.FORBIDDEN, Arrays.asList(faultDetails), e);
    }
}
