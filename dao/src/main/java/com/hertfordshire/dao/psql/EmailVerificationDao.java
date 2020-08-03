package com.hertfordshire.dao.psql;



import com.hertfordshire.model.psql.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailVerificationDao extends JpaRepository<EmailVerificationToken, Long> {

    EmailVerificationToken findByTokenAndTokenExpired(String token, boolean expired);
}
