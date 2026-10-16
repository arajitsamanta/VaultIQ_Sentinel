package com.vaultiq.sentinel.dto;

public class InjectionBlockedException extends RuntimeException {

    public InjectionBlockedException(String message) {
        super(message);
    }
}
