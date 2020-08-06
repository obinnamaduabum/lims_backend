package com.hertfordshire.restfulapi.controller.lab_test_category_controller;

import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestCategory;
import com.hertfordshire.pojo.LabTestCategoryPojo;
import com.hertfordshire.pojo.LabTestPojo;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.service.psql.lab_test_categories.LabTestCategoriesService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
public class PublicLabTestCategoryController extends PublicBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(PublicLabTestCategoryController.class.getSimpleName());

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private LabTestCategoriesService labTestCategoriesService;

    @Autowired
    private LabTestService labTestService;


    @GetMapping("/default/lab-category")
    public ResponseEntity<Object> findAllCategories() {

        ApiError apiError = null;

        try {

            List<LabTestCategory> labTestCategories = this.labTestCategoriesService.findAll();
            List<LabTestCategoryPojo> labTestCategoryPojos = new ArrayList<>();
            for (LabTestCategory labTestCategory : labTestCategories) {
                LabTestCategoryPojo labTestCategoryPojo = new LabTestCategoryPojo();
                labTestCategoryPojo.setName(labTestCategory.getName());
                labTestCategoryPojo.setId(labTestCategory.getId());
                labTestCategoryPojos.add(labTestCategoryPojo);
            }
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.failed", "en"), true, new ArrayList<>(), labTestCategoryPojos);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.failed", "en"), false, new ArrayList<>(), null);

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @GetMapping("/default/lab-category-and-tests")
    public ResponseEntity<Object> findAllCategoriesAndTests() {

        ApiError apiError = null;

        try {
            List<LabTestCategory> labTestCategories = this.labTestCategoriesService.findAll();
            List<LabTestCategoryPojo> labTestCategoryPojos = new ArrayList<>();
            for (LabTestCategory labTestCategory : labTestCategories) {
                LabTestCategoryPojo labTestCategoryPojo = new LabTestCategoryPojo();
                labTestCategoryPojo.setName(labTestCategory.getName());


                List<LabTest> labTests = this.labTestService.findByCategoryName(labTestCategory);
                List<LabTestPojo> labTestPojos = new ArrayList<>();

                for (LabTest labTest : labTests) {
                    LabTestPojo labTestPojo = new LabTestPojo();
                    labTestPojo.setName(labTest.getName());
                    labTestPojos.add(labTestPojo);
                }

                labTestCategoryPojo.setLabTestPojos(labTestPojos);
                labTestCategoryPojos.add(labTestCategoryPojo);
            }
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.failed", "en"), true, new ArrayList<>(), labTestCategoryPojos);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.failed", "en"), false, new ArrayList<>(), null);

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @GetMapping("/default/lab-category/fetch-lab-tests/{id}")
    public ResponseEntity<Object> findAllCategories(@PathVariable("id") Long id) {

        ApiError apiError = null;
        List<LabTestPojo> labTestPojos = new ArrayList<>();

        try {

            Optional<LabTestCategory> optionalLabTestCategory = this.labTestCategoriesService.findById(id);

            if(optionalLabTestCategory.isPresent()){

                List<LabTest> labTestList = this.labTestService.findByCategoryName(optionalLabTestCategory.get());

                for(LabTest labTest :labTestList){
                    LabTestPojo labTestPojo = new LabTestPojo();
                    labTestPojo.setCategoryName(optionalLabTestCategory.get().getName());
                    labTestPojo.setCode(labTest.getCode());
                    labTestPojo.setId(labTest.getId());
                    labTestPojo.setName(labTest.getName());
                    labTestPojos.add(labTestPojo);
                }

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.tests.found", "en"), true, new ArrayList<>(), labTestPojos);
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("category.not.found", "en"), false, new ArrayList<>(), null);
            }



        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("server.error", "en"), false, new ArrayList<>(), null);

        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
