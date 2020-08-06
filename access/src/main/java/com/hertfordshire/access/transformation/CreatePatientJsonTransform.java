package com.hertfordshire.access.transformation;

import com.google.gson.Gson;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dto.AdminSettingsDto;
import com.hertfordshire.dto.PatientDto;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.service.psql.patient.PatientService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.settings.admin.AdminSettingsService;
import com.hertfordshire.utils.ResourceUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.GenericStatusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static com.hertfordshire.access.transformation.Transformer.*;

@Component
public class CreatePatientJsonTransform {

    private Gson gson;

    private static final Logger logger = Logger.getLogger(CreatePatientJsonTransform.class.getSimpleName());

    @Autowired
    private PatientService patientService;

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private PortalUserDao portalUserDao;

    @Autowired
    public CreatePatientJsonTransform(){
        this.gson = new Gson();
    }

    public void create() {

        BufferedReader bufferedReader  =
                new Utils().myBufferReader(TRANSFORMATION_DATA_FOLDER, JSON_FOLDER, PATIENT_FILE_NAME);

        PatientDto patientDto = gson.fromJson(bufferedReader, PatientDto.class);

        PortalUser foundPortalUser = portalUserService.findByEmail(patientDto.getEmail());

        if (foundPortalUser != null) {

            logger.info("Patient already exists");

        } else {

            this.patientService.create(patientDto, true);

            logger.info("Patient added");
        }
    }
}
