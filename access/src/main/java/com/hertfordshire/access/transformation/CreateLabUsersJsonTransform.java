package com.hertfordshire.access.transformation;

import com.google.gson.Gson;
import com.hertfordshire.dto.AdminSettingsDto;
import com.hertfordshire.dto.CustomPortalAccountAndPortalUserDto;
import com.hertfordshire.dto.EmployeeDto;
import com.hertfordshire.dto.PortalAccountDto;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.service.psql.employee.EmployeeService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.settings.admin.AdminSettingsService;
import com.hertfordshire.utils.ResourceUtil;
import com.hertfordshire.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static com.hertfordshire.access.transformation.Transformer.*;

@Component
public class CreateLabUsersJsonTransform {

    private static final Logger logger = Logger.getLogger(CreatePortalUserJsonTransformer.class.getSimpleName());

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private EmployeeService employeeService;

    private Gson gson;

    @Autowired
    public CreateLabUsersJsonTransform() {
        this.gson = new Gson();
    }

    public void create() {

        List<BufferedReader> bufferedReaderList = new ArrayList<>();

        BufferedReader medicalLabScientistBufferedReader
        = new Utils().myBufferReader(TRANSFORMATION_DATA_FOLDER, JSON_FOLDER, MEDICAL_LAB_SCIENTIST);

        bufferedReaderList.add(medicalLabScientistBufferedReader);

        BufferedReader pathologistScientistBufferedReader
        = new Utils().myBufferReader(TRANSFORMATION_DATA_FOLDER, JSON_FOLDER, PATHOLOGIST);

        bufferedReaderList.add(pathologistScientistBufferedReader);

        BufferedReader receptionistBufferedReader
        = new Utils().myBufferReader(TRANSFORMATION_DATA_FOLDER, JSON_FOLDER, RECEPTIONIST);

        bufferedReaderList.add(receptionistBufferedReader);

       for(BufferedReader bufferedReader : bufferedReaderList) {
           save(bufferedReader);
       }

    }


    private void save(BufferedReader bufferedReader) {
        CustomPortalAccountAndPortalUserDto customPortalAccountAndPortalUserDto =
                gson.fromJson(bufferedReader, CustomPortalAccountAndPortalUserDto.class);

        PortalUser foundUser = portalUserService.findByEmail(customPortalAccountAndPortalUserDto.getPortalUser().getEmail());
        if (foundUser != null) {
            logger.info("account already exists");
        } else {

            PortalAccountDto portalAccountDto = customPortalAccountAndPortalUserDto.getPortalAccount();

            EmployeeDto portalUserDto = customPortalAccountAndPortalUserDto.getPortalUser();

            employeeService.createEmployee(portalUserDto, portalAccountDto, null, null, null);

            logger.info("Added new account");
        }
    }
}
