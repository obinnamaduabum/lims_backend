package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.CountryPhoneCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryPhoneCodesDao extends JpaRepository<CountryPhoneCodes, Long> {

    CountryPhoneCodes findByAlpha2(String alpha2);
}
