package com.hertfordshire.restfulapi.controller.admin_settings;

import com.hertfordshire.utils.errors.ApiError;
import com.hertfordshire.dao.psql.AdminSettingsDao;
import com.hertfordshire.model.psql.AdminSettings;
import com.hertfordshire.pojo.AdminSettingsPojo;
import com.hertfordshire.service.psql.settings.admin.AdminSettingsService;
import com.hertfordshire.service.psql.settings.admin.AdminSettingsServiceImpl;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class PublicAdminSettingsController extends PublicBaseApiController {

    @Autowired
    private MessageUtil messageUtil;

    private AdminSettingsService adminSettingsService;

    @Autowired
    public PublicAdminSettingsController(AdminSettingsDao adminSettingsDao) {
        this.adminSettingsService = new AdminSettingsServiceImpl(adminSettingsDao);
    }

    @GetMapping("/auth/setting/admin")
    public ResponseEntity<?> getAllAdminSettings() {

        ApiError apiError = null;
        AdminSettings adminSettings = null;
        AdminSettingsPojo adminSettingsPojo = null;

        adminSettings = adminSettingsService.getAdminSettings();

        if(adminSettings != null) {
            adminSettingsPojo = new AdminSettingsPojo();
            adminSettingsPojo.setCurrencyType(adminSettings.getCurrencyType().name());
            adminSettingsPojo.setDataStorageProduction(adminSettings.isDataStorageProduction());
            adminSettingsPojo.setDropBoxActive(adminSettings.isDropBoxActive());

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("got.settings.successfully", "en"),
                    true, new ArrayList<>(), adminSettingsPojo);
        } else {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("settings.update.not.successfully", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

    }
}
