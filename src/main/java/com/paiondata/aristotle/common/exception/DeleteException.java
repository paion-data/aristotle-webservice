package com.paiondata.aristotle.common.exception;

/**
 * This class represents an exception thrown when a delete operation fails.
 */
public class DeleteException extends BaseException {

    /**
     * Constructs a new DeleteException with no detail message.
     */
    public DeleteException() {
    }

    /**
     * Constructs a new DeleteException with the specified detail message.
     * @param msg the detail message.
     */
    public DeleteException(String msg) {
        super(msg);
    }
}
