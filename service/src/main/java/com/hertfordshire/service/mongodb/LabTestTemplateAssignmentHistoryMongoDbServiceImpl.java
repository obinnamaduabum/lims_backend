package com.hertfordshire.service.mongodb;


import com.hertfordshire.dto.LabTestAssignmentDto;
import com.hertfordshire.model.mongodb.LabTestTemplateAssignmentHistoryMongoDb;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.dao.mongodb.LabTestTemplateAssignmentHistoryMongoDbDao;
import com.hertfordshire.dao.psql.LabTestDao;
import com.hertfordshire.utils.lhenum.LabTestAssignmentStatusType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Service
public class LabTestTemplateAssignmentHistoryMongoDbServiceImpl implements LabTestTemplateAssignmentHistoryMongoDbService {

//    @Autowired
//    private LabTestTemplateAssignmentHistoryMongoDbDao labTestTemplateAssignmentHistoryMongoDbDao;

    @Autowired
    private LabTestDao labTestDao;

    @Transactional
    @Override
    public LabTestTemplateAssignmentHistoryMongoDb save(LabTestAssignmentDto labTestAssignmentDto, LabTest labTest) {

        labTest.setResultTemplateId(labTestAssignmentDto.getLabTestTemplateId());
        labTestDao.save(labTest);

        LabTestTemplateAssignmentHistoryMongoDb labTestTemplateAssignmentHistoryMongoDb =
                new LabTestTemplateAssignmentHistoryMongoDb();
        labTestTemplateAssignmentHistoryMongoDb.setActualLabTestId(""+labTest.getId());
        labTestTemplateAssignmentHistoryMongoDb.setLabTestAssignmentStatusType(LabTestAssignmentStatusType.ADDED);
        labTestTemplateAssignmentHistoryMongoDb.setLabTestTemplateName(""+labTestAssignmentDto.getLabTestTemplateId());
        labTestTemplateAssignmentHistoryMongoDb.setLabTestTemplateId(labTestAssignmentDto.getLabTestTemplateId());
        labTestTemplateAssignmentHistoryMongoDb.setDateCreated(new Date());
        labTestTemplateAssignmentHistoryMongoDb.setDateUpdated(new Date());
        return null;
        //return this.labTestTemplateAssignmentHistoryMongoDbDao.save(labTestTemplateAssignmentHistoryMongoDb);
    }


    @Override
    public List<LabTestTemplateAssignmentHistoryMongoDb> findAllByActualLabTestId(String actualLabTestId) {
        return null;
        //return this.labTestTemplateAssignmentHistoryMongoDbDao.findByActualLabTestId(actualLabTestId.toLowerCase());
    }

    @Override
    public List<LabTestTemplateAssignmentHistoryMongoDb> findAllByLabTestTemplateId(String Id) {
        return null;
        // return this.labTestTemplateAssignmentHistoryMongoDbDao.findByLabTestTemplateId(Id.toLowerCase());
    }

    @Override
    public List<LabTestTemplateAssignmentHistoryMongoDb> findByLabTestTemplateIdOOrderByDateCreatedDesc(String Id) {
        return null;
        // return this.labTestTemplateAssignmentHistoryMongoDbDao.findByLabTestTemplateIdOrderByDateCreatedDesc(Id);
    }
}
