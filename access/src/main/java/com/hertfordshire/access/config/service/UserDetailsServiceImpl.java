package com.hertfordshire.access.config.service;

import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.dao.psql.AdminSettingsDao;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.Utils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static com.hertfordshire.utils.Utils.checkIfValidNumber;


@Service(value = "userService")
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = Logger.getLogger(UserDetailsServiceImpl.class.getSimpleName());

    private Gson gson;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao;

    @Autowired
    private RolesDao rolesDao;

    @Autowired
    private AdminSettingsDao adminSettingsDao;

    @Autowired
    private PortalUserDao portalUserDao;

    @Value("${authToken}")
    private String authTokenName;

    @Value("${loginType}")
    private String loginType;

    @Value("${password}")
    private String password;

    @Autowired
    public UserDetailsServiceImpl() {
        this.gson = new Gson();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        PortalUser portalUser = null;
        PortalAccount portalAccount = null;
        String portalAccountCode = "";
        String portalUserCode = "";
        List<Role> roles = new ArrayList<>();

        if (EmailValidator.getInstance(true).isValid(username.toLowerCase())) {
            portalUser = portalUserService.findPortalUserByEmail(username.toLowerCase());
        } else if(checkIfValidNumber(username)) {
            portalUser = portalUserService.findPortalUserByPhoneNumber(username.toLowerCase());
        }

        if (portalUser == null) {
            throw new UsernameNotFoundException(username);
        }

        if(portalUser.isAccountBlockedByAdmin()) {
            throw new RuntimeException("blockedByAdmin");
        }

        List<AdminSettings> adminSettingsList = this.adminSettingsDao.findAll();
        AdminSettings adminSetting = adminSettingsList.stream().findFirst().orElse(null);

        if(adminSetting != null) {

            if(portalUser.getWhenLoginAttemptFailedLast() != null) {

                long hours = Utils.getDifferenceBetweenTwoDates(portalUser.getWhenLoginAttemptFailedLast());

                if (hours >= adminSetting.getAfterHoursPermitUserLoginAfterFailedAttempts()) {
                    Date now = new Date();
                    portalUser.setWhenLoginAttemptFailedLast(now);
                    portalUser.setFailedLoginAttempts(0);
                    portalUser = this.portalUserDao.save(portalUser);

                } else {
                    if (portalUser.getFailedLoginAttempts() >= adminSetting.getNumberOfLoginAttemptsAllowedForAUser()) {
                        throw new RuntimeException("blocked");
                    }
                }
            }
        }


        portalUserCode = portalUser.getCode();

        if (portalUser.getPortalAccounts().stream().findFirst().isPresent()) {
            portalAccount = portalUser.getPortalAccounts().stream().findFirst().get();
            portalAccountCode = portalAccount.getCode();
        }

        List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMappers
                = portalAccountAndPortalUserRoleMapperDao
                .findByPortalAccountCodeAndPortalUserCode(portalAccountCode.toLowerCase(),
                        portalUserCode.toLowerCase());


        portalAccountAndPortalUserRoleMappers.forEach(portalAccountAndPortalUserRoleMapper -> {
            Optional<Role> role = rolesDao.findById(portalAccountAndPortalUserRoleMapper.getRoleId());
            role.ifPresent(roles::add);
        });


        String value = "";
        if(!TextUtils.isBlank(portalUser.getEmail())){
           value = portalUser.getEmail();
        }else if(!TextUtils.isBlank(portalUser.getPhoneNumber())) {
            value = portalUser.getPhoneNumber();
        }

        return new UserDetailsDto(value, portalUser.getPassword(),
                portalUser.isEmailOrPhoneNumberIsVerified(), portalUser.isAccountNonLocked(),true,
                portalUser.isLockedOut(), userService.getAuthorities(roles));
    }
}
