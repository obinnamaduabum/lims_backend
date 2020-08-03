package com.hertfordshire.restfulapi.controller;

import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class PasswordGeneratorController extends PublicBaseApiController {

    @Autowired
    private MessageUtil messageUtil;

    @GetMapping("/auth/password-generation/create")
    public ResponseEntity<?> create() {

        ApiError apiError = null;

        try {

           String generatedPassword = Utils.generatePassayPassword();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("generate.password.success", "en"),
                    true, new ArrayList<>(), generatedPassword);

        } catch (Exception e) {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("generate.password.failed", "en"),
                    false, new ArrayList<>(), null);

        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

    }



    private String generateCommonLangPassword() {
        String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
        String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
        String numbers = RandomStringUtils.randomNumeric(2);
        String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
        String totalChars = RandomStringUtils.randomAlphanumeric(2);
        String combinedChars = upperCaseLetters.concat(lowerCaseLetters)
                .concat(numbers)
                .concat(specialChar)
                .concat(totalChars);
        List<Character> pwdChars = combinedChars.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
        Collections.shuffle(pwdChars);
        String password = pwdChars.stream()
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        return password;
    }

}
