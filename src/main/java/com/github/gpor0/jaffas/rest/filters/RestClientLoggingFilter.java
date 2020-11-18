package com.github.gpor0.jaffas.rest.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;

public class RestClientLoggingFilter implements ClientRequestFilter, ClientResponseFilter {

    protected final static Logger LOG = LoggerFactory.getLogger(RestClientLoggingFilter.class);

    @Override
    public void filter(ClientRequestContext clientRequestContext) {
        LOG.info("client req {}", clientRequestContext);
    }

    @Override
    public void filter(ClientRequestContext clientRequestContext, ClientResponseContext clientResponseContext) {
        LOG.info("client resp {}", clientResponseContext);
    }
}
