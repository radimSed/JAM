package com.jam.exceptions;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String errorString){
        super(errorString);
    }
}
