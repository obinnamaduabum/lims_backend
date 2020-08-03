package com.hertfordshire.access.config.authentication_token_service;


import com.hertfordshire.model.psql.PortalUser;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthenticationTokenService {

    void sendTokenToUser(User user, HttpServletResponse response, HttpServletRequest request);

    String generateUserToken(PortalUser portalUser);

    void clearUserToken(HttpServletResponse res, HttpServletRequest request);

    PortalUser fetchUserFromRequestFormCookie(HttpServletRequest request);

    PortalUser fetchUserFromRequestFormHeader(HttpServletRequest request);
}
