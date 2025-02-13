package com.capstone1.sasscapstone1.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagException extends RuntimeException{
    private int code;
    private String message;
    public TagException(String message){
        this.message=message;
    }
}
