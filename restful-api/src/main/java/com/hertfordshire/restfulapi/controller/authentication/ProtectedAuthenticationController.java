package com.hertfordshire.restfulapi.controller.authentication;


import com.hertfordshire.access.config.authentication_service.AuthenticationService;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.dto.PortalAccountDescriptionDto;
import com.hertfordshire.pubsub.redis.model.PortalUserModel;
import com.hertfordshire.pubsub.redis.pojo.PortalAccountDescriptionPojo;
import com.hertfordshire.service.firebase.FirebaseService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProtectedAuthenticationController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedAuthenticationController.class);

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private com.hertfordshire.pubsub.redis.service.portal_user.PortalUserService redisPortalUserService;

    @Autowired
    private UserService userService;

    @GetMapping("/auth/me")
    public ResponseEntity<?> me(HttpServletResponse response, HttpServletRequest request, Authentication authentication) {

        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;

        try {


            // RSAKeyPairGeneratorUtil.doWriteToFile();
            //String encryptedData = CryptographyUtil.encryptThisData("cool girls");
            //CryptographyUtil.decryptThisData(encryptedData);

            String formOfIdentification = userService.fetchFormOfIdentification();

           // logger.info("phonenumber: " + formOfIdentification);

            if (formOfIdentification != null) {

                try {

                    PortalUserModel portalUserModel = this.redisPortalUserService.fetchPortalUser(formOfIdentification);

                    if (portalUserModel != null) {
                        firebaseService.generateFireBaseCustomToken(portalUserModel.getId(), request, response);
                        apiError = new ApiError(
                                HttpStatus.OK.value(),
                                HttpStatus.OK,
                                messageUtil.getMessage("user.login.successful", "en"),
                                true, new ArrayList<>(), portalUserModel);
                        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
                    } else {

                      //  logger.info("redis is empty 1");
                        requestPrincipal = this.fetchUserInfoFromDb(response, request, authentication);
                        //    }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {

               // logger.info("redis is empty 2");

                requestPrincipal = this.fetchUserInfoFromDb(response, request, authentication);

            }

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.login.successful", "en"),
                    true, new ArrayList<>(), requestPrincipal);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {

            e.printStackTrace();

            apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("user.unauthorized", "en"),
                    false, new ArrayList<>(), null);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

    }


    private UserDetailsDto fetchUserInfoFromDb(HttpServletResponse response, HttpServletRequest request, Authentication authentication) {

        UserDetailsDto requestPrincipal = null;

        requestPrincipal = userService.getPrincipal(response, request, authentication);

        if (requestPrincipal == null) {
            throw new RuntimeException();
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            authenticationService.setPrincipal(new User(((UserDetails) principal).getUsername(),
                    ((UserDetails) principal).getPassword(), new ArrayList<>()), response, request);

            firebaseService.generateFireBaseCustomToken(requestPrincipal.getId(), request, response);
            this.saveForRedis(requestPrincipal);

        } else {
            throw new RuntimeException();
        }

        return requestPrincipal;
    }


    private void saveForRedis(UserDetailsDto requestPrincipal) {

        try {

            PortalUserModel portalUserModel = new PortalUserModel();
            portalUserModel.setId(requestPrincipal.getId());
            portalUserModel.setAccountNonLocked(requestPrincipal.isAccountNonLocked());
            portalUserModel.setEmail(requestPrincipal.getEmail());
            portalUserModel.setFirstName(requestPrincipal.getFirstName());
            portalUserModel.setLastName(requestPrincipal.getLastName());
            portalUserModel.setOtherName(requestPrincipal.getOtherName());
            portalUserModel.setPhoneNumber(requestPrincipal.getPhoneNumber());
            portalUserModel.setRoles(requestPrincipal.getRoles());

            List<PortalAccountDescriptionDto> portalAccountDescriptionDtos = requestPrincipal.getPortalAccountDescriptionDtoList();

            List<PortalAccountDescriptionPojo> portalAccountDescriptionPojos = new ArrayList<>();
            for (PortalAccountDescriptionDto portalAccountDescriptionDto : portalAccountDescriptionDtos) {
                PortalAccountDescriptionPojo portalAccountDescriptionPojo = new PortalAccountDescriptionPojo();

                if (portalAccountDescriptionDto.getPortalAccountTypeConstant() != null) {
                    if (!TextUtils.isBlank(portalAccountDescriptionDto.getPortalAccountTypeConstant().name())) {
                        portalAccountDescriptionPojo.setPortalAccountTypeConstant(portalAccountDescriptionDto.getPortalAccountTypeConstant().name());

                    }
                }

                if (portalAccountDescriptionDto.getPortalUserTypeConstant() != null) {
                    if (!TextUtils.isBlank(portalAccountDescriptionDto.getPortalUserTypeConstant().name())) {
                        portalAccountDescriptionPojo.setPortalUserTypeConstant(portalAccountDescriptionDto.getPortalUserTypeConstant().name());
                    }
                }
                portalAccountDescriptionPojo.setRoleName(portalAccountDescriptionDto.getRoleName());
                portalAccountDescriptionPojos.add(portalAccountDescriptionPojo);
            }

            portalUserModel.setPortalAccountDescriptionDtoList(portalAccountDescriptionPojos);

            this.redisPortalUserService.savePortalUser(portalUserModel);
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
