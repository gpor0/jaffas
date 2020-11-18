package com.github.gpor0.jaffas.exceptions;

import java.util.Set;

public class ForbiddenException extends ManagedException {

    private String causeMsg;
    private Set<String> requiredScopes;

    public ForbiddenException(String causeMsg, Throwable cause) {
        super(cause);
        this.causeMsg = causeMsg;
    }

    public ForbiddenException(String causeMsg) {
        super(causeMsg);
        this.causeMsg = causeMsg;
    }

    public ForbiddenException(String causeMsg, Set<String> requiredScopes) {
        super(causeMsg);
        this.causeMsg = causeMsg;
        this.requiredScopes = requiredScopes;
    }

    public ForbiddenException(Throwable cause) {
        super(cause);
    }

    public String getCauseMsg() {
        return causeMsg;
    }

    public Set<String> getRequiredScopes() {
        return requiredScopes;
    }
}
