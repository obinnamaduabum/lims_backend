package com.hertfordshire.service.psql.patient;


import com.hertfordshire.dto.PatientDto;
import com.hertfordshire.dto.PatientInfoUpdate;
import com.hertfordshire.dto.PortalAccountDto;
import com.hertfordshire.model.psql.KafkaTopicModel;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.service.psql.kafka.subscription.KafkaSubscriptionService;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.psql.role.RolesService;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequenceService;
import com.hertfordshire.service.sequence.portal_user_id.PortalUserSequenceService;
import com.hertfordshire.utils.lhenum.GenericStatusConstant;
import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;


@Service
@Transactional
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PortalUserSequenceService portalUserSequenceService;

    @Autowired
    private PortalAccountService portalAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private PortalUserDao portalUserDao;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService;

    @Autowired
    private RolesService rolesService;

    @Autowired
    private KafkaTopicService kafkaTopicService;

    @Autowired
    private KafkaSubscriptionService kafkaSubscriptionService;


    public PatientServiceImpl(){ }

    @Transactional
    @Override
    public PortalUser create(PatientDto patientDto, boolean startUpAction) {

        List<String> roles = new ArrayList<>();
        roles.add(RoleTypeConstant.USER.toString());

        String[] stringArray = roles.toArray(new String[0]);
        patientDto.setRoles(stringArray);

        PortalAccountDto portalAccountDto = new PortalAccountDto();
        portalAccountDto.setName(patientDto.getFirstName() + " " + patientDto.getLastName());
        portalAccountDto.setPortalAccountTypes(PortalAccountTypeConstant.PATIENT.name());

        PortalAccount portalAccount = this.portalAccountService.save(portalAccountDto);

        PortalUser portalUser = new PortalUser();
        portalUser.setFirstName(patientDto.getFirstName());
        portalUser.setLastName(patientDto.getLastName());

        if(!TextUtils.isBlank(patientDto.getOtherName())) {
            portalUser.setOtherName(patientDto.getOtherName());
        }
        portalUser.setPhoneNumber(patientDto.getPhoneNumber());

        if(!TextUtils.isBlank(patientDto.getOtherPhoneNumber())) {
            portalUser.setOtherPhoneNumber(patientDto.getOtherPhoneNumber());
        }

        String portalUserId = String.format("PAT_%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                portalUserSequenceService.getNextId()
        );

        portalUser.setCode(portalUserId);
        portalUser.setEmail(patientDto.getEmail());
        portalUser.setDob(patientDto.getDob());
        portalUser.setPassword(passwordEncoder.encode(patientDto.getPassword()));
        portalUser.setDefaultPortalAccountCode(portalAccount.getCode());
        portalUser.setNextOFKinFirstName(patientDto.getNextOFKinFirstName());
        portalUser.setNextOFKinLastName(patientDto.getNextOFKinLastName());
        portalUser.setNextOFKinPhoneNumber(patientDto.getNextOFKinPhoneNumber());
        portalUser.setCreatedBy(null);
        portalUser.setUserStatus(GenericStatusConstant.INACTIVE);

        if(portalUser.getPortalAccounts() != null){
            portalUser.getPortalAccounts().add(portalAccount);
        } else {
            HashSet<PortalAccount> hashSet = new HashSet<>();
            hashSet.add(portalAccount);
            portalUser.setPortalAccounts(hashSet);
        }

        portalUser.setTwoFactor(patientDto.isTwoFactor());

        if(patientDto.isTwoFactor()) {
            SecretGenerator secretGenerator = new DefaultSecretGenerator();
            String secret = secretGenerator.generate();
            portalUser.setSecret(secret);
        }

        if(startUpAction) {
            portalUser.setUserStatus(GenericStatusConstant.ACTIVE);
            portalUser.setEmailOrPhoneNumberIsVerified(true);
            portalUser.setEmailVerified(true);
        }


        portalUser = this.portalUserDao.save(portalUser);
        if(patientDto.getRoles() != null) {
            this.portalAccountAndPortalUserRoleMapperService.saveUserBeingRegisteredsRoles(portalAccount, portalUser, patientDto.getRoles());
        }

        return portalUser;

    }


    @Override
    public PortalUser updatePatient(PatientInfoUpdate employeeDto, PortalUser portalUser, PortalAccount foundPortalAccount) {

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
        this.kafkaSubscriptionService.removeAll(newPortalUser);

        for(String s: stringList) {
            Role role = this.rolesService.findByRoleType(RoleTypeConstant.valueOf(s.toUpperCase()));
            if(role.getRoleType().equals(RoleTypeConstant.RECEPTIONIST)) {
                KafkaTopicModel kafkaTopicModel =
                        this.kafkaTopicService.findByName("lab-test-ordered");
                this.kafkaSubscriptionService.add(newPortalUser, kafkaTopicModel);
            }

            if(role.getRoleType().equals(RoleTypeConstant.MEDICAL_LAB_SCIENTIST)) {
                KafkaTopicModel kafkaTopicModel =
                        this.kafkaTopicService.findByName("lab-test-ordered-notify-medical-lab-scientist");
                this.kafkaSubscriptionService.add(newPortalUser, kafkaTopicModel);
            }
        }

        return newPortalUser;
    }
}
