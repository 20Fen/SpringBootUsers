package com.example.demo.exception;

/**
 * Description: 自定义异常类
 */
public class CustomException extends RuntimeException {
    private static final long serialVersionUID = 12L;
    private int errorCode;
    private String message;

    public CustomException(int errorCode) {
        this.errorCode = errorCode;
    }

    public CustomException(int errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public CustomException(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
