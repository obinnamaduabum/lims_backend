package com.hertfordshire.restfulapi.controller.lab_test_controller;

import com.google.gson.Gson;
import com.hertfordshire.access.errors.ApiError;
import com.hertfordshire.dao.psql.LabTestCategoriesDao;
import com.hertfordshire.dto.LabTestSearchDto;
import com.hertfordshire.model.psql.AdminSettings;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestCategory;
import com.hertfordshire.pojo.LabTestPojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.pubsub.redis.service.lab_test.RedisLabTestService;
import com.hertfordshire.restfulapi.controller.lab_test_category_controller.ProtectedLabTestCategoryController;
import com.hertfordshire.service.psql.lab_test.LabTestService;
import com.hertfordshire.service.psql.lab_test_categories.LabTestCategoriesService;
import com.hertfordshire.service.psql.settings.admin.AdminSettingsService;
import com.hertfordshire.utils.MessageUtil;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.controllers.PublicBaseApiController;
import com.hertfordshire.utils.lhenum.CurrencyTypeConstant;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class PublicLabTestController extends PublicBaseApiController {

    private static final Logger logger = LoggerFactory.getLogger(ProtectedLabTestCategoryController.class.getSimpleName());

    @Autowired
    private MessageUtil messageUtil;

    @Autowired
    private LabTestService labTestService;

    @Autowired
    private LabTestCategoriesService labTestCategoriesService;

    @Autowired
    private AdminSettingsService adminSettingsService;

    @Autowired
    private LabTestCategoriesDao labTestCategoriesDao;

    private Gson gson;

    @Autowired
    private RedisLabTestService redisLabTestService;

    public PublicLabTestController() {
        this.gson = new Gson();
    }

    @PostMapping("/default/lab-test/lab-category-and-tests")
    public ResponseEntity<Object> findAllCategoriesAndTests(HttpServletRequest request,
                                                            @RequestBody LabTestSearchDto labTestSearchDto,
                                                            @RequestParam("page") int page,
                                                            @RequestParam("size") int size) {


        String lang = "en";

        try {
            String tmpLang = Utils.fetchLanguageFromCookie(request);
            if(tmpLang != null) {
               lang = tmpLang;
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        ApiError apiError = null;

        String categoryName = null;

        String searchValue = null;

        List<LabTest> labTestList = new ArrayList<>();

        try {

            logger.info("page: " + page + " size: " + size);
            List<LabTestPojo> labTestPojos = new ArrayList<>();

            Pageable sortedByDateCreated = PageRequest.of(page, size, Sort.by("date_created").descending());

            if (!TextUtils.isBlank(labTestSearchDto.getCategoryName())) {
                categoryName = labTestSearchDto.getCategoryName().toLowerCase();
            }

            if (!TextUtils.isBlank(labTestSearchDto.getSearchValue())) {
                searchValue = labTestSearchDto.getSearchValue().toLowerCase();
            }

            labTestList = this.labTestService.findBySearchTerms(categoryName, searchValue, sortedByDateCreated);
            long total = this.labTestService.findBySearchTermsTotal(categoryName, searchValue);


            PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();
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

                    labTestPojo.setCategoryId(optionalLabTestCategory.get().getId());
                    labTestPojo.setCategoryName(optionalLabTestCategory.get().getName());
                    labTestPojos.add(labTestPojo);
                }
            }

            paginationResponsePojo.setDataList(labTestPojos);
            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("lab.tests.found", lang), true, new ArrayList<>(), paginationResponsePojo);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());

        } catch (Exception e) {
            e.printStackTrace();

            apiError = new ApiError(HttpStatus.OK.value(), HttpStatus.OK, messageUtil.getMessage("an.error.occurred", lang), false, new ArrayList<>(), null);
            return new ResponseEntity<>(apiError, new HttpHeaders(), apiError.getStatus());
        }
    }
}
