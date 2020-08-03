package com.hertfordshire.service.psql.password_reset;


import com.hertfordshire.model.psql.PasswordResetToken;
import com.hertfordshire.model.psql.PortalUser;

public interface PasswordResetService {

    boolean validatePasswordResetToken(String token, Long userId);

    PasswordResetToken create(PortalUser portalUser);

}
