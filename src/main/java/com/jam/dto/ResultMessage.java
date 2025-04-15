package com.jam.dto;

public class ResultMessage {
    private String message;

    public ResultMessage(String message){
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
