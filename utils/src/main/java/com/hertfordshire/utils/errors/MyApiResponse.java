package com.hertfordshire.utils.errors;

import com.hertfordshire.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class MyApiResponse {

    @Autowired
    private MessageUtil messageUtil;

//    @Autowired
//    public MyApiResponse(MessageUtil messageUtil) {
//        this.messageUtil = messageUtil;
//    }

    public MyApiResponse() {
    }


    public ResponseEntity<Object> unAuthorizedResponse() {
        ApiError apiError;
        apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("user.unauthorized", "en"),
                false, new ArrayList<>(), null);
        return response(apiError);
    }

    public ResponseEntity<Object> successful(Object inputData, String message) {
        ApiError apiError;
        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage(message, "en"),
                true, new ArrayList<>(), inputData);
        return response(apiError);
    }


    public ResponseEntity<Object> badRequest(Object inputData, String message) {
        ApiError apiError;
        apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage(message, "en"),
                false, new ArrayList<>(), inputData);
        return response(apiError);
    }

    public ResponseEntity<Object> notSuccessful(Object inputData, String message) {
        ApiError apiError;
        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage(message, "en"),
                false, new ArrayList<>(), inputData);
        return response(apiError);
    }


    public ResponseEntity<Object> successfullyCreated(Object inputData, String message) {
        ApiError apiError;
        apiError = new ApiError(HttpStatus.CREATED.value(), HttpStatus.CREATED, messageUtil.getMessage(message, "en"),
                true, new ArrayList<>(), inputData);
        return response(apiError);
    }

    public ResponseEntity<Object> internalServerErrorResponse() {

        ApiError apiError;
        System.out.println(messageUtil.getMessage("server.error", "en"));
        apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                "bbb",
                false, new ArrayList<>(), null);

        return response(apiError);
    }


    private ResponseEntity<Object> response(ApiError apiError) {
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
