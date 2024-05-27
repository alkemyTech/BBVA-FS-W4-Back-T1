package com.magicdogs.alkywall.exceptions;

import lombok.Getter;
import lombok.Setter;

//Es el DTO del error
public class ApiExceptionHandler{
    private int statusCode;
    @Setter
    @Getter
    private String message;

    public ApiExceptionHandler(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public int getStatus() {
        return statusCode;
    }

    public void setStatus(int statusCode) {
        this.statusCode = statusCode;
    }

}

