package com.capstone1.sasscapstone1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class DocumentSearchException extends RuntimeException{
    public DocumentSearchException(String message) {
        super(message);
    }
}
