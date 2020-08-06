package com.hertfordshire.restfulapi.controller.lab_test_category_controller;

import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.dto.LabTestAssignmentDto;
import com.hertfordshire.model.mongodb.LabTestTemplateAssignmentHistoryMongoDb;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.ErrorsPojo;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.service.psql.lab_test_categories.LabTestCategoriesService;
import com.hertfordshire.service.psql.lab_test_template.LabTestTemplateService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.hertfordshire.access.transformation.Transformer.getStringValue;


@RestController
public class ProtectedLabTestCategoryController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedLabTestCategoryController.class.getSimpleName());

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private LabTestCategoriesService labTestCategoriesService;

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private UserService userService;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private LabTestTemplateService labTestTemplateService;


    @Autowired
    private MyApiResponse myApiResponse;

//    @Autowired
//    private LabTestTemplateAssignmentHistoryMongoDbService labTestTemplateAssignmentHistoryMongoDbService;

    @PostMapping("/default/lab-category/category/upload")
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

                    LabTestCategory foundLabTestCategory = this.labTestCategoriesService.findByName(labTestCategoryName);

                    if(foundLabTestCategory == null) {
                        LabTestCategory newLabTestCategory = new LabTestCategory();
                        newLabTestCategory.setName(labTestCategoryName);
                        this.labTestCategoriesService.save(newLabTestCategory);
                    } else {
                        ErrorsPojo errorsPojo = new ErrorsPojo();
                        errorsPojo.setName(foundLabTestCategory.getName().toLowerCase()+ " already exists");
                        errorsPojoList.add(errorsPojo);
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
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("file.upload.failed", "en"), false, new ArrayList<>(), null);

            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @PostMapping("/default/lab-category/assign-to-template")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'PATHOLOGIST', 'MEDICAL_LAB_SCIENTIST')")
    public ResponseEntity<Object> assignToTemplate(@Valid @RequestBody LabTestAssignmentDto labTestAssignmentDto,
                                                   BindingResult bindingResult,
                                                   HttpServletResponse res,
                                                   HttpServletRequest request,
                                                   Authentication authentication) {
        ApiError apiError = null;
        PortalUser portalUser = null;
        PortalAccount portalAccount = null;
        UserDetailsDto requestPrincipal = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            try {

                requestPrincipal = userService.getPrincipal(res, request, authentication);

                PortalUser loggedUser = this.portalUserService.findPortalUserByEmail(requestPrincipal.getEmail());

                Optional<LabTest> optionalLabTest = this.labTestService.findById(labTestAssignmentDto.getActualLabTestId());

                if(optionalLabTest.isPresent()) {

                    LabTestTemplate labTestTemplate = this.labTestTemplateService.findByCode(labTestAssignmentDto.getLabTestTemplateId());

                    if(labTestTemplate != null) {
                        LabTestTemplate storedLabTestTemplate = this.labTestTemplateService.assign(labTestTemplate, optionalLabTest.get(), loggedUser);

                        if(storedLabTestTemplate == null) {
                            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("template.assignment.failed.to.lab.test", "en"), false, new ArrayList<>(), null);
                            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                        }

                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("template.assignment.failed.to.lab.test", "en"), false, new ArrayList<>(), null);
                        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                    }
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("template.assignment.failed.to.lab.test", "en"), false, new ArrayList<>(), null);
                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                }

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("template.assigned.to.lab.test", "en"), true, new ArrayList<>(), null);


                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            } catch (Exception e) {
                e.printStackTrace();

                return myApiResponse.internalServerErrorResponse();
            }
        }


    }

    @GetMapping("/default/lab-category/assign/history-by-actual-latest-id/{id}")
    public ResponseEntity<Object> findAllLabTestAssignHistoryByActualLabTestId(@PathVariable("id") String id) {

        ApiError apiError = null;
        try {


            List<LabTestTemplateAssignmentHistoryMongoDb> labTestTemplateAssignmentHistoryMongoDbs  = null;

                    // = this.labTestTemplateAssignmentHistoryMongoDbService.findAllByActualLabTestId(id);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.tests.found", "en"), true, new ArrayList<>(), labTestTemplateAssignmentHistoryMongoDbs);

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return myApiResponse.internalServerErrorResponse();
        }

    }

    @GetMapping("/default/lab-category/assign/history-by-template-id/{id}")
    public ResponseEntity<Object> findAllLabTestAssignHistoryByTemplateId(@PathVariable("id") String id) {

        ApiError apiError = null;
        try {

            List<LabTestTemplateAssignmentHistoryMongoDb> labTestTemplateAssignmentHistoryMongoDbs = null;
                    // = this.labTestTemplateAssignmentHistoryMongoDbService.findByLabTestTemplateIdOOrderByDateCreatedDesc(id);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.tests.found", "en"), true, new ArrayList<>(), labTestTemplateAssignmentHistoryMongoDbs);

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {
            e.printStackTrace();

            return myApiResponse.internalServerErrorResponse();
        }
    }
}
