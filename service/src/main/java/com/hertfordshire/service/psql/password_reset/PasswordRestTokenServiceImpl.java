package com.hertfordshire.service.psql.password_reset;


import com.hertfordshire.model.psql.PasswordResetToken;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.psql.PasswordResetTokenDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Objects;
import java.util.UUID;

@Service
public class PasswordRestTokenServiceImpl implements PasswordResetService {

    private PasswordResetTokenDao passwordResetTokenDao;

    @Value("${passwordValidForInMinutes}")
    private int passwordValidForInMinutes;

    @Autowired
    public PasswordRestTokenServiceImpl(PasswordResetTokenDao passwordResetTokenDao) {
        this.passwordResetTokenDao = passwordResetTokenDao;
    }

    @Override
    public boolean validatePasswordResetToken(String token, Long userId) {

        PasswordResetToken passwordResetToken = passwordResetTokenDao.findByTokenAndTokenExpired(token, false);

        if(passwordResetToken == null || (!Objects.equals(passwordResetToken.getPortalUser().getId(), userId))){
            return false;
        }

        Calendar cal = Calendar.getInstance();
        if ((passwordResetToken.getExpiryDate()
                .getTime() - cal.getTime()
                .getTime()) <= 0) {

            passwordResetToken.setTokenExpired(true);
            passwordResetTokenDao.save(passwordResetToken);
            return false;
        }
        return true;
    }

    @Override
    public PasswordResetToken create(PortalUser portalUser) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setPortalUser(portalUser);
        passwordResetToken.setToken(token);
        passwordResetToken.setExpiryDate(this.passwordValidForInMinutes);
        return passwordResetTokenDao.save(passwordResetToken);
    }
}
