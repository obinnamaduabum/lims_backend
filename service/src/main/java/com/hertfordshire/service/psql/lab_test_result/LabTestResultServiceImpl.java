package com.hertfordshire.service.psql.lab_test_result;

import com.google.gson.Gson;
import com.hertfordshire.dao.psql.LabScientistTestResultDao;
import com.hertfordshire.dao.psql.LabTestResultModelDao;
import com.hertfordshire.dto.PatientResultDto;
import com.hertfordshire.model.psql.*;
import com.hertfordshire.utils.lhenum.LabScientistStatusConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class LabTestResultServiceImpl implements LabTestResultService {

    @Autowired
    private LabTestResultModelDao labTestResultModelDao;


    private Gson gson;


    @Autowired
    private LabScientistTestResultDao labScientistTestResultDao;

    @Autowired
    public LabTestResultServiceImpl() {
        this.gson = new Gson();
    }


    @Override
    public LabTestResultModel save(PatientResultDto patientResultDto,
                                   PortalUser loggedUser) {

        try {

            Optional<LabScientistTestResultModel> optional = this.labScientistTestResultDao.findById(Long.valueOf(patientResultDto.getId()));

            if (optional.isPresent()) {

                LabScientistTestResultModel labScientistTestResultModel = optional.get();

                LabTestResultModel labTestResultModel = new LabTestResultModel();
                labTestResultModel.setContent(this.gson.toJson(patientResultDto.getData()));
                labTestResultModel.setLabScientistTestResult(labScientistTestResultModel);

                labTestResultModel = this.labTestResultModelDao.save(labTestResultModel);

                labScientistTestResultModel.setLabScientistStatusConstant(LabScientistStatusConstant.COMPLETED);

                labScientistTestResultModel.setMedicalLabScientist(loggedUser);

                labScientistTestResultModel.setLabResult(labTestResultModel);

                this.labScientistTestResultDao.save(labScientistTestResultModel);

                return labTestResultModel;
            }

            return null;

        } catch (Exception e) {

            return null;
        }
    }
}
