package com.hertfordshire.restfulapi.controller.lab_scientist_result_contoller;

import com.google.gson.Gson;
import com.hertfordshire.access.config.dto.UserDetailsDto;
import com.hertfordshire.access.config.service.user_service.UserService;
import com.hertfordshire.dao.psql.LabScientistTestResultDao;
import com.hertfordshire.dao.psql.LabTestResultModelDao;
import com.hertfordshire.dto.PatientResultDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.LabTestTemplateAndResultPojo;
import com.hertfordshire.service.psql.lab_scientist_result.LabScientistResultService;
import com.hertfordshire.service.psql.lab_test_order_details.LabTestOrderDetailsService;
import com.hertfordshire.service.psql.lab_test_result.LabTestResultService;
import com.hertfordshire.service.psql.lab_test_template.LabTestTemplateService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.errors.CustomBadRequestException;
import com.hertfordshire.utils.errors.MyApiResponse;
import com.hertfordshire.utils.lhenum.LabScientistStatusConstant;
import jdk.nashorn.internal.runtime.options.Option;
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
import java.util.ArrayList;
import java.util.Optional;

@RestController
public class ProtectedLabScientistResultController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedLabScientistResultController.class.getSimpleName());

    @Autowired
    private LabScientistResultService labScientistResultService;

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private MyApiResponse apiResponse;

    private Gson gson;

    @Autowired
    private UserService userService;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private MyApiResponse myApiResponse;

    @Autowired
    private LabTestResultService labTestResultService;

    @Autowired
    private LabTestTemplateService labTestTemplateService;


    @Autowired
    private LabTestOrderDetailsService labTestOrderDetailsService;



    @Autowired
    public ProtectedLabScientistResultController() {
        this.gson = new Gson();
    }


    @PostMapping("/default/lab-scientist-result")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'PATHOLOGIST', 'MEDICAL_LAB_SCIENTIST')")
    public ResponseEntity<Object> index(@RequestBody OrderedLabTestSearchDto orderedLabTestSearchDto,
                                        @RequestParam("page") int page,
                                        @RequestParam("size") int size) {

        try {

            Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());
            PaginationResponsePojo paginationResponsePojo = this.labScientistResultService.findByLabScientistResultWithPagination(orderedLabTestSearchDto, sortedByDateCreated);
            return this.apiResponse.successful(paginationResponsePojo, "template.assigned.to.lab.test");

        } catch (Exception e) {
            e.printStackTrace();
            return this.apiResponse.internalServerErrorResponse();
        }
    }


    @GetMapping("/default/lab-scientist-result/{id}")
    public ResponseEntity<Object> get(@PathVariable("id") Long id,
                                      @RequestParam("code") String code,
                                      @RequestParam("template_code") String templateCode) {
        ApiError apiError = null;


        logger.info("id: "+ id + " code: "+ code + "template code: "+ templateCode);
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

            optionalLabTestTemplate.ifPresent(labTestTemplate -> labTestTemplateAndResultPojo.setTemplate(labTestTemplate.getContent()));

            return myApiResponse.successful(labTestTemplateAndResultPojo, "template.assigned.to.lab.test");

        } catch (Exception e) {
            e.printStackTrace();

            return myApiResponse.internalServerErrorResponse();
        }
    }


//    @PostMapping("/default/lab-scientist-result/create")
//    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN', 'PATHOLOGIST', 'MEDICAL_LAB_SCIENTIST')")
//    public ResponseEntity<Object> create(@RequestBody PatientResultDto patientResultDto,
//                                         BindingResult bindingResult,
//                                         HttpServletResponse res,
//                                         HttpServletRequest request,
//                                         Authentication authentication) {
//        try {
//
//            if (bindingResult.hasErrors()) {
//
//                bindingResult.getAllErrors().forEach(objectError -> {
//                    logger.info(objectError.toString());
//                });
//
//                throw new CustomBadRequestException();
//
//            }
//
//            UserDetailsDto requestPrincipal = null;
//
//            requestPrincipal = userService.getPrincipal(res, request, authentication);
//
//            PortalUser loggedUser = this.portalUserService.findPortalUserByEmail(requestPrincipal.getEmail());
//
//            LabTestResultModel labTestResultModel = labTestResultService.save(patientResultDto, loggedUser);
//
//            if (labTestResultModel != null) {
//                return this.myApiResponse.successful("", "patient.lab.test.result.created");
//            } else {
//                return this.myApiResponse.notSuccessful("", "patient.lab.test.result.not.created");
//            }
//
//        } catch (Exception e) {
//            return this.myApiResponse.internalServerErrorResponse();
//        }
//    }
}
