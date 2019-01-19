package com.devnock.basearchitecture.utils.entity;

public class ServerErrorEntity {

    // TODO:
    public static final int INVALID_TOKEN = 999;
    private String message;
    private int errorCode;

    public ServerErrorEntity(){

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
