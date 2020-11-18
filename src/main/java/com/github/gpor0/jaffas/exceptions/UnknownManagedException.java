package com.github.gpor0.jaffas.exceptions;

public class UnknownManagedException extends RuntimeException {

    private String faultCode = "errors.unknown";

    public UnknownManagedException(Throwable cause) {
        super(cause);
    }

    public UnknownManagedException(Throwable cause, String faultCode) {
        super(cause);
        this.faultCode = faultCode;
    }

    public String getFaultCode() {
        return faultCode;
    }
}
