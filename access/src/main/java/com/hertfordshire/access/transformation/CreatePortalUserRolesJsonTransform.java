package com.hertfordshire.access.transformation;

import com.google.gson.Gson;
import com.hertfordshire.dto.RolesDto;
import com.hertfordshire.model.psql.Role;
import com.hertfordshire.service.psql.role.RolesService;
import com.hertfordshire.utils.ResourceUtil;
import com.hertfordshire.utils.lhenum.RoleTypeConstant;
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
public class CreatePortalUserRolesJsonTransform {

    private static final Logger logger = Logger.getLogger(CreatePortalUserRolesJsonTransform.class.getSimpleName());

    @Autowired
    private RolesService rolesService;

    private Gson gson;

    @Autowired
    public CreatePortalUserRolesJsonTransform(){
        this.gson = new Gson();
    }

    public void createUserRoles() {

        BufferedReader bufferedReader = null;

        InputStream inputStream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + JSON_FOLDER + File.separator + USER_ROLES_FILE_NAME);

        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        RolesDto[] rolesDtos = gson.fromJson(bufferedReader, RolesDto[].class);

        logger.info("Adding role types");

        for (int i = 0; i < rolesDtos.length; i++) {

            Role role = null;

            if(StringUtils.isNotBlank(rolesDtos[i].getRoleType())) {

                role = rolesService.findByRoleType(RoleTypeConstant.valueOf(rolesDtos[i].getRoleType()));
                if (role == null) {
                    logger.info("role types :" + rolesDtos[i].getRoleType());
                    RolesDto rolesDto = new RolesDto();
                    rolesDto.setName(rolesDtos[i].getName());
                    rolesDto.setRoleType(rolesDtos[i].getRoleType());
                    rolesDto.setPrivileges(rolesDtos[i].getPrivileges());
                    rolesService.save(rolesDto);
                } else {
                    logger.info("Role types " + RoleTypeConstant.valueOf(rolesDtos[i].getRoleType()) +" already exist");
                }
            }
        }

        logger.info("Done adding role types");

    }
}
