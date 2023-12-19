package com.apps.mobileappws.exceptions;

import java.io.Serial;

public class UserServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1584941245256113084L;

    public UserServiceException(String message) {
        super(message);
    }

}
