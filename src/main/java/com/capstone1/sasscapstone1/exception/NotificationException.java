package com.capstone1.sasscapstone1.exception;

public class NotificationException extends RuntimeException{
    private String message;
    public NotificationException(String message){
        this.message=message;
    }
}
