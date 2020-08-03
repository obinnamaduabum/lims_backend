package com.hertfordshire.service.psql.lab_test;

import com.google.gson.Gson;
import com.hertfordshire.dto.LabTestCategoryDto;
import com.hertfordshire.dto.LabTestDto;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestCategory;
import com.hertfordshire.dao.psql.LabTestCategoriesDao;
import com.hertfordshire.dao.psql.LabTestDao;
import com.hertfordshire.service.psql.lab_test_categories.LabTestCategoriesService;
import com.hertfordshire.service.sequence.lab_test.LabTestSequenceImpl;
import com.hertfordshire.utils.Utils;
import org.apache.http.util.TextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LabTestServiceImpl implements LabTestService {

    private static final Logger logger = LoggerFactory.getLogger(LabTestServiceImpl.class.getSimpleName());

    @Autowired
    private LabTestDao labTestDao;

    private Gson gson;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private LabTestCategoriesService labTestCategoriesService;

    @Autowired
    private LabTestSequenceImpl labTestSequence;

    @Autowired
    private LabTestCategoriesDao labTestCategoriesDao;

    public LabTestServiceImpl() {
        this.gson = new Gson();
    }

    @Override
    public LabTest save(LabTestDto labTestDto) {


        String labTestId = String.format("LAB_T_%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                labTestSequence.getNextId()
        );

        LabTest labTest = new LabTest();
        labTest.setName(labTestDto.getName());

        LabTestCategory labTestCategory = labTestCategoriesDao.findByName(labTestDto.getCategoryName().toLowerCase());

        if(labTestCategory != null) {
            labTest.setLabTestCategory(labTestCategory);
        } else {

            LabTestCategoryDto labTestCategoryDto = new LabTestCategoryDto();
            labTestCategoryDto.setName(labTestDto.getCategoryName().toLowerCase());
            labTestCategory = this.labTestCategoriesService.save(labTestCategoryDto);
            labTest.setLabTestCategory(labTestCategory);
        }

        try {
            labTest.setPriceInNaira(Utils.nairaToKobo(labTestDto.getPriceInNaira()));
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            labTest.setPriceInNaira(Utils.nairaToKobo(labTestDto.getPriceInNaira()));
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            labTest.setPriceInUSD(Long.valueOf(labTestDto.getPriceInUSD()));
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            labTest.setPriceInEuro(Long.valueOf(labTestDto.getPriceInEuro()));
        }catch (Exception e){
            e.printStackTrace();
        }

        labTest.setCode(labTestId);

        return labTestDao.save(labTest);
    }

    @Override
    public Optional<LabTest> findById(Long id) {
        return this.labTestDao.findById(id);
    }

    @Override
    public LabTest findByResultTemplateId(String id) {
        return this.labTestDao.findByResultTemplateId(id);
    }

    @Override
    public List<LabTest> findAll() {
        return this.labTestDao.findAll();
    }

    @Override
    public LabTest findByName(String name) {
        return this.labTestDao.findByName(name.toLowerCase());
    }

    @Override
    public List<LabTest> findByCategoryName(LabTestCategory labTestCategory) {
        return this.labTestDao.findByLabTestCategory(labTestCategory);
    }


    @Override
    public List<LabTest> findBySearchTerms(String categoryName, String testName, Pageable pageable) {

        LabTestCategory labTestCategory = null;
        try {
            if(!TextUtils.isBlank(categoryName)) {
                labTestCategory = this.labTestCategoriesDao.findByName(categoryName.toLowerCase());
            }
        } catch (Exception e) {
            logger.info("category not found");
           // e.printStackTrace();
        }

        Query query = this.entityManager.createQuery("select i" +
                " from LabTest i" +
                " where (:testName is null or lower(i.name) like :testName)" +
                " and (:categoryId is null or i.labTestCategory.id = :categoryId)" +
                " ORDER BY i.dateCreated DESC");

        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        query.setFirstResult((pageNumber) * pageSize);

        if (!TextUtils.isBlank(testName)) {
            query.setParameter("testName", "%" + testName + "%");
        } else {
            query.setParameter("testName", testName);
        }

        if (labTestCategory !=  null) {
           // logger.info("xxx: "+labTestCategory.getId());
            query.setParameter("categoryId", labTestCategory.getId());
        } else {
            query.setParameter("categoryId", null);
        }

        query.setMaxResults(pageSize);
        List<LabTest> labTestList = (List<LabTest>) query.getResultList();
        this.entityManager.close();
        return labTestList;
    }


    @Override
    public Long findBySearchTermsTotal(String categoryName, String testName) {

        LabTestCategory labTestCategory = null;
        try {
            if(!TextUtils.isBlank(categoryName)) {
                labTestCategory = this.labTestCategoriesDao.findByName(categoryName.toLowerCase());
            }
        } catch (Exception e) {
            logger.info("category not found");
            // e.printStackTrace();
        }


        Query query = this.entityManager.createQuery("select i" +
                " from LabTest i" +
                " where (:testName is null or lower(i.name) like :testName)" +
                " and (:categoryId is null or i.labTestCategory.id = :categoryId)" +
                " ORDER BY i.dateCreated DESC");

        if (!TextUtils.isBlank(testName)) {
            query.setParameter("testName", "%" + testName + "%");
        } else {
            query.setParameter("testName", testName);
        }

        if (labTestCategory !=  null) {
            // logger.info("xxx: "+labTestCategory.getId());
            query.setParameter("categoryId", labTestCategory.getId());
        } else {
            query.setParameter("categoryId", null);
        }

        // logger.info(this.gson.toJson(query));
        List<LabTest> labTestList = (List<LabTest>) query.getResultList();
        this.entityManager.close();
        return (long) labTestList.size();
    }



    @Override
    public List<LabTest> findBySearchTermsByTestNameWithPagination(String testName, Pageable pageable) {
        return this.labTestDao.findByName(testName, pageable);
    }
}
