package com.hertfordshire.restfulapi.controller.admin_settings;

import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.model.psql.AdminSettings;
import com.hertfordshire.pojo.AdminSettingsPojo;
import com.hertfordshire.service.psql.settings.admin.AdminSettingsService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.controllers.ProtectedBaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ProtectedAdminSettingsController extends ProtectedBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedAdminSettingsController.class);

    @Autowired
    private MessageUtil messageUtil;

    private AdminSettingsService adminSettingsService;

    @Autowired
    public ProtectedAdminSettingsController(AdminSettingsService adminSettingsService) {
        this.adminSettingsService = adminSettingsService;
    }

    @GetMapping("/auth/setting/admin")
    public ResponseEntity<?> getAllAdminSettings() {

        ApiError apiError = null;
        AdminSettings adminSettings = null;
        AdminSettingsPojo adminSettingsPojo = null;

        adminSettings = adminSettingsService.getAdminSettings();

        if(adminSettings != null){

            adminSettingsPojo = new AdminSettingsPojo();
            adminSettingsPojo.setCurrencyType(adminSettings.getCurrencyType().name());
            adminSettingsPojo.setDataStorageProduction(adminSettings.isDataStorageProduction());
            adminSettingsPojo.setDropBoxActive(adminSettings.isDropBoxActive());

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("got.settings.successfully", "en"),
                    true, new ArrayList<>(), adminSettingsPojo);
        } else {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("failed.to.fetch.settings", "en"),
                    false, new ArrayList<>(), null);
        }



        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());

    }

    @PutMapping("/auth/setting/admin/update")
    @PreAuthorize("hasAnyAuthority('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<?> updateAdminSettings(@RequestBody Object adminSettingsDto) {
        ApiError apiError = null;
        AdminSettings adminSettings = null;

        AdminSettings adminSetting = adminSettingsService.getAdminSettings();

        adminSettings = adminSettingsService.update(adminSetting, adminSettingsDto);

        if(adminSettings != null) {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("settings.updated.successfully", "en"),
                    true, new ArrayList<>(), null);
        } else {

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("settings.update.not.successfully", "en"),
                    false, new ArrayList<>(), null);
        }

        return new ResponseEntity<Object>(apiError, new HttpHeaders(), apiError.getStatus());
    }
}
