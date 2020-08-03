package com.hertfordshire.dao.psql;


import com.hertfordshire.model.psql.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryDao extends JpaRepository<Country, Long> {
    @Query("SELECT c FROM Country as c WHERE lower(c.alpha2) = ?1")
    Country findByAlpha2(String alpha2);
}
