package com.hertfordshire.service.psql.portalaccount;

import com.google.gson.Gson;
import com.hertfordshire.dto.EmployeeSearchDto;
import com.hertfordshire.dto.PortalAccountDto;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.dao.mongodb.AuditTrailMongoDbDao;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.PortalAccountDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequence;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequenceService;
import com.hertfordshire.service.sequence.portal_user_id.PortalUserSequence;
import com.hertfordshire.service.sequence.portal_user_id.PortalUserSequenceService;
import com.hertfordshire.utils.lhenum.PortalAccountTypeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class PortalAccountServiceImp implements PortalAccountService {

    private static final Logger logger = Logger.getLogger(PortalAccountServiceImp.class.getSimpleName());


    private PortalAccountDao portalAccountDao;

    private RolesDao rolesDao;


    @Autowired
    @PortalUserSequence
    private PortalUserSequenceService portalUserSequenceService;


    @Autowired
    PortalAccountSequenceService portalAccountSequenceService;


    @Autowired
    public PortalAccountServiceImp(PortalAccountDao portalAccountDao,
                                   RolesDao rolesDao,
                                   PortalAccountSequenceService portalAccountSequence) {
        this.portalAccountDao = portalAccountDao;
        this.rolesDao = rolesDao;
    }


    public void setPortalAccountDao(PortalAccountDao portalAccountDao) {
        this.portalAccountDao = portalAccountDao;
    }

    public RolesDao getRolesDao() {
        return rolesDao;
    }

    public void setRolesDao(RolesDao rolesDao) {
        this.rolesDao = rolesDao;
    }


    @Override
    public PortalAccount findPortalAccountByPortalAccountId(String portalAccountId) {
        return portalAccountDao.findPortalAccountByCode(portalAccountId);
    }

    @Override
    public PortalAccount findPortalAccountByName(String name) {
        return portalAccountDao.findPortalAccountByPortalAccountName(name.toLowerCase());
    }


    @Override
    public PortalAccount save(PortalAccountDto portalAccountDto) {
        PortalAccount portalAccount = new PortalAccount();
        portalAccount.setName(portalAccountDto.getName());

        String portalAccountId = String.format("PA_%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                portalAccountSequenceService.getNextId()
        );

        portalAccount.setCode(portalAccountId);
        portalAccount.setPortalAccountType(PortalAccountTypeConstant.valueOf(portalAccountDto.getPortalAccountTypes()));
        portalAccount = portalAccountDao.save(portalAccount);
        return portalAccount;
    }


    @Override
    public PortalAccount saveOrUpdate(PortalAccountDto portalAccountDto) {
        return null;
    }

    @Override
    public List<PortalAccount> findAll() {
        return this.portalAccountDao.findAll();
    }

    @Override
    public PaginationResponsePojo findAllByEmployee(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable) {
        return null;
    }

    @Override
    public Long findAllByEmployeeCount(EmployeeSearchDto employeeSearchDto, PortalAccount lookUpPortalAccount, Pageable pageable) {
       return null;
    }
}
