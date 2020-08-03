package com.hertfordshire.restfulapi.controller.lab_scientist_result_contoller;

import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.model.mongodb.LabTestResultMongoDb;
import com.hertfordshire.model.mongodb.LabTestTemplateMongoDb;
import com.hertfordshire.pojo.LabTestTemplateAndResultPojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.service.mongodb.LabTestResultMongoDbService;
import com.hertfordshire.service.mongodb.LabTestTemplateMongoDbService;
import com.hertfordshire.service.psql.lab_scientist_result.LabScientistResultService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class LabScientistResultController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(LabScientistResultController.class.getSimpleName());

//    @Autowired
//    private LabScientistResultService labScientistResultService;
//
//    @Autowired
//    private LabTestResultMongoDbService labTestResultMongoDbService;
//
//    @Autowired
//    private LabTestTemplateMongoDbService labTestTemplateMongoDbService;

    @Autowired
    private MessageUtil messageUtil;

    public LabScientistResultController() {

    }


    @PostMapping("/default/lab-scientist-result")
    public ResponseEntity<Object> index(@RequestBody OrderedLabTestSearchDto orderedLabTestSearchDto,
                                        @RequestParam("page") int page,
                                        @RequestParam("size") int size) {
        ApiError apiError = null;

            try {

                Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());

                PaginationResponsePojo paginationResponsePojo = null;
                        // = this.labScientistResultService.findByLabScientistResultWithPagination(orderedLabTestSearchDto, sortedByDateCreated);

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("template.assigned.to.lab.test", "en"), true, new ArrayList<>(), paginationResponsePojo);

            } catch (Exception e) {
                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("server.error", "en"), false, new ArrayList<>(), null);

            }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }


    @GetMapping("/default/lab-scientist-result/{id}")
    public ResponseEntity<Object> get(@PathVariable("id") Long id,
                                      @RequestParam("code") String code,
                                      @RequestParam("template_code") String templateCode) {
        ApiError apiError = null;


        // logger.info("id: "+ id + " code: "+ code);
        try {

//            LabTestTemplateMongoDb labTestTemplateMongoDb = this.labTestTemplateMongoDbService.findByCode(templateCode);
//
//            LabTestResultMongoDb labTestResultMongoDb = this.labTestResultMongoDbService.findByCode(code);

//            LabTestTemplateAndResultPojo labTestTemplateAndResultPojo = new LabTestTemplateAndResultPojo();
//            labTestTemplateAndResultPojo.setResult(labTestResultMongoDb.getData());
//            labTestTemplateAndResultPojo.setTemplate(labTestTemplateMongoDb.getData());


            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("template.assigned.to.lab.test", "en"), true, new ArrayList<>(), null);

        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("server.error", "en"), false, new ArrayList<>(), null);

        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
