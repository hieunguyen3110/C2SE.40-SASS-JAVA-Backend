package com.capstone1.sasscapstone1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginException extends RuntimeException{
    private int code;
    private String message;
    public LoginException(String message){
        this.message=message;
    }
}
