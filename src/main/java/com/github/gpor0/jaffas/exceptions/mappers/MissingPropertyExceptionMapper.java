package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.model.ApiFaultDetails;
import com.github.gpor0.jooreo.exceptions.MissingParameterException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
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
