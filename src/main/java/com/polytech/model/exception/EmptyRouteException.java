package com.polytech.model.exception;

public class EmptyRouteException extends RuntimeException {

    /**
     * Constructs a new runtime exception with a specific detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public EmptyRouteException() {
        super("Trying to access an empty route");
    }
}
