package com.hertfordshire.restfulapi.controller;

import com.google.gson.Gson;
import com.hertfordshire.dto.PatientDto;
import com.hertfordshire.utils.ResourceUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.junit.Assert.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class PublicPatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void create() throws Exception {

        Gson gson = new Gson();

        InputStream inputStream = ResourceUtil.getResourceAsStream("test-data" + File.separator + "patient.json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        PatientDto patientDto = gson.fromJson(bufferedReader, PatientDto.class);

        String inputJson = gson.toJson(patientDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/v1/api/public/default/patient/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(inputJson);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andReturn();

        int statusCode = mvcResult.getResponse().getStatus();

        Integer expectedTitles[] = {200,201};
        List<Integer> expectedTitlesList = Arrays.asList(expectedTitles);
        assertTrue(expectedTitlesList.contains((statusCode)));
    }

}
