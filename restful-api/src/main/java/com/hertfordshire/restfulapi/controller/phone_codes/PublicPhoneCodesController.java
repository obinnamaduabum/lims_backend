package com.hertfordshire.restfulapi.controller.phone_codes;

import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.model.psql.CountryPhoneCodes;
import com.hertfordshire.service.psql.phoneCodes.PhoneCodesService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
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
public class PublicPhoneCodesController extends PublicBaseApiController {

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private PhoneCodesService phoneCodesService;

    @GetMapping("/auth/phone_codes")
    public ResponseEntity<?> index(HttpServletResponse res, HttpServletRequest request) {

        List<CountryPhoneCodes> countryPhoneCodes = phoneCodesService.findAll();
        ApiError apiError = null;

        apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("list.phone.codes", "en"),
                true, new ArrayList<>(), countryPhoneCodes);

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
