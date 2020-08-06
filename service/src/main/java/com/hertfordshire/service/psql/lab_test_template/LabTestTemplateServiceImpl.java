package com.hertfordshire.service.psql.lab_test_template;

import com.google.gson.Gson;
import com.hertfordshire.dao.psql.LabTestDao;
import com.hertfordshire.dao.psql.LabTestTemplateDao;
import com.hertfordshire.dto.LabTestTemplateCreateDto;
import com.hertfordshire.dto.LabTestTemplateEditDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.pojo.*;
import com.hertfordshire.service.sequence.lab_test_template_code.LabTestTemplateSequenceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LabTestTemplateServiceImpl implements LabTestTemplateService {


    @Autowired
    private LabTestTemplateDao labTestTemplateDao;

    @Autowired
    private LabTestDao labTestDao;

    private Gson gson;

    @Autowired
    private LabTestTemplateSequenceImpl labTestTemplateSequence;

    public LabTestTemplateServiceImpl() {
        this.gson = new Gson();
    }


    @Override
    public LabTestTemplate save(LabTestTemplateCreateDto labTestTemplateCreateDto, PortalUser portalUser) {

        String labTestTemplateId = String.format("LAB_TEST_TEMPLATE_%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                labTestTemplateSequence.getNextId()
        );

        LabTestTemplate labTestTemplate = new LabTestTemplate();
        labTestTemplate.setCode(labTestTemplateId);
        labTestTemplate.setContent(labTestTemplateCreateDto.getData());
        labTestTemplate.setName(labTestTemplateCreateDto.getTitle());
        labTestTemplate.setCreatedBy(portalUser);

        return this.labTestTemplateDao.save(labTestTemplate);
    }

    @Override
    public LabTestTemplate edit(LabTestTemplate labTestTemplate, LabTestTemplateEditDto labTestTemplateEditDto) {

        labTestTemplate.setName(labTestTemplateEditDto.getTitle());
        labTestTemplate.setContent(labTestTemplateEditDto.getData());
        return this.labTestTemplateDao.save(labTestTemplate);
    }

    @Override
    public Optional<LabTestTemplate> findById(Long id) {
        return this.labTestTemplateDao.findById(id);
    }

    @Override
    public LabTestTemplate findByName(String name) {
        return this.labTestTemplateDao.findByName(name.toLowerCase());
    }

    @Override
    public LabTestTemplate findByCode(String code) {
        return this.labTestTemplateDao.findByCode(code.toLowerCase());
    }

    @Override
    public PaginationResponsePojo findAll(Pageable sortedByDateCreated) {

        int pageNumber = sortedByDateCreated.getPageNumber();

        int pageSize = sortedByDateCreated.getPageSize();

        Page<LabTestTemplate> labTestTemplatePage = this.labTestTemplateDao.findAll(sortedByDateCreated);

        //System.out.println(labTestTemplatePage.getTotalPages());

        List<LabTestTemplate> labTestTemplateList = labTestTemplatePage.get().collect(Collectors.toList());

        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();

        List<LabTestResponsePojo> labTestResponsePojos = new ArrayList<>();

        for (int i = 0; i < labTestTemplateList.size(); i++) {

            LabTestTemplate foundLabTestTemplate = labTestTemplateList.get(i);
            LabTestResponsePojo labTestResponsePojo = new LabTestResponsePojo();

            if (pageNumber == 0) {
                labTestResponsePojo.setPosition((long) (i + 1));
            } else {
                labTestResponsePojo.setPosition((long) (i + pageSize + pageNumber));
            }

            boolean assigned = false;
            if(foundLabTestTemplate.getLabTest() != null) {
                assigned = true;
            }

            labTestResponsePojo.setCode(foundLabTestTemplate.getCode());
            labTestResponsePojo.setContent(foundLabTestTemplate.getContent());
            labTestResponsePojo.setName(foundLabTestTemplate.getName());
            labTestResponsePojo.setId(foundLabTestTemplate.getId());


            labTestResponsePojo.setAssigned(assigned);
            labTestResponsePojo.setSetUp(assigned);

            if(assigned) {

                //Lab test
                LabTest labTest = foundLabTestTemplate.getLabTest();
                if(labTest != null) {
                    LabTestPojo labTestPojo = new LabTestPojo();
                    labTestPojo.setName(labTest.getName());
                    labTestPojo.setId(labTest.getId());
                    labTestResponsePojo.setLabTest(labTestPojo);

                    //Lab test category
                    LabTestCategory labTestCategory = labTest.getLabTestCategory();
                    if(labTestCategory != null) {
                        labTestResponsePojo.setLabTestCategory(null);
                        LabTestCategoryPojo labTestCategoryPojo = new LabTestCategoryPojo();
                        labTestCategoryPojo.setId(labTestCategory.getId());
                        labTestCategoryPojo.setName(labTestCategory.getName());
                    }
                }

            } else {
                labTestResponsePojo.setLabTest(null);
                labTestResponsePojo.setLabTestCategory(null);
            }


            labTestResponsePojos.add(labTestResponsePojo);

        }

        paginationResponsePojo.setDataList(labTestResponsePojos);
        paginationResponsePojo.setPageNumber((long) pageNumber);
        paginationResponsePojo.setPageSize((long) pageSize);
        paginationResponsePojo.setLength(labTestTemplatePage.getTotalElements());
        return paginationResponsePojo;
    }

    @Override
    public LabTestTemplate assign(LabTestTemplate labTestTemplate, LabTest labTest, PortalUser assignedBy) {

        labTestTemplate.setLabTest(labTest);

        if(labTestTemplate.getAssignedByList() != null) {
            labTestTemplate.getAssignedByList().add(assignedBy);
        } else {
            HashSet<PortalUser> portalUserHashSet = new HashSet<>();
            portalUserHashSet.add(assignedBy);
            labTestTemplate.setAssignedByList(portalUserHashSet);
        }

        LabTestTemplate storedLabTestTemplate = this.labTestTemplateDao.save(labTestTemplate);

        labTest.setLabTestTemplate(labTestTemplate);
        this.labTestDao.save(labTest);
        return storedLabTestTemplate;

    }

    @Override
    public LabTestTemplatePojo removeAssignment(LabTestTemplate labTestTemplate) {

        try {

            LabTest labTest = labTestTemplate.getLabTest();
            labTest.setLabTestTemplate(null);
            this.labTestDao.save(labTest);

            labTestTemplate.setLabTest(null);
            labTestTemplate = labTestTemplateDao.save(labTestTemplate);

            LabTestTemplatePojo labTestTemplatePojo = new LabTestTemplatePojo();
            labTestTemplatePojo.setAssigned(labTestTemplate.getLabTest() != null);
            labTestTemplatePojo.setCode(labTestTemplate.getCode());
            labTestTemplatePojo.setData(labTestTemplate.getContent());
            labTestTemplatePojo.setLabTestCategory(null);
            labTestTemplatePojo.setLabTestName(null);

            return labTestTemplatePojo;

        } catch (Exception e) {
            return  null;
        }
    }
}
