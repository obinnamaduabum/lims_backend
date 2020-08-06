package com.hertfordshire.service.psql.portaluser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hertfordshire.dto.ChangePasswordDto;
import com.hertfordshire.dto.CheckOldPasswordDto;
import com.hertfordshire.dto.EmployeeSearchDto;
import com.hertfordshire.dto.PortalUserDto;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalAccountAndPortalUserRoleMappper;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.pojo.PortalUserResponsePojo;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.PortalAccountDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.utils.PhoneNumberValidationUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.*;
import com.hertfordshire.utils.pojo.ProperPhoneNumberPojo;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Service
@Transactional
public class PortalUserServiceImp implements PortalUserService {

    private static final Logger logger = Logger.getLogger(PortalUserServiceImp.class.getSimpleName());

    @Autowired
    private PortalUserDao portalUserDao;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RolesDao rolesDao;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao;

    private String dateSource = "2000-09-09";

    @Autowired
    public PortalUserServiceImp(){}


    @Override
    public List<PortalUser> findAll() {
        return this.portalUserDao.findAll();
    }

    @Override
    public PortalUser findPortalUserByEmail(String email) {
        return portalUserDao.findPortalUserByEmail(email.toLowerCase());
    }

    @Override
    public PortalUser findPortalUserById(Long id) {
        Optional<PortalUser> optionalPortalUser = portalUserDao.findById(id);
        return optionalPortalUser.orElse(null);
    }

    @Override
    public PortalUser findPortalUserByPhoneNumber(String phoneNumber) {
        return this.portalUserDao.findByPhoneNumber(phoneNumber);
    }


    @Override
    public PortalUser findByEmail(String email) {
        PortalUser portalUser = portalUserDao.findByEmail(email.toLowerCase());
        PortalUser user = new PortalUser();

        if (portalUser != null) {
            user.setEmail(portalUser.getEmail());
            user.setFirstName(portalUser.getFirstName());
            user.setLastName(portalUser.getLastName());
            user.setPhoneNumber(portalUser.getPhoneNumber());
            user.setPhoneNumber(portalUser.getPhoneNumber());
            user.setCode(portalUser.getCode());
            user.setLockedOut(portalUser.isLockedOut());
            user.setUserStatus(portalUser.getUserStatus());
            user.setOtherPhoneNumber(checkIfNull(portalUser.getOtherPhoneNumber()));
            user.setSignUpType(portalUser.getSignUpType());
            user.setId(portalUser.getId());
            user.setSignUpType(portalUser.getSignUpType());
            user.setPhoneNumberVerified(portalUser.isPhoneNumberVerified());
            user.setPassword(portalUser.getPassword());
            user.setDefaultPortalAccountCode(portalUser.getDefaultPortalAccountCode());
            user.setTwoFactor(portalUser.isTwoFactor());
            return user;
        }
        return null;
    }



    @Override
    public PortalUser findById(Long id) {
        Optional<PortalUser> optionalPortalUser = portalUserDao.findById(id);

        PortalUser user = new PortalUser();

        if (optionalPortalUser.isPresent()) {
            PortalUser portalUser = optionalPortalUser.get();
            user.setEmail(portalUser.getEmail());
            user.setFirstName(portalUser.getFirstName());
            user.setLastName(portalUser.getLastName());
            user.setPhoneNumber(portalUser.getPhoneNumber());
            user.setPhoneNumber(portalUser.getPhoneNumber());
            user.setCode(portalUser.getCode());
            user.setLockedOut(portalUser.isLockedOut());
            user.setUserStatus(portalUser.getUserStatus());
            user.setOtherPhoneNumber(checkIfNull(portalUser.getOtherPhoneNumber()));
            user.setSignUpType(portalUser.getSignUpType());
            user.setId(portalUser.getId());
            user.setSignUpType(portalUser.getSignUpType());
            user.setPhoneNumberVerified(portalUser.isPhoneNumberVerified());
            user.setPassword(portalUser.getPassword());
            user.setDefaultPortalAccountCode(portalUser.getDefaultPortalAccountCode());
            return user;
        }
        return null;
    }

