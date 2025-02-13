package com.capstone1.sasscapstone1.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordException extends RuntimeException{
    private int code;
    private String message;
    public ChangePasswordException(String message){
        super(message);
    }
}
