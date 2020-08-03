package com.hertfordshire.access.transformation;


import com.hertfordshire.model.psql.Country;
import com.hertfordshire.model.psql.CountryPhoneCodes;
import com.hertfordshire.service.psql.country.CountryService;
import com.hertfordshire.service.psql.phoneCodes.PhoneCodesService;
import com.hertfordshire.utils.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import static com.hertfordshire.access.transformation.Transformer.*;


@Component
public class CreatePhoneNumberCodeTransformer {

    private static final Logger logger = Logger.getLogger(CreatePhoneNumberCodeTransformer.class.getSimpleName());

    @Autowired
    private PhoneCodesService phoneCodesService;

    @Autowired
    private CountryService countryService;

    public void create() throws IOException {


        List<CountryPhoneCodes> countryPhoneCodesList = new ArrayList<>();
        try {
            countryPhoneCodesList = phoneCodesService.findAll();
            logger.info("number of phone code countries :" + countryPhoneCodesList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }


        if (countryPhoneCodesList.size() > 0) {
            return;
        }


        InputStream inputstream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + EXCEL_FOLDER + File.separator + PHONE_NUMBER_COUNTRIES_FILE_NAME);
        XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();

        logger.info("Started adding phone codes");

        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();

            String alpha3 = StringUtils.strip(getStringValue(row.getCell(3)).toUpperCase());
            String alpha2 = StringUtils.strip(getStringValue(row.getCell(2)).toUpperCase());
            String countryName = StringUtils.strip(getStringValue(row.getCell(1)).toUpperCase());
            String phoneCode = StringUtils.strip(getStringValue(row.getCell(4)).toUpperCase());

            ArrayList<Country> countries;
            List<Country> list = countryService.findAll();
            countries = new ArrayList<>(list);
            CountryPhoneCodes phoneCodes = null;

            if (countries.size() > 0) {


                try {
                    phoneCodes = phoneCodesService.findByAlpha2(alpha2.toLowerCase());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (phoneCodes == null) {

                    try {
                        CountryPhoneCodes countryPhoneCodes = new CountryPhoneCodes();
                        countryPhoneCodes.setAlpha2(alpha2);
                        countryPhoneCodes.setAlpha3(alpha3);
                        countryPhoneCodes.setImageUrl("xxxxx");
                        countryPhoneCodes.setInternationalPhoneNumber(phoneCode);
                        countryPhoneCodes.setName(countryName);
                        phoneCodesService.save(countryPhoneCodes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        logger.info("Done adding phone codes");
    }
}

