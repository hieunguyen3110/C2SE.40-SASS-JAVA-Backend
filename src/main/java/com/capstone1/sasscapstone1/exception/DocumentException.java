package com.capstone1.sasscapstone1.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DocumentException extends RuntimeException{
    private int code;
    private String message;
    public DocumentException(String message){
        this.message=message;
    }
}
