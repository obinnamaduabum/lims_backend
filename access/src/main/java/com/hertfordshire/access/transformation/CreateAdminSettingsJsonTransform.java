package com.hertfordshire.access.transformation;

import com.google.gson.Gson;
import com.hertfordshire.dto.AdminSettingsDto;
import com.hertfordshire.service.psql.settings.admin.AdminSettingsService;
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
public class CreateAdminSettingsJsonTransform {

    private Gson gson;

    private static final Logger logger = Logger.getLogger(CreateAdminSettingsJsonTransform.class.getSimpleName());

    @Autowired
    private AdminSettingsService adminSettingsService;

    public CreateAdminSettingsJsonTransform(){
        this.gson = new Gson();
    }

    public void create() {

        BufferedReader bufferedReader;

        InputStream inputStream = ResourceUtil.getResourceAsStream(TRANSFORMATION_DATA_FOLDER + File.separator + JSON_FOLDER + File.separator + ADMIN_SETTINGS_FILE_NAME);

        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        AdminSettingsDto adminSettingsDto = gson.fromJson(bufferedReader, AdminSettingsDto.class);

        if (adminSettingsService.findAll().size() <= 0) {

            logger.info("Adding settings");

            adminSettingsService.save(adminSettingsDto);

            logger.info("Done adding app settings");

        }else {

            logger.info("AdminSettings already added exist");

        }
    }
}
