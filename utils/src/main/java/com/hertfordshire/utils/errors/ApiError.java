package com.hertfordshire.utils.errors;

import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public class ApiError {

    private int statusCode;
    private HttpStatus status;
    private String message;
    private List<String> errors;
    private Object data;
    private boolean success;

    public ApiError(int statusCode, HttpStatus status, String message, boolean success, List<String> errors, Object data) {
        super();
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        this.errors = errors;
        this.data = data;
        this.success = success;
    }

    public ApiError(int statusCode, HttpStatus status, String message, boolean success, String error, Object data) {
        super();
        this.statusCode = statusCode;
        this.status = status;
        this.message = message;
        errors = Arrays.asList(error);
        this.data = data;
        this.success = success;
    }


    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
