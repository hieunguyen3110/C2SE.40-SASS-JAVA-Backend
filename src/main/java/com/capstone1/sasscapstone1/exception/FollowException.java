package com.capstone1.sasscapstone1.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class FollowException extends RuntimeException {
    public FollowException(String message) {
        super(message);
    }
}
