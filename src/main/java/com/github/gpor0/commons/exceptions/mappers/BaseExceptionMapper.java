package com.github.gpor0.commons.exceptions.mappers;

import com.github.gpor0.commons.context.Environment;
import com.github.gpor0.commons.context.EnvironmentEnum;
import com.github.gpor0.commons.exceptions.UnknownManagedException;
import com.github.gpor0.commons.exceptions.model.ApiFault;
import com.github.gpor0.commons.exceptions.model.ApiFaultDetails;
import com.github.gpor0.commons.exceptions.model.ApiFaultRequest;
import com.github.gpor0.jooreo.RequestContextProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.NotSupportedException;
import javax.ws.rs.core.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BaseExceptionMapper {

    protected final static Logger LOG = LoggerFactory.getLogger("com.github.gpor0.commons.Exceptions");

    @Context
    protected HttpHeaders headers;

    @Context
    protected Request request;

    @Context
    protected UriInfo uriInfo;

    @Inject
    protected Environment environment;

    @Inject
    protected RequestContextProxy requestContextProxy;

    protected Response toResponse(Response.Status status, List<ApiFaultDetails> faultList, Throwable e) {
        return toResponse(status.getStatusCode(), faultList, e);
    }

    protected Response toResponse(int status, List<ApiFaultDetails> faultList, Throwable e) {

        final ApiFault fault = new ApiFault();
        final ApiFaultRequest faultRequest = new ApiFaultRequest();

        final Map<String, String> headerMap = headers.getRequestHeaders().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                entry -> String.join(",", entry.getValue())));

        if (headerMap.containsKey("Authorization"))
            headerMap.put("Authorization", "hidden ******");

        String rUid = requestContextProxy.getCid();
        String ipAddr = Optional.ofNullable(headerMap.get("X-Forwarded-For")).orElse(null);

        faultRequest.setHeaders(headerMap);
        faultRequest.setrUid(rUid);
        faultRequest.setSourceIp(ipAddr);
        faultRequest.setMethod(request.getMethod());
        faultRequest.setUri(uriInfo.getRequestUri().toString());
        fault.setRequest(faultRequest);
        fault.setDetails(faultList);

        final EnvironmentEnum env = environment.getEnvironment();
        if (env != EnvironmentEnum.PROD && env != EnvironmentEnum.STAGE) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            fault.setDebug(sw.toString());
        }

        Optional<MediaType> jsonMediaType = headers.getAcceptableMediaTypes().stream().filter(m -> "json".equals(m.getSubtype())).findFirst();

        if (jsonMediaType.isEmpty()) {
            return Response.status(status).build();
        }

        return Response.status(status).entity(fault).build();
    }


    public Response toResponse(Exception e) {
        Response.Status status = Response.Status.INTERNAL_SERVER_ERROR;
        String faultCode = "error.unknown";
        if (e instanceof UnknownManagedException) {
            UnknownManagedException unknownServiceException = (UnknownManagedException) e;
            //this is probably not useful since we should not propagate internal exception details to users...
            faultCode = unknownServiceException.getFaultCode();

            LOG.error(e.getMessage(), e);
        } else if (e instanceof NotFoundException) {
            status = Response.Status.NOT_FOUND;
            faultCode = "error.notFound";
            LOG.error(e.getMessage() + ": " + request.getMethod() + " " + uriInfo.getRequestUri());
        } else if (e instanceof NotSupportedException) {
            LOG.error(e.getMessage());
            return Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE).entity(e.getMessage()).build();
        } else {
            LOG.error(e.getMessage(), e);
        }

        ApiFaultDetails faultDetails = new ApiFaultDetails();
        faultDetails.setFaultCode(faultCode);

        return toResponse(status, Arrays.asList(faultDetails), e);
    }

}
