package com.hertfordshire.access.config.authentication_service;

import com.hertfordshire.access.config.authentication_token_service.AuthenticationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationTokenService authenticationTokenService;


    @Override
    public void setPrincipal(User user, HttpServletResponse response, HttpServletRequest request) {
        if (user == null) {
            authenticationTokenService.clearUserToken(response , request);
            return;
        }
        authenticationTokenService.sendTokenToUser(user, response, request);
    }
}
