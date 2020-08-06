package com.hertfordshire.access.config.service.user_service;


import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.dto.PortalAccountDescriptionDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.utils.Utils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.hertfordshire.utils.Utils.checkIfValidNumber;

@Component
public class UserServiceImpl implements UserService {

    private static final Logger logger = Logger.getLogger(UserServiceImpl.class.getSimpleName());

    @Autowired
    private PortalUserDao portalUserDao;

    @Autowired
    private RolesDao roleDao;

//    @Autowired
//    private Gson gson;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService;

    @Autowired
    private PortalAccountService portalAccountService;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao;

    @Override
    public UserDetailsDto getPrincipal(HttpServletResponse res, HttpServletRequest request, Authentication authentication) {


        try {

            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            PortalUser portalUser = null;
            PortalAccount portalAccount = null;
            String portalAccountCode = "";
            String portalUserCode = "";
            List<Role> roles = new ArrayList<>();

            if (principal instanceof UserDetails) {

                String username = ((UserDetails) principal).getUsername();

                logger.info("username "+ username);


                if (EmailValidator.getInstance(true).isValid(username.toLowerCase())) {
                    portalUser = portalUserDao.findByEmail(username.toLowerCase());
                } else if (checkIfValidNumber(username)) {
                    portalUser = portalUserDao.findByPhoneNumber(username.toLowerCase().trim());
                    logger.info("portaluser " + portalUser.getCode());
                }

                List<PortalAccountDescriptionDto> portalAccountDescriptionDtoLists = new ArrayList<>();

                if (portalUser != null) {

                    portalUserCode = portalUser.getCode();

                    List<PortalAccount> newPortalAccounts = new ArrayList<>(portalUser.getPortalAccounts());

                    logger.info("account list " + newPortalAccounts.size());

                    for (int c = 0; c < newPortalAccounts.size(); c++) {

                        if (c == 0) {

                            // logger.info(""+newPortalAccounts.get(c));
                            // logger.info(""+newPortalAccounts.get(c).getCode());
                            portalAccount = newPortalAccounts.get(c);
                            logger.info("account: " + portalAccount.getCode());

                            portalAccountCode = newPortalAccounts.get(c).getCode();
                        }

                        List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMappperList =
                        this.portalAccountAndPortalUserRoleMapperService.findByPortalAccountCodeAndPortalUserCode(portalAccountCode, portalUserCode);


                        PortalAccountDescriptionDto portalAccountDescriptionDto = new PortalAccountDescriptionDto();

                        portalAccountDescriptionDto.setPortalAccountTypeConstant(portalAccount.getPortalAccountType());

                        //portalAccountDescriptionDto.setPortalUserTypeConstant();


                        //Set<PortalAccount> portalAccounts = new HashSet<PortalAccount>();
                        // portalAccounts.add(portalAccount);

                        List<String> userRoles = new ArrayList<>();
                        for (PortalAccountAndPortalUserRoleMappper portalAccountAndPortalUserRoleMappper : portalAccountAndPortalUserRoleMappperList) {

                            if (newPortalAccounts.get(c).getCode().equals(portalAccountAndPortalUserRoleMappper.getPortalAccountCode())) {

                                portalAccountDescriptionDto.setPortalUserTypeConstant(portalAccountAndPortalUserRoleMappper.getUserType());

                                // PortalAccount newPortalAccount = this.portalAccountService.findPortalAccountByPortalAccountId(portalAccountAndPortalUserRoleMappper.getPortalAccountCode());

                                userRoles.add(Utils.addUnderScore(portalAccountAndPortalUserRoleMappper.getRoleName()));
                            }
                        }

                        portalAccountDescriptionDto.setRoleName(userRoles);

                        portalAccountDescriptionDtoLists.add(portalAccountDescriptionDto);
                    }

                }


                List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMapppers
                        = portalAccountAndPortalUserRoleMapperDao
                        .findByPortalAccountCodeAndPortalUserCode(portalAccountCode.toLowerCase(),
                                portalUserCode.toLowerCase());

                portalAccountAndPortalUserRoleMapppers.forEach(portalAccountAndPortalUserRoleMappper -> {

                    Optional<Role> role = roleDao.findById(portalAccountAndPortalUserRoleMappper.getRoleId());
                    role.ifPresent(roles::add);
                });


                UserDetailsDto userDetailsDto = new UserDetailsDto(((UserDetails) principal).getUsername(),
                        ((UserDetails) principal).getPassword(), true, true, true, true,
                        getAuthorities(roles));

                logger.info(portalAccount.getCode());

                userDetailsDto.setUserDetails(portalUser, portalAccount, portalAccountDescriptionDtoLists);

                return userDetailsDto;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public String fetchFormOfIdentification() {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return null;
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return roles.stream().map(role ->
                new SimpleGrantedAuthority(role.getRoleType().name())).collect(Collectors.toList());
    }
}
