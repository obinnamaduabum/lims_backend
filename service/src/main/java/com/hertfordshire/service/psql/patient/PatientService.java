package com.hertfordshire.service.psql.patient;


import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dto.PatientDto;
import com.hertfordshire.dto.PatientInfoUpdate;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequenceService;
import com.hertfordshire.service.sequence.portal_user_id.PortalUserSequenceService;
import org.springframework.security.crypto.password.PasswordEncoder;

public interface PatientService {

    PortalUser create(PatientDto patientDto);

    PortalUser updatePatient(PatientInfoUpdate employeeDto, PortalUser portalUser, PortalAccount foundPortalAccount);
}
