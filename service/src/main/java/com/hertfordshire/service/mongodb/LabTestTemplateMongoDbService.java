package com.hertfordshire.service.mongodb;


import com.hertfordshire.dto.LabTestTemplateCreateDto;
import com.hertfordshire.dto.LabTestTemplateEditDto;
import com.hertfordshire.model.mongodb.LabTestTemplateMongoDb;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.pojo.LabTestTemplatePojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;

public interface LabTestTemplateMongoDbService {

    LabTestTemplateMongoDb save(LabTestTemplateCreateDto labTestTemplateCreateDto);

    LabTestTemplateMongoDb findByCode(String code);

    @Transactional
    LabTestTemplatePojo removeAssignment(String code, LabTest labTest);

    PaginationResponsePojo findAll(Pageable sortedByDateCreated);

    Number countAll();

    LabTestTemplateMongoDb edit(LabTestTemplateMongoDb labTestTemplateMongoDb, LabTestTemplateEditDto labTestTemplateEditDto);
}
