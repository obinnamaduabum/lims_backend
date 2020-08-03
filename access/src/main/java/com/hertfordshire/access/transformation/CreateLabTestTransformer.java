package com.hertfordshire.access.transformation;

import com.hertfordshire.dto.LabTestDto;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.service.psql.lab_test.LabTestService;
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
public class CreateLabTestTransformer {

    private static final Logger logger = Logger.getLogger(CreateLabTestTransformer.class.getSimpleName());

    @Autowired
    private LabTestService labTestService;

    public void create() throws IOException {

        List<LabTest> labTestList = new ArrayList<>();
        try {
                labTestList = labTestService.findAll();
                logger.info("Lab test categories exists");
                logger.info("number of lab test categories :"+ labTestList.size());

        } catch (Exception e){
            e.printStackTrace();
        }


        if(labTestList.size() > 0) {
            return;
        }


        InputStream inputstream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + EXCEL_FOLDER + File.separator + LAB_TEST);
        XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();

        logger.info("Started adding lab test categories");

        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();

            String labTestCategoryName = StringUtils.strip(getStringValue(row.getCell(1)).toUpperCase());
            String labTestName = StringUtils.strip(getStringValue(row.getCell(2)).toUpperCase());
            String priceInNaira = StringUtils.strip(getStringValue(row.getCell(3)).toUpperCase());

            String priceInUSD = StringUtils.strip(getStringValue(row.getCell(4)).toUpperCase());

            String priceInEuro = StringUtils.strip(getStringValue(row.getCell(5)).toUpperCase());

            LabTestDto labTestDto = new LabTestDto();
            labTestDto.setName(labTestName);
            labTestDto.setCategoryName(labTestCategoryName);
            labTestDto.setPriceInNaira(priceInNaira);
            labTestDto.setPriceInUSD(priceInUSD);
            labTestDto.setPriceInEuro(priceInEuro);

            this.labTestService.save(labTestDto);
        }

        logger.info("Done adding lab test categories");
    }
}

