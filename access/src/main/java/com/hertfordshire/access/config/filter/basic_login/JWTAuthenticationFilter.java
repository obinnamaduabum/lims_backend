package com.hertfordshire.access.config.filter.basic_login;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hertfordshire.access.config.authentication_token_service.AuthenticationTokenService;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.dto.LoginDto;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.Utils;
import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static com.hertfordshire.utils.Utils.checkIfValidNumber;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

   @Autowired
   private AuthenticationTokenService authenticationTokenService;

   private String loginEmail;

   @Autowired
   private MessageUtil messageUtil;

   private Gson gson;

   @Autowired
   private PortalUserService portalUserService;

   @Autowired
   private PortalUserDao portalUserDao;

   public JWTAuthenticationFilter() {
       this.gson = new Gson();
   }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {

            LoginDto loginDto = new ObjectMapper().readValue(req.getInputStream(), LoginDto.class);
            this.loginEmail = loginDto.getEmail();

            logger.info("got in: ");

            Authentication authentication =  super.getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail().trim(),
                            loginDto.getPassword().trim(),
                            new ArrayList<>()));


            logger.info("authenticated: " + authentication.isAuthenticated());

            PortalUser portalUser = null;
            if (EmailValidator.getInstance(true).isValid(loginEmail.toLowerCase())) {
                portalUser = portalUserService.findPortalUserByEmail(loginEmail.toLowerCase());
            } else if(checkIfValidNumber(loginEmail.toLowerCase().trim())) {
                portalUser = portalUserService.findPortalUserByPhoneNumber(loginEmail.toLowerCase().trim());
            }


            logger.info(portalUser.getCode());

            if(portalUser != null) {

                logger.info("authenticated: " + authentication.isAuthenticated());

                if(!portalUser.isTwoFactor()) {

                    return authentication;

                } else {

                    logger.info("authenticated: " + authentication.isAuthenticated());

                    if (authentication.isAuthenticated()) {

                        logger.info("xcx: " +loginDto.getTwoFactorCode());

                        boolean successful = new Utils().verify2Fa(portalUser.getSecret(), loginDto.getTwoFactorCode());
                        if (successful) {
                            logger.info(messageUtil.getMessage("two.factor.code.verified", "en"));
                            return authentication;
                        } else {
                            String message = messageUtil.getMessage("two.factor.code.not-valid.verified", "en");
                            throw new BadCredentialsException(message);
                        }

                    } else {
                        String message = "Bad credentials";
                        throw new BadCredentialsException(message);
                    }
                }
            }

            return authentication;

        } catch (IOException e) {
            e.printStackTrace();
            logger.info("qqqq: "+e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        authenticationTokenService.sendTokenToUser((User) auth.getPrincipal(), res, req);
    }


    @Override
    @Autowired
    public void setAuthenticationManager(AuthenticationManager authenticationManager){
        super.setAuthenticationManager(authenticationManager);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {

        ApiError  apiError = null;
        PortalUser portalUser = null;

        logger.info(failed.getMessage());

        if (EmailValidator.getInstance(true).isValid(loginEmail.toLowerCase())) {
            portalUser = portalUserService.findPortalUserByEmail(loginEmail.toLowerCase());
        } else if(checkIfValidNumber(loginEmail.toLowerCase().trim())) {
            portalUser = portalUserService.findPortalUserByPhoneNumber(loginEmail.toLowerCase().trim());
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (failed.getMessage().equalsIgnoreCase("User is disabled")) {

            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.user.is.deactivated", "en"),
                    false, new ArrayList<>(), null);
        } else if (failed.getMessage().equalsIgnoreCase("User account has expired")) {

            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.message.expired", "en"),
                    false, new ArrayList<>(), null);
        } else if (failed.getMessage().equalsIgnoreCase("Bad credentials")) {

            if(portalUser != null) {
                portalUser.setFailedLoginAttempts(portalUser.getFailedLoginAttempts() + 1);
                this.portalUserDao.save(portalUser);
            }
            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.message.bad.credentials", "en"),
                    false, new ArrayList<>(), null);
        } else if(failed.getMessage().equalsIgnoreCase("blocked")) {
            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.message.locked.out.credentials", "en"),
                    false, new ArrayList<>(), null);
        } else if(failed.getMessage().equalsIgnoreCase("blockedByAdmin")) {
            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("auth.message.blocked.by.admin", "en"),
                    false, new ArrayList<>(), null);
        } else if(failed.getMessage().equalsIgnoreCase("Two factor code not verified")) {
            logger.info("calling");
            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("two.factor.code.not-valid.verified", "en"),
                    false, new ArrayList<>(), null);
        }

        response.getWriter().print(this.gson.toJson(apiError));
        response.getWriter().flush();
    }
}
