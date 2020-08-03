package com.hertfordshire.service.psql.phoneCodes;



import com.hertfordshire.model.psql.CountryPhoneCodes;

import java.util.List;

public interface PhoneCodesService {

    CountryPhoneCodes save(CountryPhoneCodes phoneCodes);

    List<CountryPhoneCodes> findAll();

    CountryPhoneCodes findByAlpha2(String alpha2);

}
