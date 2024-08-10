package com.ewm.exception;

public class AlreadyExistsExeption extends RuntimeException {
    public AlreadyExistsExeption(String mes) {
        super(mes);
    }
}
