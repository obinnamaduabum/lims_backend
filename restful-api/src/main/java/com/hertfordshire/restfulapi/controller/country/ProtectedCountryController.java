package com.hertfordshire.restfulapi.controller.country;


import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.model.psql.Country;
import com.hertfordshire.service.psql.country.CountryService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
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
public class ProtectedCountryController extends ProtectedBaseApiController {

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private CountryService countryService;

    @GetMapping("/country/")
    public ResponseEntity<?> index(HttpServletResponse res, HttpServletRequest request) {

        List<Country> countryList = countryService.findAll();
        ApiError apiError = null;

        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("list.of.countries", "en"),
                true, new ArrayList<>(), countryList);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
