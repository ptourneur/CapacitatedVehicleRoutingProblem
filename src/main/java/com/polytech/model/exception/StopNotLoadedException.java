package com.polytech.model.exception;

public class StopNotLoadedException extends RuntimeException {

    /**
     * Constructs a new runtime exception with a specific detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public StopNotLoadedException() {
        super("Trying to access the graph before stops were loaded");
    }
}
