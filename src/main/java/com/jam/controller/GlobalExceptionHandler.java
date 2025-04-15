package com.jam.controller;

import com.jam.exceptions.AlreadyExistsException;
import com.jam.exceptions.NotFoundException;
import com.jam.exceptions.GlobalErrorResponse;
import com.jam.exceptions.UnableToPerformException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handlerNotFoundException(NotFoundException e){
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(HttpStatus.NOT_FOUND.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UnableToPerformException.class)
    public ResponseEntity<GlobalErrorResponse> handlerNotFoundException(UnableToPerformException e){
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<GlobalErrorResponse> handlerNotFoundException(AlreadyExistsException e){
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(HttpStatus.METHOD_NOT_ALLOWED.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorResponse);
    }
}
