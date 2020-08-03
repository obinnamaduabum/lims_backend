package com.hertfordshire.access.transformation;

import com.hertfordshire.dto.ImageResolutionDto;
import com.hertfordshire.model.psql.Country;
import com.hertfordshire.service.psql.country.CountryService;
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
public class CreateCountryTransformer {

    private static final Logger logger = Logger.getLogger(CreateCountryTransformer.class.getSimpleName());

    @Autowired
    private CountryService countryService;


    public void createCountriesTransformer() throws IOException {

        List<Country> countryList = new ArrayList<>();
        try {
                countryList = countryService.findAll();
                logger.info("Countries already exists");
                logger.info("number of countries :"+ countryList.size());

        } catch (Exception e){
            e.printStackTrace();
        }


        if(countryList.size() > 0) {
            return;
        }


        InputStream inputstream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + EXCEL_FOLDER + File.separator + COUNTRIES_FILE_NAME);
        XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();

        logger.info("Started adding countries");

        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();

            String alpha3 = StringUtils.strip(getStringValue(row.getCell(0)).toUpperCase());
            String alpha2 = StringUtils.strip(getStringValue(row.getCell(2)).toUpperCase());
            String countryName = StringUtils.strip(getStringValue(row.getCell(1)).toUpperCase());


            List<ImageResolutionDto> imageResolutionDtos = new ArrayList<>();


//            FileModel fileModel = initializeFileUploadService.saveFile(
//                    multipartFile,
//                    "file_server",
//                    imageResolutionDtos,
//                    DataStorageConstant.DROP_BOX,
//                    FileLocationConstant.MAIN_FOLDER_LOCATION_SERVER);

            Country country = new Country();
            country.setAlpha2(alpha2);
            country.setAlpha3(alpha3);
            country.setName(countryName);
            country.setInternationalPhoneNumber(getStringValue(row.getCell(4)).toUpperCase());
            countryService.save(country);
        }

        logger.info("Done adding country");
    }
}

