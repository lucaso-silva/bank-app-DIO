package org.lucas.exception;

public class AccountIdInUseException extends RuntimeException {

    public AccountIdInUseException(String message) {
        super(message);
    }

}
