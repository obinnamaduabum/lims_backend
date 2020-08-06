package com.hertfordshire.restfulapi.controller.portal_account;

import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.model.psql.PortalAccount;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class PortalAccountController extends PublicBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(PortalAccountController.class.getSimpleName());

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private PortalAccountService portalAccountService;

    @Value("${default.domainUrlOne}")
    private String domainUrlOne;

    @Value("${no.reply.email}")
    private String noReplyEmail;

    @GetMapping("/auth/portal-account/name-exists")
    public ResponseEntity<Object> findByName(@RequestParam("name") String portalAccountName) {

        ApiError apiError = null;
        PortalAccount portalAccount = null;

        if (TextUtils.isBlank(portalAccountName)) {

            logger.info(portalAccountName);

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("institution.name.can_not.be.empty", "en"),
                    false, new ArrayList<>(), null);
        } else {

            try {
                portalAccount = this.portalAccountService.findPortalAccountByName(portalAccountName);
                if (portalAccount != null) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("institution.name.found", "en"),
                            true, new ArrayList<>(), null);
                } else {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("institution.name.notfound", "en"),
                            false, new ArrayList<>(), null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("institution.name.notfound", "en"),
                        false, new ArrayList<>(), null);
            }
        }

        return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