    @Override
    public PortalUser findByCode(String code) {
        PortalUser portalUser = portalUserDao.findByCode(code);
        PortalUser user = new PortalUser();

        if (portalUser != null) {
            user.setEmail(portalUser.getEmail());
            user.setFirstName(portalUser.getFirstName());
            user.setLastName(portalUser.getLastName());
            user.setPhoneNumber(portalUser.getPhoneNumber());
            user.setPhoneNumber(portalUser.getPhoneNumber());
            user.setCode(portalUser.getCode());
            user.setLockedOut(portalUser.isLockedOut());
            user.setUserStatus(portalUser.getUserStatus());
            user.setOtherPhoneNumber(checkIfNull(portalUser.getOtherPhoneNumber()));
            user.setSignUpType(portalUser.getSignUpType());
            user.setId(portalUser.getId());
            user.setSignUpType(portalUser.getSignUpType());
            user.setPhoneNumberVerified(portalUser.isPhoneNumberVerified());
            user.setPassword(portalUser.getPassword());
            return user;
        }
        return null;
    }


    @Override
    public PortalUser findByPortalUserCode(String code) {
        return portalUserDao.findByCode(code);
    }

    @Override
    public PortalUser findByPhoneNumber(String phoneNumber) {
        PortalUser portalUser = this.portalUserDao.findByPhoneNumber(phoneNumber);
        PortalUser user = new PortalUser();

        if (portalUser != null) {
            user.setEmail(portalUser.getEmail());
            user.setFirstName(portalUser.getFirstName());
            user.setLastName(portalUser.getLastName());
            user.setPhoneNumber(portalUser.getPhoneNumber());
            user.setPhoneNumber(portalUser.getPhoneNumber());
            user.setCode(portalUser.getCode());
            user.setLockedOut(portalUser.isLockedOut());
            user.setUserStatus(portalUser.getUserStatus());
            user.setOtherPhoneNumber(checkIfNull(portalUser.getOtherPhoneNumber()));
            user.setSignUpType(portalUser.getSignUpType());
            user.setId(portalUser.getId());
            user.setSignUpType(portalUser.getSignUpType());
            return user;

        }
        return null;
    }

    @Override
    public PortalUser updateUserProfile(Object portalUserDto) {

//            JsonParser parser = new JsonParser();
//
//            JsonObject jsonObject = parser.parse(gson.toJson(portalUserDto)).getAsJsonObject();
//
//
//            if (doesFieldExist(jsonObject, "firstName")) {
//                portalUser.setFirstName(getStringField(jsonObject, "firstName"));
//            }
//
//            if (doesFieldExist(jsonObject, "lastName")) {
//                portalUser.setLastName(getStringField(jsonObject, "lastName"));
//            }
//
//            if (doesFieldExist(jsonObject, "otherName")) {
//                portalUser.setLastName(getStringField(jsonObject, "otherName"));
//            }
//
//            if (doesFieldExist(jsonObject, "defaultProfileTypeConstant")) {
//                portalUser.setDefaultProfileTypeConstant(DefaultProfileTypeConstant.valueOf(getStringField(jsonObject, "defaultProfileTypeConstant")));
//            }
//
//            if (doesFieldExist(jsonObject, "otherPhoneNumber")) {
//                portalUser.setOtherPhoneNumber(getStringField(jsonObject, "otherPhoneNumber"));
//            }

        //return portalUserDao.save(portalUser);
        return null;
    }

