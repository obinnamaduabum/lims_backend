package com.hertfordshire.service.psql.email_verification;


import com.hertfordshire.model.psql.EmailVerificationToken;
import com.hertfordshire.model.psql.PortalUser;

public interface EmailVerificationService {

    boolean validatePasswordResetToken(String token, Long userId);

    EmailVerificationToken generateToken(PortalUser portalUser);
}
