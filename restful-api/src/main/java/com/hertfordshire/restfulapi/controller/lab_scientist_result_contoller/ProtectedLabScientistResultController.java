package com.hertfordshire.restfulapi.controller.lab_scientist_result_contoller;

import com.google.gson.Gson;
import com.hertfordshire.service.psql.lab_scientist_result.LabScientistResultService;
import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.dto.OrderedLabTestSearchDto;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import com.hertfordshire.utils.errors.MyApiResponse;
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
    public ProtectedLabScientistResultController() {
        this.gson = new Gson();
    }


    @PostMapping("/default/lab-scientist-result")
    public ResponseEntity<Object> index(@RequestBody OrderedLabTestSearchDto orderedLabTestSearchDto,
                                        @RequestParam("page") int page,
                                        @RequestParam("size") int size) {

        try {

            Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());
            PaginationResponsePojo paginationResponsePojo
            = this.labScientistResultService.findByLabScientistResultWithPagination(orderedLabTestSearchDto, sortedByDateCreated);
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
