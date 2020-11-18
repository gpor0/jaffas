package com.github.gpor0.jaffas.exceptions.mappers;

import com.github.gpor0.jaffas.exceptions.model.ApiFaultDetails;

import javax.enterprise.context.ApplicationScoped;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Provider
@ApplicationScoped
public class ConstraintViolationExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException e) {

        String faultCode = "error.invalidParam";

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(faultCode);

        Map<String, String> fieldMap = new HashMap<>();
        e.getConstraintViolations().forEach(v -> {
            String fields[] = v.getPropertyPath().toString().split("\\.");
            if (fields.length > 2)
                fields = Arrays.copyOfRange(fields, 2, fields.length);
            fieldMap.put(String.join(".", fields), String.valueOf(v.getInvalidValue()));
            LOG.error(v.toString());
        });

        faultDetails.setFields(fieldMap);

        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.BAD_REQUEST, Arrays.asList(faultDetails), e);
    }

}
