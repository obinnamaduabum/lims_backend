package com.hertfordshire.service.psql.settings.admin;


import com.hertfordshire.dto.AdminSettingsDto;
import com.hertfordshire.model.psql.AdminSettings;

import java.util.List;

public interface AdminSettingsService {

    AdminSettings save(AdminSettingsDto adminSettingsDto);

    AdminSettings update(AdminSettings adminSettings, Object adminSettingsDto);

    List<AdminSettings> findAll();

    AdminSettings getAdminSettings();
}
