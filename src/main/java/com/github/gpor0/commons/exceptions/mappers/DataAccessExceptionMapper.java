package com.github.gpor0.commons.exceptions.mappers;

import com.github.gpor0.commons.exceptions.model.ApiFaultDetails;
import com.github.gpor0.commons.rest.R;
import org.jooq.exception.DataAccessException;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Provider
@ApplicationScoped
public class DataAccessExceptionMapper extends BaseExceptionMapper implements ExceptionMapper<DataAccessException> {

    private final static String CHILD_EXISTS_MSG = "Cannot delete or update a parent row";

    private static final Pattern FOREIGN_PATTERN = Pattern.compile("(.*)`(\\w*\\b)?`, CONSTRAINT(.*)");

    @Override
    public Response toResponse(DataAccessException e) {

        if (e.getMessage().contains(CHILD_EXISTS_MSG)) {
            int status = 423; //locked
            String faultCode = "error.referenceExists";

            ApiFaultDetails faultDetails = new ApiFaultDetails();
            faultDetails.setFaultCode(faultCode);

            Map<String, String> fieldMap = new HashMap<>();

            Matcher matcher = FOREIGN_PATTERN.matcher(e.getMessage());
            if (matcher.find()) {
                String group = matcher.group(2);
                if (group != null) {
                    String reference = R.snakeToCamelCase(group.toLowerCase());//CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, group.toLowerCase());
                    fieldMap.put("reference", reference);
                }
            }

            faultDetails.setFields(fieldMap);

            LOG.error(e.getMessage());
            LOG.debug(e.getMessage(), e);

            return toResponse(status, Arrays.asList(faultDetails), e);
        }

        return super.toResponse(e);
    }
}
