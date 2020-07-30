package com.github.gpor0.commons.exceptions;

public class IntegrationException extends ManagedException {

    public IntegrationException(String integration) {
        super(integration + " integration problem");
    }

    public IntegrationException(String integration, Exception e) {
        super(integration + " integration problem", e);
    }
}
