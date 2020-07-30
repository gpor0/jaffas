package com.github.gpor0.commons.exceptions.mappers;

import com.github.gpor0.commons.exceptions.model.ApiFaultDetails;
import org.jooq.exception.DataChangedException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Provider
@ApplicationScoped
public class DataChangedExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<DataChangedException> {

    @Override
    public Response toResponse(DataChangedException e) {

        String faultCode = "error.dataChanged";

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(faultCode);

        Map<String, String> fieldMap = new HashMap<>();

        fieldMap.put("version", "");

        faultDetails.setFields(fieldMap);

        LOG.error(e.getMessage());
        LOG.debug(e.getMessage(), e);

        return toResponse(Response.Status.CONFLICT, Arrays.asList(faultDetails), e);
    }

}
