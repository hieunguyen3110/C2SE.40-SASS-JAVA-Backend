package com.capstone1.sasscapstone1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegisterException extends RuntimeException{
    private int code;
    private String message;
    public RegisterException(String message){
        this.message=message;
    }
}
