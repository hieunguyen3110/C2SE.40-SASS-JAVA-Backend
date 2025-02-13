package com.capstone1.sasscapstone1.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HistoryException extends RuntimeException{
    private String message;
}
