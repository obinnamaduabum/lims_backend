package com.hertfordshire.access.config.filter.basic_login;

import com.google.gson.Gson;
import com.hertfordshire.access.config.authentication_token_service.AuthenticationTokenService;
import com.hertfordshire.access.config.service.UserDetailsBean;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.dto.PortalAccountDescriptionDto;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalAccountAndPortalUserRoleMappper;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.Utils;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

import static com.hertfordshire.access.config.constants.SecurityConstants.HEADER_STRING;


public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    @Value("${authToken}")
    private String authTokenName;

    @Autowired
    private AuthenticationTokenService authenticationTokenService;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private RolesDao roleDao;

    private Gson gson;

    @Autowired
    private UserService userService;


    @Autowired
    private PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService;

    @Autowired
    private PortalAccountService portalAccountService;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao;


    public JWTAuthorizationFilter(AuthenticationManager authManager) {
        super(authManager);
        this.gson = new Gson();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        PortalUser portalUser = null;

        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            portalUser = authenticationTokenService.fetchUserFromRequestFormHeader(request);

            if (portalUser != null) {

                if(portalUser.isAccountBlockedByAdmin()) {
                    return null;
                    //throw new RuntimeException("blockedByAdmin");
                }

                Collection<? extends GrantedAuthority> authorities = fetchAuthorities(portalUser);
                UserDetailsBean userDetailsBean = this.getUserDetails(portalUser, authorities);
                return new UsernamePasswordAuthenticationToken(userDetailsBean, null, new ArrayList<>());
            }
            return null;
        }

        portalUser = authenticationTokenService.fetchUserFromRequestFormCookie(request);

        if (portalUser != null) {

            if(portalUser.isAccountBlockedByAdmin()) {
                return null;
                //throw new RuntimeException("blockedByAdmin");
            }

            Collection<? extends GrantedAuthority> authorities = fetchAuthorities(portalUser);
            UserDetailsBean userDetailsBean = this.getUserDetails(portalUser, authorities);
           return new UsernamePasswordAuthenticationToken(userDetailsBean, null, authorities);
        }

        return null;
    }



    private UserDetailsBean getUserDetails(PortalUser portalUser, Collection<? extends GrantedAuthority> authorities) {

        String value = "";
        if(!TextUtils.isBlank(portalUser.getEmail())) {
           value = portalUser.getEmail();
        }

        if(!TextUtils.isBlank(portalUser.getPhoneNumber())) {
            value = portalUser.getPhoneNumber();
        }

        return new UserDetailsBean(value, portalUser.getPassword(),
                portalUser.isEmailOrPhoneNumberIsVerified(),
                portalUser.isAccountNonLocked(), portalUser.isLockedOut(),
                        true, authorities);
    }






    private Collection<? extends GrantedAuthority> fetchAuthorities(PortalUser foundPortalUser) {

       // logger.info("user id: "+ foundPortalUser.getId());
        PortalAccount portalAccount = null;
        String portalUserCode = "";
        String portalAccountCode = "";

        List<Role> roles = new ArrayList<>();

        PortalUser portalUser = this.portalUserService.findPortalUserById(foundPortalUser.getId());
        portalUserCode = portalUser.getCode();

        List<PortalAccountDescriptionDto> portalAccountDescriptionDtoLists = new ArrayList<>();
        List<PortalAccount> newPortalAccounts = new ArrayList<>(portalUser.getPortalAccounts());

        for(int c = 0; c < newPortalAccounts.size(); c++) {

            if(c == 0) {
                portalAccount = newPortalAccounts.get(c);
                portalAccountCode = newPortalAccounts.get(c).getCode();
            }

            List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMappperList =
                    this.portalAccountAndPortalUserRoleMapperService.findByPortalAccountCodeAndPortalUserCode(portalAccountCode, portalUserCode);



            PortalAccountDescriptionDto portalAccountDescriptionDto = new PortalAccountDescriptionDto();

            portalAccountDescriptionDto.setPortalAccountTypeConstant(portalAccount.getPortalAccountType());

            Set<PortalAccount> portalAccounts = new HashSet<PortalAccount>();
            portalAccounts.add(portalAccount);
            List<String> userRoles = new ArrayList<>();
            for (PortalAccountAndPortalUserRoleMappper portalAccountAndPortalUserRoleMappper : portalAccountAndPortalUserRoleMappperList) {
                if (newPortalAccounts.get(c).getCode().equals(portalAccountAndPortalUserRoleMappper.getPortalAccountCode())) {
                    portalAccountDescriptionDto.setPortalUserTypeConstant(portalAccountAndPortalUserRoleMappper.getUserType());
                    PortalAccount newPortalAccount = this.portalAccountService.findPortalAccountByPortalAccountId(portalAccountAndPortalUserRoleMappper.getPortalAccountCode());
                    userRoles.add(Utils.addUnderScore(portalAccountAndPortalUserRoleMappper.getRoleName()));
                }
            }

            portalAccountDescriptionDto.setRoleName(userRoles);

            portalAccountDescriptionDtoLists.add(portalAccountDescriptionDto);
        }


        //logger.info(this.gson.toJson(portalAccountDescriptionDtoLists));

        List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMapppers
                = portalAccountAndPortalUserRoleMapperDao
                .findByPortalAccountCodeAndPortalUserCode(portalAccountCode.toLowerCase(),
                        portalUserCode.toLowerCase());

        portalAccountAndPortalUserRoleMapppers.forEach(portalAccountAndPortalUserRoleMappper -> {

            Optional<Role> role = roleDao.findById(portalAccountAndPortalUserRoleMappper.getRoleId());
            role.ifPresent(roles::add);
        });

        //logger.info(this.gson.toJson("ur here "));
        //logger.info(this.gson.toJson(this.userService.getAuthorities(roles)));
       return this.userService.getAuthorities(roles);

    }

}