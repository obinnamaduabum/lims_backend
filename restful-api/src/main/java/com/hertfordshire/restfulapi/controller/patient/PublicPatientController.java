package com.hertfordshire.restfulapi.controller.patient;

import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.access.errors.CustomBadRequestException;
import com.hertfordshire.dto.PatientDto;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.service.psql.patient.PatientService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.PhoneNumberValidationUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import com.hertfordshire.utils.pojo.ProperPhoneNumberPojo;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.qr.ZxingPngQrGenerator;
import io.swagger.annotations.ApiOperation;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@RestController
public class PublicPatientController extends PublicBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(PublicPatientController.class.getSimpleName());

    @Autowired
    private PortalUserService portalUserService;

    @Value("${default.domainUrlOne}")
    private String domainUrlOne;

    @Value("${default.domainUrlTwo}")
    private String domainUrlTwo;

    @Value("${no.reply.email}")
    private String noReplyEmail;

    @Value("${emailValidForInMinutes}")
    private int emailValidForInMinutes;

    @Autowired
    private PatientService patientService;

    @Autowired
    private MessageUtil messageUtil;

    @ApiOperation("Create patient")
    @PostMapping("/default/patient/create")
    public ResponseEntity<Object> create(@Valid @RequestBody PatientDto patientDto,
                                         BindingResult bindingResult) {

        ApiError apiError = null;
        PortalUser portalUser = null;

        if (bindingResult.hasErrors()) {

            bindingResult.getAllErrors().forEach(objectError -> {
                logger.info(objectError.toString());
            });

            throw new CustomBadRequestException();

        } else {

            if(!TextUtils.isBlank(patientDto.getEmail())) {
                portalUser = portalUserService.findByEmail(patientDto.getEmail().trim());

                if (portalUser != null) {
                    apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.email.exists", "en"),
                            false, new ArrayList<>(), portalUser);
                    return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
                }
            }

            portalUser = portalUserService.findByPhoneNumber(patientDto.getPhoneNumber().trim());

            if (portalUser != null) {
                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("portal.user.phone.number.already.exists", "en"),
                        false, new ArrayList<>(), portalUser);

                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }


            try {

                ProperPhoneNumberPojo phoneNumber = PhoneNumberValidationUtil.validatePhoneNumber(patientDto.getPhoneNumber(), patientDto.getPhoneNumberObj());

                if (phoneNumber != null) {
                    patientDto.setPhoneNumber(phoneNumber.getFormattedNumber());
                }

                if (!TextUtils.isBlank(patientDto.getOtherPhoneNumber())) {
                    ProperPhoneNumberPojo otherPhoneNumber = PhoneNumberValidationUtil.validatePhoneNumber(patientDto.getOtherPhoneNumber(), patientDto.getOtherPhoneNumberObj());

                    if (otherPhoneNumber != null) {
                        patientDto.setOtherPhoneNumber(otherPhoneNumber.getFormattedNumber());
                    }
                }

                ProperPhoneNumberPojo nextOFKinPhoneNumber = PhoneNumberValidationUtil.validatePhoneNumber(patientDto.getNextOFKinPhoneNumber(), patientDto.getNextOFKinPhoneNumberObj());

                if (nextOFKinPhoneNumber != null) {
                    patientDto.setNextOFKinPhoneNumber(nextOFKinPhoneNumber.getFormattedNumber());
                }

            } catch (Exception e) {

                e.printStackTrace();

                apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("phone.number.validation.failed", "en"),
                        false, new ArrayList<>(), null);

                return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
            }


            try {

                portalUser = patientService.create(patientDto);



                String dataUri = null;
                if(portalUser.isTwoFactor()) {
                    QrData data = new QrData.Builder()
                            .label("info@hertfordshire.dev")
                            .secret(portalUser.getSecret())
                            .issuer("Hertfordshire Labs")
                            .algorithm(HashingAlgorithm.SHA1) // More on this below
                            .digits(6)
                            .period(30)
                            .build();


                    QrGenerator generator = new ZxingPngQrGenerator();
                    byte[] imageData = generator.generate(data);
                    String mimeType = generator.getImageMimeType();
                    dataUri = getDataUriForImage(imageData, mimeType);
                }



                apiError = new ApiError(HttpStatus.CREATED.value(), HttpStatus.CREATED, messageUtil.getMessage("patient.creation.successful", "en"),
                        true, new ArrayList<>(), dataUri);

            } catch (Exception e) {
                e.printStackTrace();

                apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR, messageUtil.getMessage("patient.creation.not.successful", "en"),
                        false, new ArrayList<>(), portalUser);
            }
            return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }
}
