package com.hertfordshire.service.psql.country;



import com.hertfordshire.model.psql.Country;

import java.util.List;

public interface CountryService {

    Country save(Country country);

    List<Country> findAll();

    Country findByAlpha2(String alpha2);

}
