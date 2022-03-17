package com.github.gpor0.jaffas.exceptions;

public class IntegrationException extends ManagedException {

    private String customFaultCode;

    public IntegrationException(String integration) {
        super(integration + " integration problem");
    }

    public IntegrationException(String integration, Exception e) {
        super(integration + " integration problem", e);
    }

    public IntegrationException(String message, String customFaultCode) {
        super(message);
        this.customFaultCode = customFaultCode;
    }

    public IntegrationException(String message, String customFaultCode, Exception e) {
        super(message, e);
        this.customFaultCode = customFaultCode;
    }

    public String getCustomFaultCode() {
        return customFaultCode;
    }
}
