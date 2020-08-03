package com.hertfordshire.restfulapi.controller.employee;

import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.access.errors.CustomBadRequestException;
import com.hertfordshire.dto.EmployeeDto;
import com.hertfordshire.dto.PatientInfoUpdate;
import com.hertfordshire.mailsender.pojo.MailPojo;
import com.hertfordshire.mailsender.service.EmailService;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.PortalUserPojo;
import com.hertfordshire.pojo.RolePojo;
import com.hertfordshire.service.psql.employee.EmployeeService;
import com.hertfordshire.service.psql.kafka.subscription.KafkaSubscriptionService;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.service.psql.patient.PatientService;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.role.RolesService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.PhoneNumberValidationUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.lhenum.GenericStatusConstant;
import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;
import com.hertfordshire.utils.pojo.ProperPhoneNumberPojo;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ProtectedEmployeeController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedEmployeeController.class.getSimpleName());

    @Autowired
    private UserService userService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private PortalAccountService portalAccountService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService;


    @Autowired
    private PatientService patientService;

    private Gson gson;

    @Value("${default.domainUrlOne}")
    private String domainUrlOne;

    @Value("${no.reply.email}")
    private String noReplyEmail;

    @Autowired
    private EmailService emailService;

    @Autowired
    private KafkaTopicService kafkaTopicService;

    @Autowired
    private KafkaSubscriptionService kafkaSubscriptionService;

    @Autowired
    private com.hertfordshire.pubsub.kafka.service.Utils utils;


    public ProtectedEmployeeController() {
        gson = new Gson();
    }

    @PostMapping("/default/employee/create")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Object> create(@Valid @RequestBody EmployeeDto employeeDto,
                                         HttpServletResponse res,
                                         HttpServletRequest request,
                                         Authentication authentication,
                                         BindingResult bindingResult) {

        ApiError apiError = null;
        PortalUser portalUser = null;
        PortalAccount portalAccount = null;
        UserDetailsDto requestPrincipal = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            requestPrincipal = userService.getPrincipal(res, request, authentication);

            PortalUser loggedUser = this.portalUserService.findPortalUserByEmail(requestPrincipal.getEmail());

            if (!TextUtils.isBlank(employeeDto.getEmail())) {
                portalUser = portalUserService.findByEmail(employeeDto.getEmail().trim());

                if (portalUser != null) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.exists", "en"),
                            false, new ArrayList<>(), portalUser);
                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                }
            }


            portalAccount = this.portalAccountService.findPortalAccountByName(PortalAccountTypeConstant.LAB.name());

            try {
                ProperPhoneNumberPojo phoneNumber = PhoneNumberValidationUtil.validatePhoneNumber(employeeDto.getPhoneNumber().trim(), employeeDto.getSelectedPhoneNumber());

                if (phoneNumber != null) {
                    employeeDto.setPhoneNumber(phoneNumber.getFormattedNumber());

                    if (!TextUtils.isBlank(employeeDto.getPhoneNumber().trim())) {
                        portalUser = portalUserService.findByPhoneNumber(employeeDto.getPhoneNumber().trim());
                        if (portalUser != null) {
                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.already.exists", "en"),
                                    false, new ArrayList<>(), portalUser);

                            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                        }
                    }
                }

                if (!TextUtils.isBlank(employeeDto.getOtherPhoneNumber())) {
                    ProperPhoneNumberPojo otherPhoneNumber = PhoneNumberValidationUtil.validatePhoneNumber(employeeDto.getOtherPhoneNumber().trim(), employeeDto.getSelectedOtherPhoneNumber());

                    if (otherPhoneNumber != null) {
                        employeeDto.setOtherPhoneNumber(otherPhoneNumber.getFormattedNumber());
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("phone.number.validation.failed", "en"),
                        false, new ArrayList<>(), null);

                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            String generatedPassword = Utils.generatePassayPassword();

            if (!TextUtils.isBlank(generatedPassword)) {


                employeeDto.setEmailOrPhoneNumberIsVerified(true);
                employeeDto.setEmailVerified(true);
                employeeDto.setUserStatus(GenericStatusConstant.ACTIVE.name());
                employeeDto.setPassword(generatedPassword);
                PortalUser newPortalUser = employeeService.createEmployee(employeeDto, null, portalAccount, loggedUser, null);

                apiError = new ApiError(HttpStatus.CREATED.value(), HttpStatus.CREATED, messageUtil.getMessage("user.creation.successful", "en"),
                        true, new ArrayList<>(), newPortalUser);

                if (newPortalUser != null) {

                    logger.info("user created");
                    MailPojo mailPojo = new MailPojo();
                    mailPojo.setFrom(this.noReplyEmail);
                    mailPojo.setTo(newPortalUser.getEmail());
                    mailPojo.setSubject("employee password");
                    mailPojo.setTemplateName("send-password-to-employee.ftl");

                    Map model = new HashMap();
                    model.put("firstName", newPortalUser.getFirstName());
                    model.put("lastName", newPortalUser.getLastName());
                    model.put("password", generatedPassword);
                    model.put("domainUrlOne", domainUrlOne);
                    mailPojo.setModel(model);

                    try {
                        emailService.sendActualEmail(mailPojo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ////// subscription
                    List<KafkaSubscription> kafkaSubscriptions = this.kafkaSubscriptionService.findAll(newPortalUser);
                    List<String> topics = new ArrayList<>();
                    for (KafkaSubscription kafkaSubscription : kafkaSubscriptions) {
                        KafkaTopicModel kafkaTopicModel =
                                this.kafkaTopicService.findById(kafkaSubscription.getKafkaTopicModel().getId());
                        topics.add(kafkaTopicModel.getName());
                    }
                    if (topics.size() > 0) {
                        this.utils.subscribe(topics, "" + newPortalUser.getId());
                    }
                    // end subscription

                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.for.employee.successful", "en"),
                            true, new ArrayList<>(), null);
                }
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.not.successful", "en"),
                        false, new ArrayList<>(), null);
            }

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @PostMapping("/default/employee/update")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Object> updatePost(@Valid @RequestBody EmployeeDto employeeDto,
                                             BindingResult bindingResult) {
        ApiError apiError = null;
        PortalUser portalUser = null;
        PortalAccount portalAccount = null;

        logger.info(this.gson.toJson(employeeDto));

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {


            try {

                ProperPhoneNumberPojo phoneNumber = PhoneNumberValidationUtil.validatePhoneNumber(employeeDto.getPhoneNumber().trim(), employeeDto.getSelectedPhoneNumber());

                if (phoneNumber != null) {
                    employeeDto.setPhoneNumber(phoneNumber.getFormattedNumber());
                }

                if (!TextUtils.isBlank(employeeDto.getOtherPhoneNumber())) {
                    ProperPhoneNumberPojo otherPhoneNumber = PhoneNumberValidationUtil.validatePhoneNumber(employeeDto.getOtherPhoneNumber().trim(), employeeDto.getSelectedOtherPhoneNumber());

                    if (otherPhoneNumber != null) {
                        employeeDto.setOtherPhoneNumber(otherPhoneNumber.getFormattedNumber());
                    }
                }

            } catch (Exception e) {

                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("phone.number.validation.failed", "en"),
                        false, new ArrayList<>(), null);

                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            try {
                portalUser = this.portalUserService.findPortalUserById(employeeDto.getEmployeeId());

                portalAccount = portalUser.getPortalAccounts().stream().findFirst().orElse(null);

                this.employeeService.updateEmployee(employeeDto, portalUser, portalAccount);

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("employee.account.update.successful", "en"),
                        true, new ArrayList<>(), null);
            } catch (Exception e) {

                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("employee.account.update.not.successful", "en"),
                        false, new ArrayList<>(), null);
            }

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }


    @GetMapping("/default/employee/update/{id}")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Object> updateGet(@PathVariable("id") Long id) {

        ApiError apiError = null;

        try {

            PortalUser portalUser = this.portalUserService.findPortalUserById(id);

            PortalUserPojo portalUserPojo = new PortalUserPojo();
            List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMappers = this.portalAccountAndPortalUserRoleMapperService
                    .findByPortalAccountCodeAndPortalUserCode(portalUser.getDefaultPortalAccountCode(), portalUser.getCode());


            List<RolePojo> rolePojoList = new ArrayList<>();
            List<String> roles = new ArrayList<>();
            for (PortalAccountAndPortalUserRoleMappper portalUserRoleMapper : portalAccountAndPortalUserRoleMappers) {
                Role role = this.rolesService.findById(portalUserRoleMapper.getRoleId());
                if (role != null) {
                    RolePojo rolePojo = new RolePojo();
                    rolePojo.setName(role.getRoleName());
                    rolePojo.setType(role.getRoleType().name());
                    rolePojoList.add(rolePojo);
                    roles.add(role.getRoleType().name());
                }
            }

            portalUserPojo.setEmail(portalUser.getEmail());
            portalUserPojo.setFirstName(portalUser.getFirstName());
            portalUserPojo.setLastName(portalUser.getLastName());


            if (!TextUtils.isBlank(portalUser.getPhoneNumber())) {
                ProperPhoneNumberPojo properPhoneNumberPojo =
                        PhoneNumberValidationUtil.validatePhoneNumber(portalUser.getPhoneNumber());

                if(properPhoneNumberPojo != null) {
                    portalUserPojo.setPhoneNumber(properPhoneNumberPojo.getNationalNumber());
                    portalUserPojo.setPhoneNumberObj(properPhoneNumberPojo);
                    logger.info("set phone number");
                }
            }


            if (!TextUtils.isBlank(portalUser.getOtherPhoneNumber())) {
                ProperPhoneNumberPojo properPhoneNumberPojo =
                        PhoneNumberValidationUtil.validatePhoneNumber(portalUser.getPhoneNumber());

                if(properPhoneNumberPojo != null) {
                    portalUserPojo.setOtherPhoneNumber(properPhoneNumberPojo.getNationalNumber());
                    portalUserPojo.setOtherPhoneNumberObj(properPhoneNumberPojo);
                    logger.info("set other phone number");
                }
            }

            portalUserPojo.setOtherName(portalUser.getOtherName());
            portalUserPojo.setAccountBlockedByAdmin(portalUser.isAccountBlockedByAdmin());
            portalUserPojo.setRolesPojo(rolePojoList);
            String[] array = roles.toArray(new String[0]);
            portalUserPojo.setRoles(array);


            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.for.employee.successful", "en"),
                    true, new ArrayList<>(), portalUserPojo);
        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("user.creation.not.successful", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @PostMapping("/default/patient/update")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<Object> updatePatientPost(@Valid @RequestBody PatientInfoUpdate employeeDto,
                                                    BindingResult bindingResult) {
        ApiError apiError = null;
        PortalUser portalUser = null;
        PortalAccount portalAccount = null;

        logger.info(this.gson.toJson(employeeDto));

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            try {
                portalUser = this.portalUserService.findPortalUserById(employeeDto.getEmployeeId());

                portalAccount = portalUser.getPortalAccounts().stream().findFirst().orElse(null);

                this.patientService.updatePatient(employeeDto, portalUser, portalAccount);

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("employee.account.update.successful", "en"),
                        true, new ArrayList<>(), null);
            } catch (Exception e) {

                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("employee.account.update.not.successful", "en"),
                        false, new ArrayList<>(), null);
            }

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

}
