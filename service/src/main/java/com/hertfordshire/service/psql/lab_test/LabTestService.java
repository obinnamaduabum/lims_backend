package com.hertfordshire.service.psql.lab_test;


import com.hertfordshire.dto.LabTestDto;
import com.hertfordshire.model.psql.LabTest;
import com.hertfordshire.model.psql.LabTestCategory;
import org.springframework.data.domain.Pageable;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface LabTestService {

    @Transactional
    LabTest save(LabTestDto labTestDto);

    Optional<LabTest> findById(Long id);

    LabTest findByResultTemplateId(String id);

    List<LabTest> findAll();

    LabTest findByName(String name);

    List<LabTest> findByCategoryName(LabTestCategory labTestCategory);

    List<LabTest> findBySearchTerms(String categoryName, String testName, Pageable pageable);

    Long findBySearchTermsTotal(String categoryName, String testName);

    List<LabTest> findBySearchTermsByTestNameWithPagination(String testName, Pageable pageable);
}
