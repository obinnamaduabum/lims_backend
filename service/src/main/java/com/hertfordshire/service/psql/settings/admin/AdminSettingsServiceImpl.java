package com.hertfordshire.service.psql.settings.admin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hertfordshire.dto.AdminSettingsDto;
import com.hertfordshire.model.psql.AdminSettings;
import com.hertfordshire.dao.psql.AdminSettingsDao;
import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminSettingsServiceImpl implements AdminSettingsService {

    private AdminSettingsDao adminSettingsDao;

    @Autowired
    public AdminSettingsServiceImpl(AdminSettingsDao adminSettingsDao) {
        this.adminSettingsDao = adminSettingsDao;
    }


    @Override
    public AdminSettings save(AdminSettingsDto adminSettingsDto) {

        AdminSettings adminSettings = new AdminSettings();
        if (adminSettingsDto.isDropBoxActive()) {
            adminSettings.setDropBoxActive(adminSettingsDto.isDropBoxActive());
        }

        if (adminSettingsDto.isDataStorageProduction()) {
            adminSettings.setDataStorageProduction(adminSettingsDto.isDataStorageProduction());
        }

        if (StringUtils.isNotBlank(adminSettingsDto.getCurrencyType())) {
            adminSettings.setCurrencyType(CurrencyTypeConstant.valueOf(adminSettingsDto.getCurrencyType()));
        }

        return adminSettingsDao.save(adminSettings);
    }

    @Override
    public AdminSettings update(AdminSettings adminSettings, Object adminSettingsDto) {

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(gson.toJson(adminSettingsDto)).getAsJsonObject();

        if(doesFieldExist(jsonObject, "dropBoxActive")) {
            adminSettings.setDropBoxActive(getBooleanField(jsonObject, "dropBoxActive"));
        }

        if(doesFieldExist(jsonObject, "dataStorageProduction")) {
            adminSettings.setDataStorageProduction(getBooleanField(jsonObject, "dataStorageProduction"));
        }

        if(doesFieldExist(jsonObject, "currencyType")){
            adminSettings.setCurrencyType(CurrencyTypeConstant.valueOf(getStringField(jsonObject, "currencyType")));
        }

        return adminSettingsDao.save(adminSettings);
    }

    @Override
    public List<AdminSettings> findAll() {
        return adminSettingsDao.findAll();
    }

    @Override
    public AdminSettings getAdminSettings() {
        List<AdminSettings> adminSettingsList = this.findAll();
        return adminSettingsList.stream().findFirst().orElse(null);
    }



    private boolean doesFieldExist(JsonObject json, String field) {
        return json.has(field);
    }

    private boolean getBooleanField(JsonObject json, String field) {
        return json.get(field).getAsBoolean();
    }

    private String getStringField(JsonObject json, String field) {
        return json.get(field).getAsString();
    }

    private Long getLongField(JsonObject json, String field) {
        return json.get(field).getAsLong();
    }
}
