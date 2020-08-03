package com.hertfordshire.service.mongodb;


import com.hertfordshire.dto.LabTestTemplateCreateDto;
import com.hertfordshire.dto.LabTestTemplateEditDto;
import com.hertfordshire.model.mongodb.LabTestTemplateAssignmentHistoryMongoDb;
import com.hertfordshire.model.mongodb.LabTestTemplateMongoDb;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestCategory;
import com.hertfordshire.dao.mongodb.LabTestTemplateAssignmentHistoryMongoDbDao;
import com.hertfordshire.dao.mongodb.LabTestTemplateMongoDbDao;
import com.hertfordshire.pojo.LabTestTemplatePojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import com.hertfordshire.dao.psql.LabTestCategoriesDao;
import com.hertfordshire.dao.psql.LabTestDao;
import com.hertfordshire.service.sequence.lab_test_template_code.LabTestTemplateSequenceImpl;
import com.hertfordshire.utils.Utils;
import com.hertfordshire.utils.lhenum.LabTestAssignmentStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class LabTestTemplateMongoDbServiceImpl implements LabTestTemplateMongoDbService {

//    @Autowired
//    private LabTestTemplateMongoDbDao labTestTemplateMongoDbDao;


    @Autowired
    private LabTestTemplateSequenceImpl labTestTemplateSequence;

    @Autowired
    private LabTestDao labTestDao;

    @Autowired
    private LabTestCategoriesDao labTestCategoriesDao;

//    @Autowired
//    private LabTestTemplateAssignmentHistoryMongoDbDao labTestTemplateAssignmentHistoryMongoDbDao;

    @Override
    public LabTestTemplateMongoDb save(LabTestTemplateCreateDto labTestTemplateCreateDto) {

        String Id = String.format("LBTEM%04d%02d%02d%05d",
                LocalDate.now().getYear(),
                LocalDate.now().getMonthValue(),
                LocalDate.now().getDayOfMonth(),
                this.labTestTemplateSequence.getNextId()
        );

        LabTestTemplateMongoDb labTestTemplateMongoDb = new LabTestTemplateMongoDb();
        labTestTemplateMongoDb.setData(labTestTemplateCreateDto.getData());
        labTestTemplateMongoDb.setTitle(labTestTemplateCreateDto.getTitle());
        labTestTemplateMongoDb.setCode(Id.toLowerCase());
        labTestTemplateMongoDb.setDateCreated(new Date());
        labTestTemplateMongoDb.setDateUpdated(new Date());
        return null;
        //return this.labTestTemplateMongoDbDao.save(labTestTemplateMongoDb);
    }

    @Override
    public LabTestTemplateMongoDb findByCode(String code) {
        return null;
       // return this.labTestTemplateMongoDbDao.findByCode(code.toLowerCase());
    }

    @Transactional
    @Override
    public LabTestTemplatePojo removeAssignment(String code, LabTest labTest) {

        try {


                labTest.setResultTemplateId("");
                this.labTestDao.save(labTest);
                LabTestTemplateMongoDb labTestTemplateMongoDb = null;
                        // = this.labTestTemplateMongoDbDao.findByCode(code);

                LabTestTemplateAssignmentHistoryMongoDb labTestTemplateAssignmentHistoryMongoDb = new LabTestTemplateAssignmentHistoryMongoDb();
                labTestTemplateAssignmentHistoryMongoDb.setLabTestTemplateId(code);
                labTestTemplateAssignmentHistoryMongoDb.setLabTestAssignmentStatusType(LabTestAssignmentStatusType.REMOVED);
                labTestTemplateAssignmentHistoryMongoDb.setActualLabTestId("" + labTest.getId());
                labTestTemplateAssignmentHistoryMongoDb.setDateUpdated(new Date());
                labTestTemplateAssignmentHistoryMongoDb.setDateCreated(new Date());
                labTestTemplateAssignmentHistoryMongoDb.setLabTestTemplateName("" + labTest.getId());
                // this.labTestTemplateAssignmentHistoryMongoDbDao.save(labTestTemplateAssignmentHistoryMongoDb);


                LabTestTemplatePojo labTestTemplatePojo = new LabTestTemplatePojo();

                labTestTemplatePojo.setAssigned(false);
                Optional<LabTestCategory> optionalLabTestCategory = this.labTestCategoriesDao.findById(labTest.getLabTestCategory().getId());
                optionalLabTestCategory.ifPresent(labTestCategory -> labTestTemplatePojo.setLabTestCategory(labTestCategory.getName()));
                labTestTemplatePojo.setLabTestName(labTest.getName());

                labTestTemplatePojo.setCode(labTestTemplateMongoDb.getCode());
                labTestTemplatePojo.setData(labTestTemplateMongoDb.getData());
                labTestTemplatePojo.setDateCreated(labTestTemplateMongoDb.getDateCreated());
                labTestTemplatePojo.setDateUpdated(labTestTemplateMongoDb.getDateUpdated());
                labTestTemplatePojo.setTitle(labTestTemplateMongoDb.getTitle());

                return labTestTemplatePojo;

        } catch (Exception e) {
            e.printStackTrace();


            return null;
        }

    }

    @Override
    public PaginationResponsePojo findAll(Pageable sortedByDateCreated) {
        Page<LabTestTemplateMongoDb> pageLabTestTemplateMongoDb = null;

                // = this.labTestTemplateMongoDbDao.findAll(sortedByDateCreated);


        List<LabTestTemplateMongoDb> labTestTemplateMongoDbList = pageLabTestTemplateMongoDb.getContent();
        List<LabTestTemplatePojo> labTestTemplatePojos = new ArrayList<>();

        for (int i = 0; i < labTestTemplateMongoDbList.size(); i++) {
            LabTestTemplatePojo labTestTemplatePojo = new LabTestTemplatePojo();

            LabTest labTest = this.labTestDao.findByResultTemplateId(labTestTemplateMongoDbList.get(i).getCode());

            if (labTest != null) {
                labTestTemplatePojo.setAssigned(true);
                Optional<LabTestCategory> optionalLabTestCategory = this.labTestCategoriesDao.findById(labTest.getLabTestCategory().getId());
                if (optionalLabTestCategory.isPresent()) {
                    labTestTemplatePojo.setLabTestCategory(optionalLabTestCategory.get().getName());
                }
                labTestTemplatePojo.setLabTestName(labTest.getName());
            }

            labTestTemplatePojo.setCode(labTestTemplateMongoDbList.get(i).getCode());
            labTestTemplatePojo.setData(labTestTemplateMongoDbList.get(i).getData());
            labTestTemplatePojo.setDateCreated(labTestTemplateMongoDbList.get(i).getDateCreated());
            labTestTemplatePojo.setDateUpdated(labTestTemplateMongoDbList.get(i).getDateUpdated());
            labTestTemplatePojo.setTitle(labTestTemplateMongoDbList.get(i).getTitle());
            labTestTemplatePojo.setPosition((long) Utils.calculatePageNumber(sortedByDateCreated.getPageNumber(), sortedByDateCreated.getPageSize(), i));
            labTestTemplatePojos.add(labTestTemplatePojo);
        }


        Number count = this.countAll();
        PaginationResponsePojo paginationResponsePojo = new PaginationResponsePojo();
        paginationResponsePojo.setLength((long) count);
        paginationResponsePojo.setPageSize((long) sortedByDateCreated.getPageSize());
        paginationResponsePojo.setPageNumber(sortedByDateCreated.getOffset());
        paginationResponsePojo.setDataList(labTestTemplatePojos);

        return paginationResponsePojo;
    }

    @Override
    public Number countAll() {
        return null;
        //return this.labTestTemplateMongoDbDao.count();
    }

    @Override
    public LabTestTemplateMongoDb edit(LabTestTemplateMongoDb labTestTemplateMongoDb, LabTestTemplateEditDto labTestTemplateEditDto) {
        labTestTemplateMongoDb.setTitle(labTestTemplateEditDto.getTitle());
        labTestTemplateEditDto.setData(labTestTemplateEditDto.getData());
        return null;
        // return this.labTestTemplateMongoDbDao.save(labTestTemplateMongoDb);
    }
}
