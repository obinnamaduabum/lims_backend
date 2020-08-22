package com.hertfordshire.restfulapi.controller.lab_test_controller;

import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.dto.LabTestDto;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.pojo.ErrorsPojo;
import com.hertfordshire.restfulapi.controller.lab_test_category_controller.ProtectedLabTestCategoryController;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.errors.MyApiResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.hertfordshire.access.transformation.Transformer.getStringValue;

@RestController
public class ProtectedLabTestController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedLabTestCategoryController.class.getSimpleName());

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private MyApiResponse myApiResponse;

    @PostMapping("/default/lab-test/test/upload")
    public ResponseEntity<Object> singleFileUpload(@RequestParam("file") MultipartFile multipartFile) {

        ApiError apiError = null;
        InputStream inputstream = null;
        List<ErrorsPojo> errorsPojoList = new ArrayList<>();

        if (multipartFile.isEmpty()) {
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK,
                    messageUtil.getMessage("file.empty", "en"), false, new ArrayList<>(), null);
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }

        try {

            try {
                inputstream = multipartFile.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }


            if (inputstream != null) {
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

                    LabTest labTest =  this.labTestService.findByName(labTestName);
                    if(labTest != null) {
                        ErrorsPojo errorsPojo = new ErrorsPojo();
                        errorsPojo.setName(labTest.getName().toLowerCase()+ " already exists");
                        errorsPojoList.add(errorsPojo);
                    } else {
                        LabTestDto labTestDto = new LabTestDto();
                        labTestDto.setName(labTestName);
                        labTestDto.setCategoryName(labTestCategoryName);
                        labTestDto.setPriceInNaira(priceInNaira);
                        labTestDto.setPriceInUSD(priceInUSD);
                        labTestDto.setPriceInEuro(priceInEuro);
                        this.labTestService.save(labTestDto);
                    }
                }

                if(errorsPojoList.size() <= 0) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.successful", "en"), true, new ArrayList<>(), null);
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK,messageUtil.getMessage("file.upload.failed", "en"), false, new ArrayList<>(), errorsPojoList);
                }

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.failed", "en"), false, new ArrayList<>(), null);

                return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
            }


        } catch (Exception e) {
            return myApiResponse.internalServerErrorResponse();
        }
    }
}
