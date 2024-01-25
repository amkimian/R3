package com.staberinde.sscript.exception;

public class SSException extends RuntimeException {
    public SSException(String message) {
        super(message);
    }

    public SSException(String message, Throwable e) {
        super(message, e);
    }

    public SSException(Exception e) {
        super(e);
    }

    public static SSException createTypeMismatchException(final String requestedType, final String actualType) {
        return new SSException(String.format("Value type %s does not match requested type %s", actualType, requestedType));
    }
}
