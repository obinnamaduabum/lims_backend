package com.hertfordshire.restfulapi.controller;

import com.hertfordshire.service.psql.country.CountryService;
import com.hertfordshire.utils.ResponsePojo;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CountryController extends PublicBaseApiController {


    private static final Logger logger = LoggerFactory.getLogger(CountryController.class);

    private CountryService countryService;


    @Autowired
    public void setCountryService(CountryService countryService){
        this.countryService = countryService;
    }

    @GetMapping("/default/countries/")
    public ResponsePojo index(){

        ResponsePojo responsePojo = new ResponsePojo();
        responsePojo.setSuccess(true);
        responsePojo.setCode(200);
        responsePojo.setMessage("List of Countries");
        responsePojo.setData(countryService.findAll());
        return responsePojo;
    }
}
