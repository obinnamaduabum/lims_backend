package com.hertfordshire.service.psql.employee;

import com.google.gson.Gson;
import com.hertfordshire.dto.EmployeeDto;
import com.hertfordshire.dto.PortalAccountDto;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.PortalAccountDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.service.psql.kafka.subscription.KafkaSubscriptionService;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.psql.role.RolesService;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequenceService;
import com.hertfordshire.service.sequence.portal_user_id.PortalUserSequenceService;
import com.hertfordshire.utils.lhenum.GenericStatusConstant;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class.getSimpleName());

    @Autowired
    private PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao;


    @Autowired
    private PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService;

    @Autowired
    private PortalUserDao portalUserDao;

    @Autowired
    private PortalAccountService portalAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PortalUserSequenceService portalUserSequenceService;

    private Gson gson;

    @Autowired
    private RolesService rolesService;

//    @Autowired
//    private KafkaTopicService kafkaTopicService;
//
//    @Autowired
//    private KafkaSubscriptionService kafkaSubscriptionService;

    @Autowired
    public EmployeeServiceImpl() {
        gson = new Gson();
    }

    @Override
    public PortalUser updateEmployee(EmployeeDto employeeDto, PortalUser portalUser, PortalAccount foundPortalAccount) {

        portalUser.setEmail(employeeDto.getEmail());

        portalUser.setFirstName(employeeDto.getFirstName());

        portalUser.setLastName(employeeDto.getLastName());

        portalUser.setPhoneNumber(employeeDto.getPhoneNumber().trim());

        if (!TextUtils.isBlank(employeeDto.getOtherPhoneNumber())) {
            portalUser.setOtherPhoneNumber(employeeDto.getOtherPhoneNumber().trim());
        }

        portalUser.setAccountBlockedByAdmin(employeeDto.isAccountBlockedByAdmin());

        if (employeeDto.getRoles().length > 0) {
            if (employeeDto.getRoles() != null) {
                portalAccountAndPortalUserRoleMapperService.removeAllRoles(foundPortalAccount, portalUser);
                portalAccountAndPortalUserRoleMapperService.saveUserBeingRegisteredsRoles(foundPortalAccount, portalUser, employeeDto.getRoles());
            }
        }

        PortalUser newPortalUser = this.portalUserDao.save(portalUser);
        List<String> stringList = Arrays.asList(employeeDto.getRoles());


        //// First remove all
        //this.kafkaSubscriptionService.removeAll(newPortalUser);

        for (String s : stringList) {
            Role role = this.rolesService.findByRoleType(RoleTypeConstant.valueOf(s.toUpperCase()));
            if (role.getRoleType().equals(RoleTypeConstant.RECEPTIONIST)) {
//                KafkaTopicModel kafkaTopicModel =
//                        this.kafkaTopicService.findByName("lab-test-ordered");
//                this.kafkaSubscriptionService.add(newPortalUser, kafkaTopicModel);
            }

            if (role.getRoleType().equals(RoleTypeConstant.MEDICAL_LAB_SCIENTIST)) {
               // KafkaTopicModel kafkaTopicModel =
//                        this.kafkaTopicService.findByName("lab-test-ordered-notify-medical-lab-scientist");
//                this.kafkaSubscriptionService.add(newPortalUser, kafkaTopicModel);
            }
        }

        return newPortalUser;
    }

    @Transactional
    @Override
    public PortalUser createEmployee(EmployeeDto employeeDto,
                                     PortalAccountDto portalAccountDto,
                                     PortalAccount portalAccount,
                                     PortalUser createdBy,
                                     PortalAccountSequenceService portalAccountSequenceService) {


        String portalUserId = String.format("EMP_%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                portalUserSequenceService.getNextId()
        );

        PortalAccount foundPortalAccount = null;
        if (portalAccount != null) {
            foundPortalAccount = portalAccount;
        } else if (portalAccountDto != null) {
            foundPortalAccount = portalAccountService.save(portalAccountDto);
        } else {
            logger.info("portalAccount not found nor created");
            return null;
        }

        PortalUser portalUser = new PortalUser();
        portalUser.setCode(portalUserId);
        portalUser.setEmail(employeeDto.getEmail());
        portalUser.setFirstName(employeeDto.getFirstName());
        portalUser.setLastName(employeeDto.getLastName());
        portalUser.setDefaultPortalAccountCode(foundPortalAccount.getCode());


        if (!TextUtils.isBlank(employeeDto.getUserStatus())) {
            portalUser.setUserStatus(GenericStatusConstant.valueOf(employeeDto.getUserStatus().toUpperCase()));
        }

        if (employeeDto.isEmailOrPhoneNumberIsVerified()) {
            portalUser.setEmailOrPhoneNumberIsVerified(employeeDto.isEmailOrPhoneNumberIsVerified());
        }
        if (employeeDto.isEmailVerified()) {
            portalUser.setEmailVerified(employeeDto.isEmailVerified());
        }

        if (createdBy != null) {
            portalUser.setCreatedBy(createdBy);
        } else {
            portalUser.setCreatedBy(null);
        }
        portalUser.setPassword(passwordEncoder.encode(employeeDto.getPassword().trim()));

        portalUser.setPhoneNumber(employeeDto.getPhoneNumber().trim());

        if (!TextUtils.isBlank(employeeDto.getOtherPhoneNumber())) {
            portalUser.setOtherPhoneNumber(employeeDto.getOtherPhoneNumber().trim());
        }
        portalUser.getPortalAccounts().add(foundPortalAccount);

        if (employeeDto.getRoles().length > 0) {
            if (employeeDto.getRoles() != null) {
                portalAccountAndPortalUserRoleMapperService.saveUserBeingRegisteredsRoles(foundPortalAccount, portalUser, employeeDto.getRoles());
            }
        }

        //return null;

        PortalUser newPortalUser = this.portalUserDao.save(portalUser);
        List<String> stringList = Arrays.asList(employeeDto.getRoles());

        for (String s : stringList) {
            Role role = this.rolesService.findByRoleType(RoleTypeConstant.valueOf(s.toUpperCase()));
            if (role.getRoleType().equals(RoleTypeConstant.RECEPTIONIST)) {
//                KafkaTopicModel kafkaTopicModel =
//                        this.kafkaTopicService.findByName("lab-test-ordered");
//                this.kafkaSubscriptionService.add(newPortalUser, kafkaTopicModel);
            }

            if (role.getRoleType().equals(RoleTypeConstant.MEDICAL_LAB_SCIENTIST)) {
//                KafkaTopicModel kafkaTopicModel =
//                        this.kafkaTopicService.findByName("lab-test-ordered-notify-medical-lab-scientist");
//                this.kafkaSubscriptionService.add(newPortalUser, kafkaTopicModel);
            }
        }

        return newPortalUser;
    }
}
