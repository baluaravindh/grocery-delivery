package com.balu.grocery_delivery.exception;

public class DuplicateUserFoundException extends RuntimeException {
    public DuplicateUserFoundException(String msg) {
        super(msg);
    }
}
