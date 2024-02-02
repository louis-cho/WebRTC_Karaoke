package com.ssafy.server.exception;

import com.ssafy.server.exception.feed.FeedNotFoundException;
import com.ssafy.server.exception.like.LikeNotCreatedException;
import com.ssafy.server.exception.like.LikeNotUpdatedException;
import com.ssafy.server.exception.request.InvalidParameterException;
import com.ssafy.server.exception.user.*;
import com.ssafy.server.like.model.Likes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidUserIdException.class)
    public ResponseEntity<String> handleInvalidUserIdException(InvalidUserIdException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(FeedNotFoundException.class)
    public ResponseEntity<String> handlePostNotFoundException(FeedNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<String> handleInvalidParameterException(InvalidParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<String> handleInvalidCredentialException(InvalidCredentialException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(UserNotCreatedException.class)
    public ResponseEntity<String> handleUserNotCreatedException(InvalidCredentialException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(LikeNotCreatedException.class)
    public Likes handleLikeNotCreatedException(LikeNotUpdatedException ex) {
        log.error(ex.getMessage());
        return null;
    }
}
