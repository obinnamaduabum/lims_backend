package com.hertfordshire.service.psql.email_verification;


import com.hertfordshire.model.psql.EmailVerificationToken;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.psql.EmailVerificationDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.service.sequence.email_verification.EmailVerificationTokenSequence;
import com.hertfordshire.utils.lhenum.GenericStatusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Objects;

@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Autowired
    private EmailVerificationDao emailVerificationDao;

    @Autowired
    private PortalUserDao portalUserDao;

    @Autowired
    private EmailVerificationTokenSequence emailVerificationTokenSequence;

    @Value("${emailValidForInMinutes}")
    private int emailValidForInMinutes;

    @Override
    public EmailVerificationToken generateToken(PortalUser portalUser) {

        String token = String.format("U_SE_%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                emailVerificationTokenSequence.getNextId()
        );

        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setPortalUser(portalUser);
        emailVerificationToken.setToken(token);
        emailVerificationToken.setExpiryDate(emailValidForInMinutes);
        return emailVerificationDao.save(emailVerificationToken);
    }



    @Override
    public boolean validatePasswordResetToken(String token, Long userId) {

        EmailVerificationToken emailVerificationToken = emailVerificationDao.findByTokenAndTokenExpired(token, false);

        if(emailVerificationToken == null || (!Objects.equals(emailVerificationToken.getPortalUser().getId(), userId))){
            return false;
        }


        Calendar cal = Calendar.getInstance();
        if ((emailVerificationToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {

            emailVerificationToken.setTokenExpired(true);
            emailVerificationDao.save(emailVerificationToken);
            return false;
        }

        PortalUser portalUser = emailVerificationToken.getPortalUser();
        portalUser.setUserStatus(GenericStatusConstant.ACTIVE);
        portalUser.setEmailOrPhoneNumberIsVerified(true);
        portalUser.setEmailVerified(true);
        portalUserDao.save(portalUser);

        return true;
    }
}
