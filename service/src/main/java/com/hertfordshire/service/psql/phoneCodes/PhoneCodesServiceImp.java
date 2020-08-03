package com.hertfordshire.service.psql.phoneCodes;


import com.hertfordshire.model.psql.CountryPhoneCodes;
import com.hertfordshire.dao.psql.CountryPhoneCodesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class PhoneCodesServiceImp implements PhoneCodesService {


    @Autowired
    private CountryPhoneCodesDao countryPhoneCodesDao;

    @Transactional
    @Override
    public CountryPhoneCodes save(CountryPhoneCodes phoneCodes) {
        return countryPhoneCodesDao.save(phoneCodes);
    }

    @Override
    public List<CountryPhoneCodes> findAll() {
        List<CountryPhoneCodes> countryPhoneCodes = countryPhoneCodesDao.findAll();
        List<CountryPhoneCodes> countryPhoneCodes1 = new ArrayList<>();

        for (int i = 0; i < countryPhoneCodes.size(); i++) {

            CountryPhoneCodes phoneCodes = countryPhoneCodes.get(i);
            String imageUrl = countryPhoneCodes.get(i).getImageUrl();

            if(imageUrl.contains(".com")) {
                String[] split1 = imageUrl.split("\\?");
                String String2 = split1[0];
                String[] split2 = String2.split(".com");
                String dropBoxImageUrl = "https://dl.dropboxusercontent.com" + split2[1];
                phoneCodes.setImageUrl(dropBoxImageUrl);
            }

            countryPhoneCodes1.add(phoneCodes);
        }

        return countryPhoneCodes1;

    }

    @Override
    public CountryPhoneCodes findByAlpha2(String alpha2) {
        return countryPhoneCodesDao.findByAlpha2(alpha2.toLowerCase());
    }
}
