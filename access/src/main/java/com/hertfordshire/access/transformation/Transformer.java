package com.hertfordshire.access.transformation;

import org.apache.commons.lang3.StringUtils;

public abstract class Transformer {

    static final String TRANSFORMATION_DATA_FOLDER = "data-transforms";
    static final String EXCEL_FOLDER = "excel";
    static final String JSON_FOLDER = "json";
    /////////json
    static final String USER_ROLES_FILE_NAME = "user_roles_and_privileges.json";
    static final String ADMIN_PORTAL_USERS_FILE_NAME = "admin_portal_user.json";
    static final String ADMIN_SETTINGS_FILE_NAME = "admin_settings.json";
    static final String KAFKA_TOPICS = "kafka-topics.json";
    static final String PAYMENT_METHOD_CONFIG = "payment_method_config.json";
    static final String PRIVILEGES = "privileges.json";
    static final String SETTINGS_FILE_NAME = "settings.json";
    static final String PORTAL_ACCOUNT_TYPES = "portal_acccount_types.json";
    //////excels
    static final String PHONE_NUMBER_COUNTRIES_FILE_NAME = "litehauzz_countries_phone_code.xlsx";
    static final String COUNTRIES_FILE_NAME = "countries.xlsx";
    static final String LAB_TEST_CATEGORIES = "lab-test-categories.xlsx";
    static final String LAB_TEST = "lab-tests.xlsx";

    public static String getStringValue(org.apache.poi.ss.usermodel.Cell cell) {
        if (cell == null) {
            return "";
        }
        cell.setCellType(org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING);
        String value = StringUtils.trim(cell.getStringCellValue());
        return value == null ? "" : value;
    }
}
