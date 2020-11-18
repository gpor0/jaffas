package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.model.ApiFaultDetails;
import com.github.gpor0.jooreo.exceptions.ConflictException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Provider
@ApplicationScoped
public class ConflictExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<ConflictException> {


    @Override
    public Response toResponse(ConflictException e) {

        final ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(e.getFaultCode());

        final Map<String, String> fieldMap = new HashMap<>();

        String field = e.getField();
        Object value = e.getValue();
        Object[] fieldValues = e.getFieldValues();

        fieldMap.put(field, Objects.toString(value, null));

        if (fieldValues != null && fieldValues.length > 0) {
            for (int i = 0; i < fieldValues.length && fieldValues.length > i + 1; i = i + 2) {
                fieldMap.put(String.valueOf(fieldValues[i]), Objects.toString(fieldValues[i + 1], null));
            }
        }

        faultDetails.setFields(fieldMap);

        LOG.error(e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.CONFLICT, Arrays.asList(faultDetails), e);
    }

}
