package com.hertfordshire.restfulapi.controller.lab_test_template_controller;

import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.dto.LabTestTemplateCreateDto;
import com.hertfordshire.dto.LabTestTemplateEditDto;
import com.hertfordshire.model.mongodb.LabTestTemplateMongoDb;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestTemplate;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.LabTestTemplatePojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.service.psql.lab_test_template.LabTestTemplateService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.errors.MyApiResponse;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;

@RestController
public class ProtectedLabTestTemplateController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedLabTestTemplateController.class.getSimpleName());

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private LabTestTemplateService labTestTemplateService;

    @Autowired
    private LabTestService labTestService;

    @GetMapping("/default/lab_test_template")
    public ResponseEntity<Object> index(@RequestParam("page") int page,
                                        @RequestParam("size") int size) {

        ApiError apiError;

        try {

            if(size == 0) {
                size = 10;
            }

            Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("name").descending());

            PaginationResponsePojo paginationResponsePojo = this.labTestTemplateService.findAll(sortedByDateCreated);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.templates.found", "en"),
                    true, new ArrayList<>(), paginationResponsePojo);

        } catch (Exception e) {
            e.printStackTrace();
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("server.error", "en"),
                    false, new ArrayList<>(), null);
        }
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }

    @PostMapping("/default/lab_test_template/create")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'PATHOLOGIST', 'MEDICAL_LAB_SCIENTIST')")
    public ResponseEntity<Object> create(@Valid @RequestBody LabTestTemplateCreateDto labTestTemplateCreateDto,
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

                LabTestTemplate foundLabTestTemplate =
                this.labTestTemplateService.findByName(labTestTemplateCreateDto.getTitle());

                if(foundLabTestTemplate != null) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.already.exists", "en"),
                            false, new ArrayList<>(), null);
                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                }

                LabTestTemplate labTestTemplate = this.labTestTemplateService.save(labTestTemplateCreateDto, loggedUser);

                if (labTestTemplate != null) {
                    apiError = new ApiError(HttpStatus.CREATED.value(), HttpStatus.CREATED, messageUtil.getMessage("lab.test.template.create.success", "en"),
                            true, new ArrayList<>(), null);
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.create.failed", "en"),
                            false, new ArrayList<>(), null);
                }

            } catch (Exception e) {
                e.printStackTrace();
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.create.failed", "en"),
                        false, new ArrayList<>(), null);
            }
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }

    @GetMapping("/default/lab_test_template/{code}")
    public ResponseEntity<Object> findByCode(@PathVariable("code") String code) {

        ApiError apiError = null;

        try {

            LabTestTemplateMongoDb labTestTemplateMongoDb = null;
                    // = this.labTestTemplateMongoDbService.findByCode(code.toLowerCase());

            if (labTestTemplateMongoDb != null) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.found", "en"),
                        true, new ArrayList<>(), labTestTemplateMongoDb);
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.notfound", "en"),
                        false, new ArrayList<>(), null);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new MyApiResponse().internalServerErrorResponse();
        }
        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @PostMapping("/default/lab_test_template/edit")
    public ResponseEntity<Object> update(@Valid @RequestBody LabTestTemplateEditDto labTestTemplateEditDto,
                                         BindingResult bindingResult) {
        ApiError apiError = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        }

        try {

            LabTestTemplateMongoDb labTestTemplateMongoDb = null;
            // = this.labTestTemplateMongoDbService.findByCode(labTestTemplateEditDto.getCode());

            if (labTestTemplateMongoDb != null) {

                LabTestTemplateMongoDb updatedLabTestTemplateMongoDb = null;
                // = this.labTestTemplateMongoDbService.edit(labTestTemplateMongoDb, labTestTemplateEditDto);

                if (updatedLabTestTemplateMongoDb != null) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.updated", "en"),
                            true, new ArrayList<>(), null);
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.templates.not.updated", "en"),
                            false, new ArrayList<>(), null);
                }
            } else {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.notfound", "en"),
                        false, new ArrayList<>(), null);
            }

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
            return new MyApiResponse().internalServerErrorResponse();
        }

    }


    @GetMapping("/default/lab_test_template/remove-assignment/{code}")
    public ResponseEntity<Object> removeAssignment(@PathVariable("code") String code) {

        ApiError apiError = null;
        try {

            LabTest labTest = this.labTestService.findByResultTemplateId(code);

            if(labTest != null) {
                if (TextUtils.isBlank(labTest.getResultTemplateId())) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.notfound", "en"),
                            false, new ArrayList<>(), null);

                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

                } else {

                    LabTestTemplatePojo labTestTemplatePojo = null;
                            // = this.labTestTemplateMongoDbService.removeAssignment(code.toLowerCase(), labTest);

                    if (labTestTemplatePojo != null) {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.found", "en"),
                                true, new ArrayList<>(), labTestTemplatePojo);
                    } else {
                        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.template.notfound", "en"),
                                false, new ArrayList<>(), null);
                    }
                }

            } else {

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.test.not.assigned", "en"),
                        false, new ArrayList<>(), null);
                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }

            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {
            e.printStackTrace();
            return new MyApiResponse().internalServerErrorResponse();
        }

    }
}
