package com.hertfordshire.access.transformation;

import com.hertfordshire.model.psql.LabTestCategory;
import com.hertfordshire.service.psql.lab_test_categories.LabTestCategoriesService;
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
public class CreateLabTestCategoriesTransformer {

    private static final Logger logger = Logger.getLogger(CreateLabTestCategoriesTransformer.class.getSimpleName());

    @Autowired
    private LabTestCategoriesService labTestCategoriesService;

    public void create() throws IOException {

        List<LabTestCategory> labTestCategories = new ArrayList<>();
        try {
                labTestCategories = labTestCategoriesService.findAll();
                logger.info("Lab test categories exists");
                logger.info("number of lab test categories :"+ labTestCategories.size());

        } catch (Exception e){
            e.printStackTrace();
        }


        if(labTestCategories.size() > 0) {
            return;
        }


        InputStream inputstream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + EXCEL_FOLDER + File.separator + LAB_TEST_CATEGORIES);
        XSSFWorkbook workbook = new XSSFWorkbook(inputstream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.rowIterator();
        rowIterator.next();

        logger.info("Started adding lab test categories");

        while (rowIterator.hasNext()) {

            Row row = rowIterator.next();

            String labTestCategoryName = StringUtils.strip(getStringValue(row.getCell(1)).toUpperCase());

            LabTestCategory newLabTestCategory = new LabTestCategory();
            newLabTestCategory.setName(labTestCategoryName);
            this.labTestCategoriesService.save(newLabTestCategory);
        }

        logger.info("Done adding lab test categories");
    }
}

