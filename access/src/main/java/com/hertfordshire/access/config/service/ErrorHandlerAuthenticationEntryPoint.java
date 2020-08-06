package com.hertfordshire.access.config.service;

import com.google.gson.Gson;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.MessageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

@Component
public class ErrorHandlerAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    private static final long serialVersionUID = -8970718410437077606L;

    private Gson gson;

    @Autowired
    private MessageUtil messageUtil;

    public ErrorHandlerAuthenticationEntryPoint() {
        this.gson = new Gson();
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException failed) throws IOException {
        ApiError apiError = null;
        if (failed.getMessage().equalsIgnoreCase("User is disabled")) {

            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.user.is.deactivated", "en"),
                    false, new ArrayList<>(), null);
        } else if (failed.getMessage().equalsIgnoreCase("User account has expired")) {

            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.message.expired", "en"),
                    false, new ArrayList<>(), null);
        } else if (failed.getMessage().equalsIgnoreCase("Bad credentials")) {

            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.message.bad.credentials", "en"),
                    false, new ArrayList<>(), null);
        } else if (failed.getMessage().equalsIgnoreCase("blocked")) {
            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.message.locked.out.credentials", "en"),
                    false, new ArrayList<>(), null);
        }

        response.getWriter().print(this.gson.toJson(apiError));
        response.getWriter().flush();

    }
}