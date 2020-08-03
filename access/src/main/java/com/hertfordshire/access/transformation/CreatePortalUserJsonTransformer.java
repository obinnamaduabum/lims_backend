package com.hertfordshire.access.transformation;

import com.google.gson.Gson;
import com.hertfordshire.dto.CustomPortalAccountAndPortalUserDto;
import com.hertfordshire.dto.EmployeeDto;
import com.hertfordshire.dto.PortalAccountDto;
import com.hertfordshire.service.psql.employee.EmployeeService;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.utils.ResourceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static com.hertfordshire.access.transformation.Transformer.*;



@Component
public class CreatePortalUserJsonTransformer {

    private static final Logger logger = Logger.getLogger(CreatePortalUserJsonTransformer.class.getSimpleName());

    @Autowired
    private PortalUserService portalUserService;

    @Autowired
    private EmployeeService employeeService;

    private Gson gson;

    @Autowired
    public CreatePortalUserJsonTransformer() {
        this.gson = new Gson();
    }

    public void createPortalUser() {

        BufferedReader bufferedReader = null;

        InputStream inputStream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + JSON_FOLDER + File.separator + ADMIN_PORTAL_USERS_FILE_NAME);

        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        CustomPortalAccountAndPortalUserDto customPortalAccountAndPortalUserDto =
                gson.fromJson(bufferedReader, CustomPortalAccountAndPortalUserDto.class);

        if (portalUserService.findAll().size() <= 0) {

            PortalAccountDto portalAccountDto = customPortalAccountAndPortalUserDto.getPortalAccount();

            EmployeeDto portalUserDto = customPortalAccountAndPortalUserDto.getPortalUser();

            employeeService.createEmployee(portalUserDto, portalAccountDto, null, null, null);

            logger.info("Added admin account");

        } else {

            logger.info("Admin account already exists");
        }
    }
}
