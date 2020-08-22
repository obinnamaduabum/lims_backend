package com.hertfordshire.restfulapi.controller.patient_result_controller;

import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.dto.DateSearchDto;
import com.hertfordshire.dto.PatientResultDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.LabTestTemplateAndResultPojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.service.psql.lab_scientist_result.LabScientistResultService;
import com.hertfordshire.service.psql.lab_test_order_details.LabTestOrderDetailsService;
import com.hertfordshire.service.psql.lab_test_result.LabTestResultService;
import com.hertfordshire.service.psql.lab_test_template.LabTestTemplateService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.service.mongodb.LabTestResultMongoDbService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.utils.errors.MyApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Optional;

@RestController
public class ProtectedPatientResultController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedPatientResultController.class.getSimpleName());

    @Autowired
    private UserService userService;

    private Gson gson;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private MyApiResponse myApiResponse;

    @Autowired
    private LabTestResultService labTestResultService;


    @Autowired
    private LabTestOrderDetailsService labTestOrderDetailsService;


    @Autowired
    private LabTestTemplateService labTestTemplateService;

    @Autowired
    private LabScientistResultService labScientistResultService;

    @Autowired
    public ProtectedPatientResultController() {
        this.gson = new Gson();
    }


    @PostMapping("/default/my-patient-result/create-result")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'PATHOLOGIST', 'MEDICAL_LAB_SCIENTIST')")
    public ResponseEntity<Object> create(@Valid @RequestBody PatientResultDto patientResultDto,
                                         HttpServletResponse res,
                                         HttpServletRequest request,
                                         Authentication authentication,
                                         BindingResult bindingResult) {


        ApiError apiError = null;
        UserDetailsDto requestPrincipal = null;

        if (bindingResult.hasErrors()) {

            logger.info(this.gson.toJson(bindingResult.getAllErrors()));

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        }
//
//        else {

            try {

                logger.info("bbbbbbbbbbbbb");

                requestPrincipal = userService.getPrincipal(res, request, authentication);

                PortalUser loggedUser = this.portalUserService.findPortalUserByEmail(requestPrincipal.getEmail());

                LabTestResultModel labTestResultModel = labTestResultService.save(patientResultDto, loggedUser);

                if (labTestResultModel != null) {
                    return this.myApiResponse.successful("", "patient.lab.test.result.created");
                } else {
                    return this.myApiResponse.notSuccessful("", "patient.lab.test.result.not.created");
                }

//                if(labTestResultMongoDb != null) {
//                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("patient.labtest.result", "en"),
//                            true, new ArrayList<>(), null);
//                } else {
//                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("patient.labtest.result.failed", "en"),
//                            false, new ArrayList<>(), null);
//
//                }

            }
            catch (Exception e) {
                return myApiResponse.internalServerErrorResponse();
            }

       // }
    }


    @PostMapping("/default/my-patient-result/list-all")
//    @PreAuthorize("hasAnyAuthority('PATIENT')")
    public ResponseEntity<Object> index(@RequestBody DateSearchDto dateSearchDto,
                                         HttpServletResponse res,
                                         HttpServletRequest request,
                                         Authentication authentication,
                                        @RequestParam("page") int page,
                                        @RequestParam("size") int size) {

        UserDetailsDto requestPrincipal = null;

        try {

            Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());

            requestPrincipal = userService.getPrincipal(res, request, authentication);

            PortalUser loggedUser = this.portalUserService.findPortalUserByEmail(requestPrincipal.getEmail());

            PaginationResponsePojo paginationResponsePojo =
            this.labTestOrderDetailsService.findAllByPatientId(loggedUser, dateSearchDto, sortedByDateCreated);


            logger.info(this.gson.toJson(paginationResponsePojo));

            return myApiResponse.successful(paginationResponsePojo, "patient.lab.test.list.of.results");

        }
        catch (Exception e) {
            e.printStackTrace();
            return myApiResponse.internalServerErrorResponse();
        }

    }


    @GetMapping("/default/my-patient-result/{id}")
    public ResponseEntity<Object> get(@PathVariable("id") Long id,
                                      @RequestParam("code") String code,
                                      @RequestParam("template_code") String templateCode) {
        ApiError apiError = null;


        ///logger.info("id: "+ id + " code: "+ code + "template code: "+ templateCode);
        try {

            LabTestOrderDetail labTestOrderDetail = this.labTestOrderDetailsService.findUniqueCode(code);
            Optional<LabTestTemplate> optionalLabTestTemplate = this.labTestTemplateService.findById(labTestOrderDetail.getLabTest().getLabTestTemplate().getId());

            Optional<LabScientistTestResultModel> optionalLabScientistTestResultModel
                    = this.labScientistResultService.findById(id);


            LabTestTemplateAndResultPojo labTestTemplateAndResultPojo = new LabTestTemplateAndResultPojo();

            if(optionalLabScientistTestResultModel.isPresent()) {
                LabScientistTestResultModel labScientistTestResultModel = optionalLabScientistTestResultModel.get();
                String result = labScientistTestResultModel.getLabResult().getContent();
                labTestTemplateAndResultPojo.setResult(result);
            }

            labTestTemplateAndResultPojo.setName(labTestOrderDetail.getLabTest().getName());

            optionalLabTestTemplate.ifPresent(labTestTemplate -> labTestTemplateAndResultPojo.setTemplate(labTestTemplate.getContent()));

            return myApiResponse.successful(labTestTemplateAndResultPojo, "template.assigned.to.lab.test");

        } catch (Exception e) {
            e.printStackTrace();

            return myApiResponse.internalServerErrorResponse();
        }
    }
}
