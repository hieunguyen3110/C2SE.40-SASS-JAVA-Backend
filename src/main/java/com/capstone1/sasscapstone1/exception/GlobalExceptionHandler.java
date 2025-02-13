package com.capstone1.sasscapstone1.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(value = LoginException.class)
    public ResponseEntity<ErrorResponse> handleLoginException(LoginException exception){
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(exception.getCode()));
    }

    @ExceptionHandler(value = RegisterException.class)
    public ResponseEntity<ErrorResponse> handleRegisterException(RegisterException exception){
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(exception.getCode()));
    }

    @ExceptionHandler(value = RefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenException(RefreshTokenException exception){
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        log.error(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(exception.getCode()));
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorResponse> handleUnWantedException(Exception e){
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler({SharesException.class, UserProfileException.class, HistoryException.class})
    public ResponseEntity<ErrorResponse> handleCustomExceptions(Exception e){
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = DocumentException.class)
    public ResponseEntity<ErrorResponse> handleDocumentException(Exception e){
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = TagException.class)
    public ResponseEntity<ErrorResponse> handleTagException(Exception e){
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = FolderException.class)
    public ResponseEntity<ErrorResponse> handleFolderException(FolderException exception){
        ErrorResponse errorResponse = new ErrorResponse(exception.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(exception.getMessage()));
    }

    @ExceptionHandler(value = NotificationException.class)
    public ResponseEntity<ErrorResponse> handleNotificationException(Exception e){
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = DocumentSearchException.class)
    public ResponseEntity<ErrorResponse> handleDocumentSearchException(Exception e){
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = FollowException.class)
    public ResponseEntity<ErrorResponse> handleFollowException(Exception e){
        log.error(e.getMessage());
        ErrorResponse errorResponse= new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = SavedDocumentException.class)
    public ResponseEntity<ErrorResponse> handleSavedDocumentException(Exception e) {
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = AdminDashboardException.class)
    public ResponseEntity<ErrorResponse> handleAdminDashboardException(Exception e) {
        log.error(e.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(value = ChangePasswordException.class)
    public ResponseEntity<ErrorResponse> handleChangePasswordException(ChangePasswordException e){
        log.error(e.getMessage());
        ErrorResponse errorResponse= new ErrorResponse(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}