package com.github.gpor0.jaffas.exceptions;

public class UnauthorizedException extends ManagedException {

    private String causeMsg;

    public UnauthorizedException(String causeMsg, Throwable cause) {
        super(cause);
        this.causeMsg = causeMsg;
    }

    public UnauthorizedException(String causeMsg) {
        super(causeMsg);
        this.causeMsg = causeMsg;
    }

    public UnauthorizedException(Throwable cause) {
        super(cause);
    }

    public String getCauseMsg() {
        return causeMsg;
    }
}
