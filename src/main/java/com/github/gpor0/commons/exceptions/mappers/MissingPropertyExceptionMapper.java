package com.github.gpor0.commons.exceptions.mappers;

import com.github.gpor0.commons.exceptions.model.ApiFaultDetails;
import com.github.gpor0.jooreo.exceptions.MissingParameterException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Provider
@ApplicationScoped
public class MissingPropertyExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<MissingParameterException> {

    @Override
    public Response toResponse(MissingParameterException e) {

        String faultCode = "error.missingParameter";

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(faultCode);

        Map<String, String> fieldMap = new HashMap<>();
        fieldMap.put(e.getFieldName(), "");
        faultDetails.setFields(fieldMap);

        LOG.error(e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.BAD_REQUEST, Arrays.asList(faultDetails), e);
    }

}
