package com.hertfordshire.access.transformation;


import com.google.gson.Gson;
import com.hertfordshire.dto.PrivilegeDto;
import com.hertfordshire.model.psql.Privilege;
import com.hertfordshire.service.psql.privileges.PrivilegeService;
import com.hertfordshire.utils.ResourceUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import static com.hertfordshire.access.transformation.Transformer.*;


@Component
public class CreatePrivilegesJsonTransform {

    private static final Logger logger = Logger.getLogger(CreatePrivilegesJsonTransform.class.getSimpleName());

    @Autowired
    private PrivilegeService privilegeService;

    private Gson gson;

    public CreatePrivilegesJsonTransform() {
        this.gson = new Gson();
    }

    public void createPrivileges() {

        BufferedReader bufferedReader = null;

        InputStream inputStream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + JSON_FOLDER + File.separator + PRIVILEGES);

        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        PrivilegeDto[] privilegeDtos = gson.fromJson(bufferedReader, PrivilegeDto[].class);

        logger.info("Adding privileges");

        for (int i = 0; i < privilegeDtos.length; i++) {

            Privilege privilege = null;

            if(StringUtils.isNotBlank(privilegeDtos[i].getName())) {

                privilege = privilegeService.findName(privilegeDtos[i].getName());

                if (privilege == null) {
                    PrivilegeDto privilegeDto = new PrivilegeDto();
                    privilegeDto.setName(privilegeDtos[i].getName());
                    privilegeService.save(privilegeDto);

                } else {
                    logger.info(" privilege,  "+ privilege.getName() + " already exist");
                }
            }
        }

        logger.info("Done adding privileges");

    }
}
