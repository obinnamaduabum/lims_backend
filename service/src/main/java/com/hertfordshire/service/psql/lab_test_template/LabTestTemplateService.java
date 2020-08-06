package com.hertfordshire.service.psql.lab_test_template;

import com.hertfordshire.dto.LabTestTemplateCreateDto;
import com.hertfordshire.dto.LabTestTemplateEditDto;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestTemplate;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.pojo.LabTestTemplatePojo;
import com.hertfordshire.pojo.PaginationResponsePojo;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LabTestTemplateService {

    LabTestTemplate save(LabTestTemplateCreateDto labTestTemplateCreateDto, PortalUser portalUser);

    LabTestTemplate edit(LabTestTemplate labTestTemplate, LabTestTemplateEditDto labTestTemplateEditDto);

    Optional<LabTestTemplate> findById(Long id);

    LabTestTemplate findByName(String name);

    LabTestTemplate findByCode(String code);

    PaginationResponsePojo findAll(Pageable sortedByDateCreated);

    LabTestTemplate assign(LabTestTemplate labTestTemplate, LabTest labTest,  PortalUser portalUser);

    LabTestTemplatePojo removeAssignment(LabTestTemplate labTestTemplate);

}
