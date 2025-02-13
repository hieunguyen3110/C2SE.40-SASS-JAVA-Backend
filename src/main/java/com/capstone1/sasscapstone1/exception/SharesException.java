package com.capstone1.sasscapstone1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SharesException extends RuntimeException{
    private String message;
}
