package com.hertfordshire.service.psql.lab_test_categories;


import com.hertfordshire.dto.LabTestCategoryDto;
import com.hertfordshire.model.psql.LabTestCategory;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;


public interface LabTestCategoriesService {

    @Transactional
    LabTestCategory save(LabTestCategoryDto labTestCategoryDto);

    @Transactional
    LabTestCategory save(LabTestCategory labTestCategory);

    List<LabTestCategory> findAll();

    LabTestCategory findByName(String name);

    Optional<LabTestCategory> findById(Long id);
}
