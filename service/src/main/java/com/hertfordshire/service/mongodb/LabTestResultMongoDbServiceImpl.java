package com.hertfordshire.service.mongodb;

import com.google.gson.Gson;
import com.hertfordshire.dto.PatientRestDto;
import com.hertfordshire.model.mongodb.LabTestResultMongoDb;
import com.hertfordshire.model.psql.LabScientistTestResultModel;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.dao.mongodb.LabTestResultMongoDbDao;
import com.hertfordshire.dao.psql.LabScientistTestResultDao;
import com.hertfordshire.utils.lhenum.LabScientistStatusConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
public class LabTestResultMongoDbServiceImpl implements LabTestResultMongoDbService {

    private static final Logger logger = LoggerFactory.getLogger(LabTestResultMongoDbServiceImpl.class.getSimpleName());

//    @Autowired
//    private LabScientistTestResultDao labScientistTestResultDao;
//
//    @Autowired
//    private LabTestResultMongoDbDao labTestResultMongoDbDao;

    private Gson gson;

    public LabTestResultMongoDbServiceImpl() {
        this.gson = new Gson();
    }


    @Transactional
    @Override
    public LabTestResultMongoDb save(PatientRestDto patientRestDto, PortalUser medicalLabScientist) {


//        Optional<LabScientistTestResultModel> optionalLabScientistTestResultModel = this.labScientistTestResultDao.findById(patientRestDto.getMedicalLabScientistSampleCollectedId());

        Optional<LabScientistTestResultModel> optionalLabScientistTestResultModel = null;
        if(optionalLabScientistTestResultModel.isPresent()) {

            LabScientistTestResultModel labScientistTestResultModel = optionalLabScientistTestResultModel.get();
            labScientistTestResultModel.setMedicalLabScientist(medicalLabScientist);

            labScientistTestResultModel.setLabScientistStatusConstant(LabScientistStatusConstant.COMPLETED);
           // this.labScientistTestResultDao.save(labScientistTestResultModel);

            LabTestResultMongoDb labTestResultMongoDb = new LabTestResultMongoDb();
            labTestResultMongoDb.setCode(patientRestDto.getPatientSampleId());
            String data = this.gson.toJson(patientRestDto.getData());
           // logger.info(data);
            labTestResultMongoDb.setData(data);
            labTestResultMongoDb.setDateCreated(new Date());
            labTestResultMongoDb.setDateUpdated(new Date());
            labTestResultMongoDb.setLabTestResultTemplateId(patientRestDto.getRestTemplateId());
            labTestResultMongoDb.setTestOrderSampleUniqueId(patientRestDto.getPatientSampleId());
            //return this.labTestResultMongoDbDao.save(labTestResultMongoDb);

            return null;
        }
        return null;
    }

    @Override
    public LabTestResultMongoDb findByCode(String code) {
        //return this.labTestResultMongoDbDao.findByCode(code.toUpperCase());
        return null;
    }
}
