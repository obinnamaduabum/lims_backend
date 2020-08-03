package com.hertfordshire.service.resolvers;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.google.gson.Gson;
import com.hertfordshire.dto.LabTestSearchDto;
import com.hertfordshire.model.psql.AdminSettings;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestCategory;
import com.hertfordshire.pojo.LabTestCategoryPojo;
import com.hertfordshire.pojo.LabTestPojo;
import com.hertfordshire.pojo.graphql.PaginationResponseLabTestAndCategories;
import com.hertfordshire.dao.psql.AdminSettingsDao;
import com.hertfordshire.dao.psql.LabTestCategoriesDao;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.service.psql.lab_test_categories.LabTestCategoriesService;
import com.hertfordshire.service.psql.settings.admin.AdminSettingsService;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Component
public class LabTestCategoriesQuery implements GraphQLQueryResolver {

    @Autowired
    private AdminSettingsService adminSettingsService;

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private LabTestCategoriesDao labTestCategoriesDao;

    @Autowired
    private LabTestCategoriesService labTestCategoriesService;

    @Autowired
    private AdminSettingsDao adminSettingsDao;

    public List<LabTestCategoryPojo> getLabTestCategories() {

        List<LabTestCategory> labTestCategories = this.labTestCategoriesService.findAll();
        List<LabTestCategoryPojo> labTestCategoryPojos = new ArrayList<>();

        for (LabTestCategory labTestCategory : labTestCategories) {
            LabTestCategoryPojo labTestCategoryPojo = new LabTestCategoryPojo();
            labTestCategoryPojo.setName(labTestCategory.getName());
            labTestCategoryPojos.add(labTestCategoryPojo);
        }

        return labTestCategoryPojos;
    }

    public PaginationResponseLabTestAndCategories getPagination(Number first) {

        String categoryName = null;

        String searchValue = null;

        List<LabTest> labTestList = new ArrayList<>();

        int page = 0;
        int size = first.intValue();
        //logger.info("page: " + page + " size: " + size);
        List<LabTestPojo> labTestPojos = new ArrayList<>();


        Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());

        LabTestSearchDto labTestSearchDto = new LabTestSearchDto();


        if (!TextUtils.isBlank(labTestSearchDto.getCategoryName())) {
            categoryName = labTestSearchDto.getCategoryName().toLowerCase();
        }


        if (!TextUtils.isBlank(labTestSearchDto.getSearchValue())) {
            searchValue = labTestSearchDto.getSearchValue().toLowerCase();
        }

        labTestList = this.labTestService.findBySearchTerms(categoryName, searchValue, sortedByDateCreated);
        long total = this.labTestService.findBySearchTermsTotal(categoryName, searchValue);


        PaginationResponseLabTestAndCategories paginationResponsePojo = new PaginationResponseLabTestAndCategories();
        paginationResponsePojo.setLength(total);
        paginationResponsePojo.setPageNumber((long) page);
        paginationResponsePojo.setPageSize((long) size);


        for (LabTest labTest : labTestList) {
            LabTestPojo labTestPojo = new LabTestPojo();
            labTestPojo.setId(labTest.getId());
            labTestPojo.setName(labTest.getName());
            labTestPojo.setCode(labTest.getCode());


            List<AdminSettings> adminSettingsList =this.adminSettingsService.findAll();
            AdminSettings adminSettings = adminSettingsList.stream().findFirst().orElse(null);


            if(adminSettings != null) {
                if (adminSettings.getCurrencyType().equals(CurrencyTypeConstant.NGN)) {
                    labTestPojo.setPrice(Utils.koboToNaira(labTest.getPriceInNaira()));
                } else if(adminSettings.getCurrencyType().equals(CurrencyTypeConstant.USD)) {
                    labTestPojo.setPrice(String.valueOf(labTest.getPriceInUSD()));
                } else if(adminSettings.getCurrencyType().equals(CurrencyTypeConstant.EUR)) {
                    labTestPojo.setPrice(String.valueOf(labTest.getPriceInEuro()));
                }
            }

            Optional<LabTestCategory> optionalLabTestCategory = this.labTestCategoriesDao.findById(labTest.getLabTestCategory().getId());

            if (optionalLabTestCategory.isPresent()) {

                labTestPojo.setCategoryName(optionalLabTestCategory.get().getName());
                labTestPojos.add(labTestPojo);
            }
        }

        paginationResponsePojo.setDataList(labTestPojos);

        return paginationResponsePojo;
    }

    public AdminSettings getAdminSettings() {
       List<AdminSettings> adminSettingsList = this.adminSettingsDao.findAll();
       return adminSettingsList.stream().findFirst().orElse(null);
    }

}
