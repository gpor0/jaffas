package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.model.ApiFaultDetails;
import com.github.gpor0.jooreo.exceptions.ParameterSyntaxException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Provider
@ApplicationScoped
public class InvalidParameterExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<IllegalArgumentException> {

    @Override
    public Response toResponse(IllegalArgumentException e) {

        String faultCode = "error.invalidParam";

        Map<String, String> fieldMap = new HashMap<>();

        if (e instanceof ParameterSyntaxException) {
            String field = ((ParameterSyntaxException) e).getField();
            Object value = ((ParameterSyntaxException) e).getValue();
            Object[] fieldValues = ((ParameterSyntaxException) e).getFieldValues();

            fieldMap.put(field, Objects.toString(value, null));

            if (fieldValues != null && fieldValues.length > 0) {
                for (int i = 0; i < fieldValues.length && fieldValues.length > i + 1; i = i + 2) {
                    fieldMap.put(String.valueOf(fieldValues[i]), Objects.toString(fieldValues[i + 1], null));
                }
            }
        } else {
            fieldMap.put("unknown", e.getMessage());
        }

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(faultCode);


        faultDetails.setFields(fieldMap);

        LOG.error(e.getMessage());
        if (e.getMessage() == null || e.getClass() == IllegalArgumentException.class) {
            LOG.error(e.getMessage(), e);
        }
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.BAD_REQUEST, Arrays.asList(faultDetails), e);
    }
}
