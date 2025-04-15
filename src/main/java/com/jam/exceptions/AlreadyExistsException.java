package com.jam.exceptions;

public class AlreadyExistsException extends RuntimeException{
    public AlreadyExistsException(String errorString){
        super(errorString);
    }
}
