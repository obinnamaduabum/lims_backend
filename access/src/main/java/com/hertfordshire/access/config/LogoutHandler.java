package com.hertfordshire.access.config;


import com.hertfordshire.access.config.authentication_token_service.AuthenticationTokenService;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.service.firebase.FirebaseService;
import com.hertfordshire.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Component
public class LogoutHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {

//    @Autowired
//    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private UserService userService;


    @Value("${fire.base.device.token}")
    private String fireBaseDeviceToken;

    private static final Logger logger = LoggerFactory.getLogger(LogoutHandler.class);


    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        Principal principal = request.getUserPrincipal();

        if (principal instanceof UserDetails) {
           // System.out.println(((UserDetails) principal).getUsername());
            String formOfIdentification = ((UserDetails) principal).getUsername();
            //this.redisPortalUserService.delete(formOfIdentification);
        }

        authenticationTokenService.clearUserToken(response, request);

        // clear firebase cookies
        firebaseService.clearFireBaseCookies(response);

       // this.unsubscribeFromFCM(authentication, request, response);

        try {
            UserDetailsDto requestPrincipal = userService.getPrincipal(response, request, authentication);
            String cookieValue = Utils.fetchCookie(request, this.fireBaseDeviceToken);
            this.firebaseService.unsubscribeFromFCM(requestPrincipal.getId(), request, response, cookieValue);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        Cookie cookie = new Cookie(this.fireBaseDeviceToken, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        response.setStatus(HttpStatus.OK.value());
    }

}
