package com.jam.exceptions;

public class UnableToPerformException extends RuntimeException{
    public UnableToPerformException(String errorString){
        super(errorString);
    }
}
