package com.hertfordshire.restfulapi.controller.country;


import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.model.psql.Country;
import com.hertfordshire.service.psql.country.CountryService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import com.hertfordshire.utils.errors.MyApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
public class PublicCountryController extends PublicBaseApiController {

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private CountryService countryService;

    @Autowired
    private MyApiResponse myApiResponse;

    @GetMapping("/auth/country/")
    public ResponseEntity<?> index(HttpServletResponse res, HttpServletRequest request) {

        try {

            List<Country> countryList = countryService.findAll();
            return myApiResponse.successful(countryList, "list.of.countries");

        } catch (Exception e) {
            return myApiResponse.internalServerErrorResponse();
        }
    }
}
