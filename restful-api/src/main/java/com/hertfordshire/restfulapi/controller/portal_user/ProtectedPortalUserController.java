package com.hertfordshire.restfulapi.controller.portal_user;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dto.*;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.*;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.portaluser.PortalUserServiceImp;
import com.hertfordshire.service.psql.role.RolesService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.PhoneNumberValidationUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.errors.MyApiResponse;
import com.hertfordshire.utils.lhenum.GenericStatusConstant;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;
import com.hertfordshire.utils.pojo.ProperPhoneNumberPojo;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProtectedPortalUserController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedPortalUserController.class.getSimpleName());

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private PortalUserServiceImp portalUserServiceImp;

    @Autowired
    private PortalUserDao portalUserDao;


    @Autowired
    private RolesService rolesService;

    @Autowired
    private MessageUtil messageUtil;

    private Gson gson;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao;


    @Autowired
    private MyApiResponse myApiResponse;

    public ProtectedPortalUserController() {
        this.gson = new Gson();
    }

    @PutMapping("/auth/portal_user/profile/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody Object portalUserDto,
                                               HttpServletResponse res,
                                               HttpServletRequest request,
                                               Authentication authentication) {
        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;
        String lang = "en";

        try {
            String tmpLang = Utils.fetchLanguageFromCookie(request);
            if (tmpLang != null) {
                lang = tmpLang;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (portalUserDto == null) {

            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("settings.update.not.successfully", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {

            requestPrincipal = userService.getPrincipal(res, request, authentication);

            String email = requestPrincipal.getEmail();

            PortalUser portalUser = portalUserService.findPortalUserByEmail(email);


            if (portalUser != null) {

                try {

                    JsonParser parser = new JsonParser();

                    JsonObject jsonObject = parser.parse(gson.toJson(portalUserDto)).getAsJsonObject();


                    if (portalUserServiceImp.doesFieldExist(jsonObject, "firstName")) {
                        portalUser.setFirstName(portalUserServiceImp.getStringField(jsonObject, "firstName"));
                    }

                    if (portalUserServiceImp.doesFieldExist(jsonObject, "lastName")) {
                        portalUser.setLastName(portalUserServiceImp.getStringField(jsonObject, "lastName"));
                    }

                    if (portalUserServiceImp.doesFieldExist(jsonObject, "otherName")) {
                        portalUser.setLastName(portalUserServiceImp.getStringField(jsonObject, "otherName"));
                    }


                    if (portalUserServiceImp.doesFieldExist(jsonObject, "otherPhoneNumber")) {
                        portalUser.setOtherPhoneNumber(portalUserServiceImp.getStringField(jsonObject, "otherPhoneNumber"));
                    }

                    portalUserDao.save(portalUser);


                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("profile.update.successful", "en"),
                            true, new ArrayList<>(), null);

                } catch (Exception e) {
                    e.printStackTrace();

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("profile.update.failed", "en"),
                            false, new ArrayList<>(), null);
                }

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

            } else {

                apiError = new ApiError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED, messageUtil.getMessage("user.unauthorized", "en"),
                        false, new ArrayList<>(), null);

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
            }

        } catch (NullPointerException e) {

            return new com.hertfordshire.utils.errors.MyApiResponse().unAuthorizedResponse();
        }
    }


    @GetMapping("/auth/portal_user/profile/update")
    public ResponseEntity<?> fetchUserProfile(HttpServletResponse res,
                                              HttpServletRequest request,
                                              Authentication authentication) {
        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;


        String lang = "en";

        try {
            String tmpLang = Utils.fetchLanguageFromCookie(request);
            if (tmpLang != null) {
                lang = tmpLang;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            requestPrincipal = userService.getPrincipal(res, request, authentication);

            Long id = requestPrincipal.getId();

            PortalUser portalUser = portalUserService.findPortalUserById(id);


            if (portalUser != null) {

                PortalUserPojo portalUserPojo = new PortalUserPojo();
                portalUserPojo.setEmail(portalUser.getEmail());
                portalUserPojo.setFirstName(portalUser.getFirstName());
                portalUserPojo.setLastName(portalUser.getLastName());


                if (!TextUtils.isBlank(portalUser.getPhoneNumber())) {
                    ProperPhoneNumberPojo properPhoneNumberPojo =
                            PhoneNumberValidationUtil.validatePhoneNumber(portalUser.getPhoneNumber());

                    portalUserPojo.setPhoneNumber(portalUser.getPhoneNumber());
                    portalUserPojo.setPhoneNumberObj(properPhoneNumberPojo);
                    logger.info("set phone number");
                }


                if (!TextUtils.isBlank(portalUser.getOtherPhoneNumber())) {
                    ProperPhoneNumberPojo properPhoneNumberPojo =
                            PhoneNumberValidationUtil.validatePhoneNumber(portalUser.getPhoneNumber());

                    portalUserPojo.setOtherPhoneNumber(portalUser.getOtherPhoneNumber());
                    portalUserPojo.setOtherPhoneNumberObj(properPhoneNumberPojo);
                    logger.info("set other phone number");
                }

                portalUserPojo.setOtherName(portalUser.getOtherName());


                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.unauthorized", lang),
                        true, new ArrayList<>(), portalUserPojo);

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

            } else {

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.unauthorized", lang),
                        false, new ArrayList<>(), null);

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
            }

        } catch (NullPointerException e) {

            return new MyApiResponse().unAuthorizedResponse();
        }
    }


    @PutMapping("/auth/portal_user/employee/update")
    public ResponseEntity<?> editEmployee(@RequestBody Object portalUserDto,
                                          HttpServletResponse res,
                                          HttpServletRequest request,
                                          Authentication authentication) {

        ApiError apiError = null;
        PortalUser portalUser = null;
        String portalAccountCode = null;
        String portalUserCode = null;
        UserDetailsDto requestPrincipal = null;

        JsonParser parser = new JsonParser();

        if (portalUserDto == null) {


            apiError = new ApiError(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, messageUtil.getMessage("settings.update.not.successfully", "en"),
                    false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

        } else {

            requestPrincipal = this.userService.getPrincipal(res, request, authentication);
            portalUser = this.portalUserService.findPortalUserByEmail(requestPrincipal.getEmail());

            List<Role> roleList = this.getRoles(portalUser.getDefaultPortalAccountCode(), portalUser.getCode());
//            Role ceo = this.rolesService.findByRoleType(RoleTypeConstant.CEO);

            Role superAdmin = this.rolesService.findByRoleType(RoleTypeConstant.SUPER_ADMIN);


            if (!roleList.contains(superAdmin)) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sorry.you.are.not.allowed.to.perform.this.action", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
            }


            JsonObject jsonObject = parser.parse(gson.toJson(portalUserDto)).getAsJsonObject();

            if (portalUserServiceImp.doesFieldExist(jsonObject, "code")) {

                portalUserCode = portalUserServiceImp.getStringField(jsonObject, "code");
                portalUser = portalUserService.findByPortalUserCode(portalUserServiceImp.getStringField(jsonObject, "code"));

            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.code.not.found", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            if (portalUserServiceImp.doesFieldExist(jsonObject, "portalAccountCode")) {
                portalAccountCode = portalUserServiceImp.getStringField(jsonObject, "portalAccountCode");
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.account.code.not.found", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

            }
        }

        try {

            JsonObject jsonObject = parser.parse(gson.toJson(portalUserDto)).getAsJsonObject();


            if (portalUser != null) {

                try {

                    if (portalUserServiceImp.doesFieldExist(jsonObject, "code")) {
                        portalUser = portalUserService.findByPortalUserCode(portalUserServiceImp.getStringField(jsonObject, "code"));
                    }

                    if (portalUserServiceImp.doesFieldExist(jsonObject, "firstName")) {
                        portalUser.setFirstName(portalUserServiceImp.getStringField(jsonObject, "firstName"));
                    }

                    if (portalUserServiceImp.doesFieldExist(jsonObject, "lastName")) {
                        portalUser.setLastName(portalUserServiceImp.getStringField(jsonObject, "lastName"));
                    }

                    if (portalUserServiceImp.doesFieldExist(jsonObject, "otherName")) {
                        portalUser.setLastName(portalUserServiceImp.getStringField(jsonObject, "otherName"));
                    }

                    if (portalUserServiceImp.doesFieldExist(jsonObject, "phoneNumber")) {
                        portalUser.setPhoneNumber(portalUserServiceImp.getStringField(jsonObject, "phoneNumber"));
                    }

                    List<PortalAccountAndPortalUserRoleMappper> portalUserRoleMapppers = this.portalAccountAndPortalUserRoleMapperService.findByPortalAccountCodeAndPortalUserCode(portalAccountCode, portalUserCode);

                    if (portalUserServiceImp.doesFieldExist(jsonObject, "roles")) {
                        JsonArray jsonArray = portalUserServiceImp.getArrayField(jsonObject, "roles");

                        portalUserRoleMapppers.forEach(portalAccountAndPortalUserRoleMappper -> {
                            this.portalAccountAndPortalUserRoleMapperService.delete(portalAccountAndPortalUserRoleMappper);
                        });

                        String tempNames = jsonArray.toString();
                        String[] stringArray = tempNames.substring(1, tempNames.length() - 1).split(","); //remove [ and ] , then split by ','

                        logger.info(this.gson.toJson(stringArray));

                        for (int i = 0; i < stringArray.length; i++) {

                            String roleString = stringArray[i].toUpperCase();
                            String stripped = roleString.replace("\"", "");
                            Role role = this.rolesService.findByRoleType(RoleTypeConstant.valueOf(stripped));
                            PortalAccountAndPortalUserRoleMappper portalAccountAndPortalUserRoleMappper = new PortalAccountAndPortalUserRoleMappper();
                            portalAccountAndPortalUserRoleMappper.setPortalAccountCode(portalAccountCode);
                            portalAccountAndPortalUserRoleMappper.setPortalUserCode(portalUserCode);
                            portalAccountAndPortalUserRoleMappper.setRoleId(role.getId());
                            portalAccountAndPortalUserRoleMappper.setRoleName(role.getRoleName());
                            this.portalAccountAndPortalUserRoleMapperDao.save(portalAccountAndPortalUserRoleMappper);
                            // logger.info(stringArray[i]);
                        }
                    }

                    portalUserDao.save(portalUser);


                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("profile.update.successful", "en"),
                            true, new ArrayList<>(), null);

                } catch (Exception e) {
                    e.printStackTrace();

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("profile.update.failed", "en"),
                            false, new ArrayList<>(), null);
                }

            } else {

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.deactivation.failed", "en"),
                        false, new ArrayList<>(), null);
            }

        } catch (NullPointerException e) {

            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.deactivation.failed", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @PostMapping("/auth/portal_user/deactivate")
    public ResponseEntity<?> deactivateUser(@Valid @RequestBody DeactivateEmployeeDto employeeDto,
                                            HttpServletResponse res,
                                            HttpServletRequest request,
                                            Authentication authentication,
                                            BindingResult bindingResult) {
        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            try {

                requestPrincipal = userService.getPrincipal(res, request, authentication);

                PortalUser loggedUser = this.portalUserService.findPortalUserByEmail(requestPrincipal.getEmail());

                List<Role> roleList = this.getRoles(loggedUser.getDefaultPortalAccountCode(), loggedUser.getCode());
//                Role ceo = this.rolesService.findByRoleType(RoleTypeConstant.CEO);

                Role superAdmin = this.rolesService.findByRoleType(RoleTypeConstant.SUPER_ADMIN);


                if (!roleList.contains(superAdmin)) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("sorry.you.are.not.allowed.to.perform.this.action", "en"),
                            false, new ArrayList<>(), null);
                    return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
                }


                String email = requestPrincipal.getEmail();

                PortalUser portalUser = portalUserService.findByPortalUserCode(employeeDto.getCode());

                if (portalUser != null) {

                    if (portalUser.getEmail().equals(email)) {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.cannot.deactivate.themselves", "en"),
                                false, new ArrayList<>(), null);

                    } else {

                        portalUser.setEmailVerified(false);
                        portalUser.setLockedOut(true);
                        portalUser.setUserStatus(GenericStatusConstant.INACTIVE);
                        portalUserDao.save(portalUser);
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.deactivated.successful", "en"),
                                true, new ArrayList<>(), null);
                    }

                } else {

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.deactivation.failed", "en"),
                            false, new ArrayList<>(), null);

                }

            } catch (NullPointerException e) {

                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.deactivation.failed", "en"),
                        false, new ArrayList<>(), null);
            }

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @PostMapping("/auth/portal_user/employee/view-all")
    public ResponseEntity<?> fetchPortalUserEmployee(@RequestParam("page") int page,
                                                     @RequestParam("size") int size,
                                                     @RequestBody EmployeeSearchDto employeeSearchDto) {
        ApiError apiError = null;

        if (size == 0) {
            size = 10;
        }

        try {

            Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("dateCreated").descending());

            PaginationResponsePojo paginationResponsePojo = this.portalUserService.findAllByPortalUsers(employeeSearchDto, sortedByDateCreated);
            Long total = this.portalUserService.countAllByPortalUsers(employeeSearchDto, sortedByDateCreated);
            paginationResponsePojo.setLength(total);


            //logger.info(this.gson.toJson(paginationResponsePojo));

            logger.info("wwww");

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("list.of.users", "en"),
                    true, new ArrayList<>(), paginationResponsePojo);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        } catch (Exception e) {

            logger.info("xxxwwww");

            return myApiResponse.internalServerErrorResponse();

            // e.printStackTrace();
        }

    }

    @PostMapping("/auth/portal_user/edit/for_admin/view")
    public ResponseEntity<?> fetchPortalUserEmployee(@RequestBody EditPortalUserDto editPortalUserDto,
                                                     BindingResult bindingResult) {

        ApiError apiError = null;
        PortalUser portalUser = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            try {

                portalUser = this.portalUserService.findByCode(editPortalUserDto.getPortalUserCode());

                List<Role> roles = this.rolesService.findRoleByPortalAccountAndPortalUser(editPortalUserDto.getPortalAccountCode(), editPortalUserDto.getPortalUserCode());

                List<RolePojo> rolePojos = new ArrayList<>();

                for (Role role : roles) {
                    RolePojo pojo = new RolePojo();
                    pojo.setName(role.getRoleName());
                    pojo.setType(role.getRoleType().toString());
                    rolePojos.add(pojo);
                }

                EditPortalUserPojo portalUserPojo = new EditPortalUserPojo();
                portalUserPojo.setPhoneNumber(portalUser.getPhoneNumber());
                portalUserPojo.setOtherName(portalUser.getOtherName());
                portalUserPojo.setLastName(portalUser.getLastName());
                portalUserPojo.setFirstName(portalUser.getFirstName());
                portalUserPojo.setEmail(portalUser.getEmail());
                portalUserPojo.setRoles(rolePojos);


                String swissNumberStr = portalUser.getPhoneNumber();
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
                Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, "");


                InternationalPhoneNumberPojo internationalPhoneNumberPojo = new InternationalPhoneNumberPojo();
                internationalPhoneNumberPojo.setCountry(String.valueOf(swissNumberProto.getCountryCode()));
                internationalPhoneNumberPojo.setPhoneCountryCode(String.valueOf(swissNumberProto.getCountryCode()));
                internationalPhoneNumberPojo.setPhoneNumber(String.valueOf(swissNumberProto.getNationalNumber()));
                portalUserPojo.setInternationalPhoneNumberPojo(internationalPhoneNumberPojo);


                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("profile.update.failed", "en"),
                        true, new ArrayList<>(), portalUserPojo);
            } catch (Exception e) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("profile.update.failed", "en"),
                        false, new ArrayList<>(), null);
                e.printStackTrace();
            }
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @PostMapping("/auth/portal_user/edit/change_password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDto changePasswordDto,
                                            HttpServletResponse response,
                                            HttpServletRequest request,
                                            Authentication authentication,
                                            BindingResult bindingResult) {

        ApiError apiError = null;
        PortalUser portalUser = null;
        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            UserDetailsDto requestPrincipal = null;

            try {

                requestPrincipal = userService.getPrincipal(response, request, authentication);
                portalUser = this.portalUserService.findPortalUserByEmail(requestPrincipal.email);
                boolean result = this.portalUserService.changePassword(changePasswordDto, portalUser);
                if (result) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.password.change.successful", "en"),
                            true, new ArrayList<>(), null);
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.password.change.failed", "en"),
                            false, new ArrayList<>(), null);
                }

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

            } catch (NullPointerException e) {

                return new MyApiResponse().unAuthorizedResponse();
            }
        }
    }

    @PostMapping("/auth/portal_user/edit/change_password/matches_old")
    public ResponseEntity<?> matchesOldPassword(@RequestBody CheckOldPasswordDto checkOldPasswordDto,
                                                HttpServletResponse response,
                                                HttpServletRequest request,
                                                Authentication authentication,
                                                BindingResult bindingResult) {

        ApiError apiError = null;
        PortalUser portalUser = null;
        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            UserDetailsDto requestPrincipal = null;

            try {

                requestPrincipal = userService.getPrincipal(response, request, authentication);
                portalUser = this.portalUserService.findByEmail(requestPrincipal.email);
                boolean result = this.portalUserService.checkIfPasswordsmatch(checkOldPasswordDto, portalUser);
                if (result) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.password.does.match", "en"),
                            true, new ArrayList<>(), null);
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.password.does.not.match", "en"),
                            false, new ArrayList<>(), null);
                }

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

            } catch (NullPointerException e) {
                return new MyApiResponse().unAuthorizedResponse();
            }
        }
    }


    @GetMapping("/auth/portal_user/{id}")
    public ResponseEntity<?> findUser(@PathVariable("id") Long id) {

        ApiError apiError = null;
        PortalUser portalUser = null;

        try {

            // starter
            portalUser = this.portalUserService.findById(id);

            PortalUserPojo portalUserPojo = new PortalUserPojo();

            if(!TextUtils.isBlank(portalUser.getEmail())) {
                portalUserPojo.setEmail(portalUser.getEmail());
            }

            portalUserPojo.setFirstName(portalUser.getFirstName());

            portalUserPojo.setLastName(portalUser.getFirstName());

            if(!TextUtils.isBlank(portalUser.getOtherName())) {
                portalUserPojo.setOtherName(portalUser.getOtherName());
            }

            portalUserPojo.setPhoneNumber(portalUser.getPhoneNumber());


            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.found", "en"),
                    true, new ArrayList<>(), portalUserPojo);

        } catch (NullPointerException e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.info.not.found", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    private List<Role> getRoles(String portalAccountCode, String portalUserCode) {
        return this.rolesService.findRoleByPortalAccountAndPortalUser(portalAccountCode, portalUserCode);
    }
}
