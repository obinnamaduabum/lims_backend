package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetTokenDao extends JpaRepository<PasswordResetToken, Long> {

    PasswordResetToken findByTokenAndTokenExpired(String token, boolean expired);
}
