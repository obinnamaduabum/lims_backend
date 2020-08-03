package com.hertfordshire.service.psql.country;

import com.hertfordshire.model.psql.Country;
import com.hertfordshire.dao.psql.CountryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryServiceImp implements CountryService {

    @Autowired
    private CountryDao countryDao;

    @Override
    public Country save(Country country) {
        this.countryDao.save(country);
        return country;
    }

    @Override
    public Country findByAlpha2(String alpha2) {
        return countryDao.findByAlpha2(alpha2.toLowerCase());
    }


    @Override
    public List<Country> findAll() throws RuntimeException {
        return this.countryDao.findAll();
    }
}
