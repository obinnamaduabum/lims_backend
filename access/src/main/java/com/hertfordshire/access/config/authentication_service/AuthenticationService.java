package com.hertfordshire.access.config.authentication_service;

import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationService {

        void setPrincipal(User user, HttpServletResponse response, HttpServletRequest httpServletRequest);
}
