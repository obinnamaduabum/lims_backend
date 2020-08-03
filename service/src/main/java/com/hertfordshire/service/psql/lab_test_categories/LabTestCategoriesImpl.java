package com.hertfordshire.service.psql.lab_test_categories;

import com.hertfordshire.dto.LabTestCategoryDto;
import com.hertfordshire.model.psql.LabTestCategory;
import com.hertfordshire.dao.psql.LabTestCategoriesDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LabTestCategoriesImpl implements LabTestCategoriesService {

    @Autowired
    private LabTestCategoriesDao labTestCategoriesDao;

    @Override
    public LabTestCategory save(LabTestCategoryDto labTestCategoryDto) {

        LabTestCategory labTestCategory = new LabTestCategory();
        labTestCategory.setName(labTestCategoryDto.getName().toLowerCase());
        return this.labTestCategoriesDao.save(labTestCategory);
    }

    @Override
    public LabTestCategory save(LabTestCategory labTestCategory) {
        return labTestCategoriesDao.save(labTestCategory);
    }

    @Override
    public List<LabTestCategory> findAll() {
        return this.labTestCategoriesDao.findAll();
    }

    @Override
    public LabTestCategory findByName(String name) {
        return this.labTestCategoriesDao.findByName(name.toLowerCase());
    }

    @Override
    public Optional<LabTestCategory> findById(Long id) {
        return this.labTestCategoriesDao.findById(id);
    }
}
