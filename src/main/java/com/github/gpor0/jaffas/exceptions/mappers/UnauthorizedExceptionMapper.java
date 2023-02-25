package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.UnauthorizedException;
import com.github.gpor0.jaffas.exceptions.model.ApiFaultDetails;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;

@Provider
@ApplicationScoped
public class UnauthorizedExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<UnauthorizedException> {

    @Override
    public Response toResponse(UnauthorizedException e) {

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(e.getCauseMsg());

        LOG.warn(e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.UNAUTHORIZED, Arrays.asList(faultDetails), e);
    }
}