    @Override
    public boolean checkIfPasswordsmatch(CheckOldPasswordDto checkOldPasswordDto, PortalUser portalUser) {
        try {
            return this.bCryptPasswordEncoder.matches(checkOldPasswordDto.getOldPassword(), portalUser.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean changePassword(ChangePasswordDto changePasswordDto, PortalUser portalUser) {

        try {
            if (this.bCryptPasswordEncoder.matches(changePasswordDto.getOldPassword(), portalUser.getPassword())) {
                portalUser.setPassword(this.bCryptPasswordEncoder.encode(changePasswordDto.getNewPassword()));
                this.portalUserDao.save(portalUser);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public PaginationResponsePojo findAllByDefaultPortalAccountCode(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable) {
        return null;
    }


    public String checkIfNull(String value) {
        if (value == null || StringUtils.isBlank(value)) {
            return "";
        }
        return value;
    }


    public boolean doesFieldExist(JsonObject json, String field) {
        return json.has(field);
    }

    public JsonArray getArrayField(JsonObject json, String field) {
        return json.get(field).getAsJsonArray();
    }

    public boolean getBooleanField(JsonObject json, String field) {
        return json.get(field).getAsBoolean();
    }

    public String getStringField(JsonObject json, String field) {
        return json.get(field).getAsString();
    }

    @Override
    public PaginationResponsePojo findAllByPortalUsers(EmployeeSearchDto employeeSearchDto, Pageable pageable) {

        // logger.info(this.gson.toJson(employeeSearchDto));
        String email = null;
        String fullName = null;
        String phoneNumber = null;
        PortalAccountTypeConstant accountType = null;
        String userId = null;
        Date startDate;
        Date endDate;

        List<RoleTypeConstant> roles = null;

        // logger.info(this.gson.toJson(employeeSearchDto.getRoles()));

        if(employeeSearchDto.getRoles().size() > 0) {
            roles = new ArrayList<>();
            for(String employeeRole: employeeSearchDto.getRoles()) {
                roles.add(RoleTypeConstant.valueOf(employeeRole));
            }
        }

        if (StringUtils.isNotBlank(employeeSearchDto.getEmail())) {
            email = employeeSearchDto.getEmail().toLowerCase().trim();
        }

        if(!TextUtils.isBlank(employeeSearchDto.getCode())){
            userId = employeeSearchDto.getCode().toLowerCase().trim();
        }

        if (StringUtils.isNotBlank(employeeSearchDto.getAccountType())) {
            if (!employeeSearchDto.getAccountType().toUpperCase().trim().equalsIgnoreCase("all")) {

                // logger.info(employeeSearchDto.getAccountType());
                accountType = PortalAccountTypeConstant.valueOf(employeeSearchDto.getAccountType().toUpperCase().trim());
                // logger.info(accountType.name());
            }
        }

        if (StringUtils.isNotBlank(employeeSearchDto.getFullName())) {
            fullName = employeeSearchDto.getFullName().toLowerCase().trim();
        }


        if (StringUtils.isNotBlank(employeeSearchDto.getPhoneNumber())) {

            //ProperPhoneNumberPojo properPhoneNumberPojo = this
            // phoneNumber = employeeSearchDto.getPhoneNumber().toLowerCase().trim();
            try {

                ProperPhoneNumberPojo properPhoneNumberPojo = PhoneNumberValidationUtil.validatePhoneNumber(employeeSearchDto.getPhoneNumber().trim(),
                        employeeSearchDto.getSelectedPhoneNumber());
                if (properPhoneNumberPojo != null) {
                    phoneNumber = properPhoneNumberPojo.getFormattedNumber();
                }

                logger.info("phone number not found");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("phone number not found");
            }
        }

        if (employeeSearchDto.getStartDate() != null) {
            startDate = Utils.atStartOfDay(employeeSearchDto.getStartDate());
        } else {

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date = null;
            try {
                date = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startDate = Utils.atStartOfDay(date);
        }

        if (employeeSearchDto.getEndDate() != null) {
            endDate = Utils.atEndOfDay(employeeSearchDto.getEndDate());
        } else {
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);
        }


        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();


        //List<PortalUser> portalUserList = this.portalUserDao.findByDefaultPortalAccountCode(lookUpPortalAccount.getCode().toLowerCase(), email, fullName, startDate, endDate, pageable);

        Query query = this.entityManager.createQuery(
                "select distinct p from PortalAccount as pa join pa.portalUsers p"
                        + " left join PortalAccountAndPortalUserRoleMappper papr on p.code = papr.portalUserCode "
                        + " where (:accountType is null or pa.portalAccountType = :accountType)"
                        + " and ((:roles) is null or papr.roleTypeConstant in (:roles))"
                        + " and (:email is null or lower(p.email) like :email)"
                        + " and (:userId is null or lower(p.code) like :userId )"
                        + " and (:phoneNumber is null or lower(p.phoneNumber) like :phoneNumber)" +
                        " and ((:fullName is null or lower(p.firstName) like :fullName)" +
                        " or (:fullName is null or lower(p.lastName) like :fullName)" +
                        " or (:fullName is null or lower(p.otherName) like :fullName))" +
                        " and p.dateCreated between :startDate and :endDate order by p.dateCreated desc");

        query.setFirstResult((pageNumber) * pageSize);

        if (StringUtils.isNotBlank(email)) {
            query.setParameter("email", "%" + email + "%");
        } else {
            query.setParameter("email", email);
        }

        query.setParameter("userId", userId);

        query.setParameter("roles", roles);

        query.setParameter("accountType", accountType);

        if (StringUtils.isNotBlank(fullName)) {
            query.setParameter("fullName", "%" + fullName + "%");
        } else {
            query.setParameter("fullName", fullName);
        }
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(pageSize);
        this.entityManager.close();

        List<PortalUser> portalUserList = query.getResultList();

        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();

        List<PortalUserResponsePojo> portalUserPojos = new ArrayList<>();
        for (int i = 0; i < portalUserList.size(); i++) {

            PortalUserResponsePojo portalUserResponsePojo = new PortalUserResponsePojo();

            if (pageNumber == 0) {
                portalUserResponsePojo.setPosition((long) (i + 1));
            } else {
                portalUserResponsePojo.setPosition((long) (i + pageSize + pageNumber));
            }

            portalUserResponsePojo.setDateCreated(portalUserList.get(i).getDateCreated());
            portalUserResponsePojo.setId(portalUserList.get(i).getId());
            //portalUserResponsePojo.setPortalAccountCode(lookUpPortalAccount.getCode());
            portalUserResponsePojo.setCode(portalUserList.get(i).getCode());
            portalUserResponsePojo.setEmail(portalUserList.get(i).getEmail());
            portalUserResponsePojo.setFirstName(portalUserList.get(i).getFirstName());
            portalUserResponsePojo.setLastName(portalUserList.get(i).getLastName());
            portalUserResponsePojo.setPhoneNumber(portalUserList.get(i).getPhoneNumber());


            Set<PortalAccount> portalAccountSet = portalUserList.get(i).getPortalAccounts();
            List<PortalAccount> portalAccountList = new ArrayList(portalAccountSet);
            PortalAccount portalAccount = portalAccountList.stream().findFirst().orElse(null);
            if(portalAccount != null) {
                portalUserResponsePojo.setPortalAccountType(portalAccount.getPortalAccountType().toString());
            }

           // ArrayList<String> roles = new ArrayList<>();

//            List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMappperList = this.portalAccountAndPortalUserRoleMapperDao.findByPortalAccountCodeAndPortalUserCode(lookUpPortalAccount.getCode(), portalUserList.get(i).getCode());
//            portalAccountAndPortalUserRoleMappperList.forEach(result -> {
//
//               Optional<Role> optionalRole = this.rolesDao.findById(result.getRoleId());
//
//               if(optionalRole.isPresent()){
//                   Role role =  optionalRole.get();
//                   roles.add(role.getRoleType().name());
//               }
//
//            });
//            String[] stringArray = roles.stream().toArray(String[]::new);
//            portalUserResponsePojo.setRoles(stringArray);

            portalUserPojos.add(portalUserResponsePojo);
        }

        paginationResponsePojo.setDataList(portalUserPojos);
        paginationResponsePojo.setPageNumber((long) pageNumber);
        paginationResponsePojo.setPageSize((long) pageSize);
        return paginationResponsePojo;
    }


    @Override
    public Long countAllByPortalUsers(EmployeeSearchDto employeeSearchDto, Pageable pageable) {

        String email = null;
        PortalAccountTypeConstant accountType = null;
        String fullName = null;
        String phoneNumber = null;
        Date startDate;
        Date endDate;
        String userId = null;


        List<RoleTypeConstant> roles = null;

        // logger.info(this.gson.toJson(employeeSearchDto.getRoles()));

        if(employeeSearchDto.getRoles().size() > 0) {
            roles = new ArrayList<>();
            for(String employeeRole: employeeSearchDto.getRoles()) {
                roles.add(RoleTypeConstant.valueOf(employeeRole));
            }
        }

        if (StringUtils.isNotBlank(employeeSearchDto.getEmail())) {
            email = employeeSearchDto.getEmail().toLowerCase().trim();
        }

        if(!TextUtils.isBlank(employeeSearchDto.getCode())){
            userId = employeeSearchDto.getCode().toLowerCase().trim();
        }


        if (StringUtils.isNotBlank(employeeSearchDto.getAccountType())) {
            if (!employeeSearchDto.getAccountType().toUpperCase().trim().equalsIgnoreCase("all")) {

                // logger.info(employeeSearchDto.getAccountType());
                accountType = PortalAccountTypeConstant.valueOf(employeeSearchDto.getAccountType().toUpperCase().trim());
                // logger.info(accountType.name());
            }
        }

        if (StringUtils.isNotBlank(employeeSearchDto.getFullName())) {
            fullName = employeeSearchDto.getFullName().toLowerCase().trim();
        }


        if (StringUtils.isNotBlank(employeeSearchDto.getPhoneNumber())) {

            //ProperPhoneNumberPojo properPhoneNumberPojo = this
            // phoneNumber = employeeSearchDto.getPhoneNumber().toLowerCase().trim();
            try {

                ProperPhoneNumberPojo properPhoneNumberPojo = PhoneNumberValidationUtil.validatePhoneNumber(employeeSearchDto.getPhoneNumber().trim(),
                        employeeSearchDto.getSelectedPhoneNumber());
                if (properPhoneNumberPojo != null) {
                    phoneNumber = properPhoneNumberPojo.getFormattedNumber();
                }

                logger.info("phone number not found");
            } catch (Exception e) {
                e.printStackTrace();
                logger.info("phone number not found");
            }
        }

        if (employeeSearchDto.getStartDate() != null) {
            startDate = Utils.atStartOfDay(employeeSearchDto.getStartDate());
        } else {

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date = null;
            try {
                date = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startDate = Utils.atStartOfDay(date);
        }

        if (employeeSearchDto.getEndDate() != null) {
            endDate = Utils.atEndOfDay(employeeSearchDto.getEndDate());
        } else {
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);
        }

        Query query = this.entityManager.createQuery(
                "select count(distinct p) from PortalAccount as pa join pa.portalUsers p"
                        + " left join PortalAccountAndPortalUserRoleMappper papr on p.code = papr.portalUserCode "
                        + " where (:accountType is null or pa.portalAccountType = :accountType)"
                        + " and ((:roles) is null or papr.roleTypeConstant in (:roles))"
                        + " and (:email is null or lower(p.email) like :email)"
                        + " and (:userId is null or lower(p.code) like :userId )"
                        + " and (:phoneNumber is null or lower(p.phoneNumber) like :phoneNumber)" +
                        " and ((:fullName is null or lower(p.firstName) like :fullName)" +
                        " or (:fullName is null or lower(p.lastName) like :fullName)" +
                        " or (:fullName is null or lower(p.otherName) like :fullName))" +
                        " and p.dateCreated between :startDate and :endDate");

        if (!TextUtils.isBlank(email)) {
            query.setParameter("email", "%" + email + "%");
        } else {
            query.setParameter("email", email);
        }



        //roles
        query.setParameter("roles", roles);
        //roles


        query.setParameter("userId", userId);
        query.setParameter("accountType", accountType);

        if (!TextUtils.isBlank(fullName)) {
            query.setParameter("fullName", "%" + fullName + "%");
        } else {
            query.setParameter("fullName", fullName);
        }
        query.setParameter("phoneNumber", phoneNumber);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        this.entityManager.close();

        int count = ((Number) query.getSingleResult()).intValue();
        logger.info("count: "+ count);
        return (long) count;
    }


    @Override
    public Long countByDefaultPortalAccountCode(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable) {

        String email = null;
        String fullName = null;
        Date startDate;
        Date endDate;
        if (StringUtils.isNotBlank(employeeSearchDto.getEmail())) {
            email = employeeSearchDto.getEmail().toLowerCase().trim();
        }

        if (StringUtils.isNotBlank(employeeSearchDto.getFullName())) {
            fullName = employeeSearchDto.getFullName().toLowerCase().trim();
        }

        if (employeeSearchDto.getStartDate() != null) {
            startDate = Utils.atStartOfDay(employeeSearchDto.getStartDate());
        } else {

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date = null;
            try {
                date = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startDate = Utils.atStartOfDay(date);
        }

        if (employeeSearchDto.getEndDate() != null) {
            endDate = Utils.atEndOfDay(employeeSearchDto.getEndDate());
        } else {
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);
        }


        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();


        //List<PortalUser> portalUserList = this.portalUserDao.findByDefaultPortalAccountCode(lookUpPortalAccount.getCode().toLowerCase(), email, fullName, startDate, endDate, pageable);

        Query query = this.entityManager.createQuery(
                "select distinct count(p) from PortalAccount as pa join pa.portalUsers p" +
                        " where (:email is null or lower(p.email) like :email)" +
                        " and ((:fullName is null or lower(p.firstName) like :fullName)" +
                        " or (:fullName is null or lower(p.lastName) like :fullName)" +
                        " or (:fullName is null or lower(p.otherName) like :fullName))" +
                        " and p.dateCreated between :startDate and :endDate");

        query.setFirstResult((pageNumber) * pageSize);
//        query.setParameter("lookUpPortalAccount", lookUpPortalAccount.getCode().toLowerCase());

        if (StringUtils.isNotBlank(email)) {
            query.setParameter("email", "%" + email + "%");
        } else {
            query.setParameter("email", email);
        }
        if (StringUtils.isNotBlank(fullName)) {
            query.setParameter("fullName", "%" + fullName + "%");
        } else {
            query.setParameter("fullName", fullName);
        }

        query.setParameter("startDate", startDate);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(pageSize);

        this.entityManager.close();


        int count = ((Number) query.getSingleResult()).intValue();
        return (long) count;
    }

    @Override
    public PaginationResponsePojo findAllByDefaultPortalAccountCodeForEmployee(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable) {

        //   logger.info(this.gson.toJson(employeeSearchDto));
        String email = null;
        String fullName = null;
        Date startDate;
        Date endDate;
        if (StringUtils.isNotBlank(employeeSearchDto.getEmail())) {
            email = employeeSearchDto.getEmail().toLowerCase().trim();
        }

        if (StringUtils.isNotBlank(employeeSearchDto.getFullName())) {
            fullName = employeeSearchDto.getFullName().toLowerCase().trim();
        }

        if (employeeSearchDto.getStartDate() != null) {
            startDate = Utils.atStartOfDay(employeeSearchDto.getStartDate());
        } else {

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date = null;
            try {
                date = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startDate = Utils.atStartOfDay(date);
        }

        if (employeeSearchDto.getEndDate() != null) {
            endDate = Utils.atEndOfDay(employeeSearchDto.getEndDate());
        } else {
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);
        }


        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();


        //List<PortalUser> portalUserList = this.portalUserDao.findByDefaultPortalAccountCode(lookUpPortalAccount.getCode().toLowerCase(), email, fullName, startDate, endDate, pageable);

        Query query = this.entityManager.createQuery(
                "select distinct p from PortalAccount as pa join pa.portalUsers p" +
                        " where lower(pa.code) = :lookUpPortalAccount" +
                        " and (:email is null or lower(p.email) like :email)" +
                        " and ((:fullName is null or lower(p.firstName) like :fullName)" +
                        " or (:fullName is null or lower(p.lastName) like :fullName)" +
                        " or (:fullName is null or lower(p.otherName) like :fullName))" +
                        " and p.dateCreated between :startDate and :endDate");

        query.setFirstResult((pageNumber) * pageSize);
        query.setParameter("lookUpPortalAccount", lookUpPortalAccount.getCode().toLowerCase());

        if (StringUtils.isNotBlank(email)) {
            query.setParameter("email", "%" + email + "%");
        } else {
            query.setParameter("email", email);
        }
        if (StringUtils.isNotBlank(fullName)) {
            query.setParameter("fullName", "%" + fullName + "%");
        } else {
            query.setParameter("fullName", fullName);
        }

        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(pageSize);

        this.entityManager.close();

        List<PortalUser> portalUserList = query.getResultList();

        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();

        List<PortalUserResponsePojo> portalUserPojos = new ArrayList<>();
        for (int i = 0; i < portalUserList.size(); i++) {

            PortalUserResponsePojo portalUserResponsePojo = new PortalUserResponsePojo();

            if (pageNumber == 0) {
                portalUserResponsePojo.setPosition((long) (i + 1));
            } else {
                portalUserResponsePojo.setPosition((long) (i + pageSize + pageNumber));
            }

            portalUserResponsePojo.setDateCreated(portalUserList.get(i).getDateCreated());
            portalUserResponsePojo.setId(portalUserList.get(i).getId());
            portalUserResponsePojo.setPortalAccountCode(lookUpPortalAccount.getCode());
            portalUserResponsePojo.setCode(portalUserList.get(i).getCode());
            portalUserResponsePojo.setEmail(portalUserList.get(i).getEmail());
            portalUserResponsePojo.setFirstName(portalUserList.get(i).getFirstName());
            portalUserResponsePojo.setLastName(portalUserList.get(i).getLastName());

            ArrayList<String> roles = new ArrayList<>();

            List<PortalAccountAndPortalUserRoleMappper> portalAccountAndPortalUserRoleMappperList =
            this.portalAccountAndPortalUserRoleMapperDao.findByPortalAccountCodeAndPortalUserCode(lookUpPortalAccount.getCode(), portalUserList.get(i).getCode());

            portalAccountAndPortalUserRoleMappperList.forEach(result -> {

                Optional<Role> optionalRole = this.rolesDao.findById(result.getRoleId());

                if (optionalRole.isPresent()) {
                    Role role = optionalRole.get();
                    roles.add(role.getRoleType().name());
                }

            });
            String[] stringArray = roles.stream().toArray(String[]::new);
            portalUserResponsePojo.setRoles(stringArray);

            portalUserPojos.add(portalUserResponsePojo);
        }

        paginationResponsePojo.setDataList(portalUserPojos);
        paginationResponsePojo.setPageNumber((long) pageNumber);
        paginationResponsePojo.setPageSize((long) pageSize);
        return paginationResponsePojo;
    }


    @Override
    public Long countByDefaultPortalAccountCodeForEmployee(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable) {

        String email = null;
        String fullName = null;
        Date startDate;
        Date endDate;
        if (StringUtils.isNotBlank(employeeSearchDto.getEmail())) {
            email = employeeSearchDto.getEmail().toLowerCase().trim();
        }

        if (StringUtils.isNotBlank(employeeSearchDto.getFullName())) {
            fullName = employeeSearchDto.getFullName().toLowerCase().trim();
        }

        if (employeeSearchDto.getStartDate() != null) {
            startDate = Utils.atStartOfDay(employeeSearchDto.getStartDate());
        } else {

            String pattern = "yyyy-MM-dd";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

            Date date = null;
            try {
                date = simpleDateFormat.parse(dateSource);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startDate = Utils.atStartOfDay(date);
        }

        if (employeeSearchDto.getEndDate() != null) {
            endDate = Utils.atEndOfDay(employeeSearchDto.getEndDate());
        } else {
            Date date = new Date();
            endDate = Utils.atEndOfDay(date);
        }


        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();


        //List<PortalUser> portalUserList = this.portalUserDao.findByDefaultPortalAccountCode(lookUpPortalAccount.getCode().toLowerCase(), email, fullName, startDate, endDate, pageable);

        Query query = this.entityManager.createQuery(
                "select distinct count(p) from PortalAccount as pa join pa.portalUsers p" +
                        " where lower(pa.code) = :lookUpPortalAccount" +
                        " and (:email is null or lower(p.email) like :email)" +
                        " and ((:fullName is null or lower(p.firstName) like :fullName)" +
                        " or (:fullName is null or lower(p.lastName) like :fullName)" +
                        " or (:fullName is null or lower(p.otherName) like :fullName))" +
                        " and p.dateCreated between :startDate and :endDate");

        query.setFirstResult((pageNumber) * pageSize);
        query.setParameter("lookUpPortalAccount", lookUpPortalAccount.getCode().toLowerCase());

        if (StringUtils.isNotBlank(email)) {
            query.setParameter("email", "%" + email + "%");
        } else {
            query.setParameter("email", email);
        }
        if (StringUtils.isNotBlank(fullName)) {
            query.setParameter("fullName", "%" + fullName + "%");
        } else {
            query.setParameter("fullName", fullName);
        }

        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setMaxResults(pageSize);

        this.entityManager.close();

        int count = ((Number) query.getSingleResult()).intValue();
        return (long) count;
    }


    @Override
    public List<PortalUser> findByRoleType(Long roleId) {
        Query query = this.entityManager.createQuery(
                "select distinct p from PortalAccountAndPortalUserRoleMappper as pa, PortalUser as p" +
                        " where lower(pa.portalUserCode) = lower(p.code)" +
                        " and pa.roleId = :roleId");
        query.setParameter("roleId", roleId);
        this.entityManager.close();
        List<PortalUser> portalUsers = query.getResultList();
        return portalUsers;
    }
}
