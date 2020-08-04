package com.hertfordshire.service.psql.lab_test_categories;

import com.hertfordshire.dao.psql.LabTestCategoriesDao;
import com.hertfordshire.model.psql.LabTestCategory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class LabTestCategoriesTest {

    @Mock
    private LabTestCategoriesDao labTestCategoriesDao;

    @InjectMocks
    private LabTestCategoriesImpl labTestCategoriesService;

    @Test
    public void save() {

        LabTestCategory newLabTestCategory = new LabTestCategory();
        newLabTestCategory.setName("test");

        when(labTestCategoriesDao.save(newLabTestCategory)).thenReturn(newLabTestCategory);

        LabTestCategory labTestCategory = labTestCategoriesService.save(newLabTestCategory);

        assertNotNull(labTestCategory);
    }


    @Test
    public void findByName() {

        String inputString = "HEAMATOLOGY";
        String labTest = inputString.toLowerCase();

        LabTestCategory newLabTestCategory = new LabTestCategory();
        newLabTestCategory.setName(labTest);

        given(labTestCategoriesDao.findByName(labTest)).willReturn(newLabTestCategory);

        LabTestCategory labTestCategory = labTestCategoriesService.findByName(labTest);
        assertEquals(labTest.toLowerCase(), labTestCategory.getName().toLowerCase());
    }
}
