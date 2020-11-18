/*
 * © 2020 Doorson d.o.o.
 */
package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.NotImplementedException;
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
public class NotImplementedExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<NotImplementedException> {

    @Override
    public Response toResponse(NotImplementedException e) {

        String faultCode = "error.notImplemented";


        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(faultCode);

        Map<String, String> fieldMap = new HashMap<>();
        faultDetails.setFields(fieldMap);

        LOG.error(e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.NOT_IMPLEMENTED, Arrays.asList(faultDetails), e);
    }
}
