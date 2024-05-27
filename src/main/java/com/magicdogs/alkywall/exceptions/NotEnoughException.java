package com.magicdogs.alkywall.exceptions;

public class NotEnoughException extends RuntimeException {
    public NotEnoughException(String message) {
        super(message);
    }
}