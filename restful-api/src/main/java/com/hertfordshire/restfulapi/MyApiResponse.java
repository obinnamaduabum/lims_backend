package com.hertfordshire.restfulapi;

import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public class MyApiResponse {

    @Autowired
    private MessageUtil messageUtil;

    public ResponseEntity unAuthenticated(NullPointerException e, ApiError apiError) {
        e.printStackTrace();

        apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("user.unauthorized", "en"),
                false, new ArrayList<>(), null);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
