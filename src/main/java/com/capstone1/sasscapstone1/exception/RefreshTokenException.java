package com.capstone1.sasscapstone1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenException extends RuntimeException{
    private int code;
    private String message;
    public RefreshTokenException(String message){
        this.message=message;
    }
}
