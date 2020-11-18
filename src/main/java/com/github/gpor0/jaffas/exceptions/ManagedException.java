package com.github.gpor0.jaffas.exceptions;

public abstract class ManagedException extends RuntimeException {

    public ManagedException() {
    }

    public ManagedException(String message) {
        super(message);
    }

    public ManagedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ManagedException(Throwable cause) {
        super(cause);
    }

    public ManagedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
