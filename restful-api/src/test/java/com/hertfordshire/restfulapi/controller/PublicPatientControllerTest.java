package com.hertfordshire.restfulapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hertfordshire.dao.psql.PortalAccountAndPortalUserRoleMapperDao;
import com.hertfordshire.dao.psql.PortalAccountDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.dto.PatientDto;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.restfulapi.RestfulApiApplicationTests;
import com.hertfordshire.restfulapi.controller.patient.PublicPatientController;
import com.hertfordshire.service.psql.kafka.subscription.KafkaSubscriptionService;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.service.psql.patient.PatientService;
import com.hertfordshire.service.psql.patient.PatientServiceImpl;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperService;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperServiceImpl;
import com.hertfordshire.service.psql.portalaccount.PortalAccountServiceImp;
import com.hertfordshire.service.psql.portaluser.PortalUserService;
import com.hertfordshire.service.psql.privileges.PrivilegeService;
import com.hertfordshire.service.psql.privileges.PrivilegeServiceImp;
import com.hertfordshire.service.psql.role.RoleServiceImp;
import com.hertfordshire.service.psql.role.RolesService;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequenceService;
import com.hertfordshire.service.sequence.portal_user_id.PortalUserSequenceService;
import com.hertfordshire.utils.ResourceUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

//@ExtendWith(SpringExtension.class)
//@WebMvcTest(PublicPatientController.class)
//@WebAppConfiguration
//@RunWith(SpringRunner.class)
//@SpringBootTest
@ActiveProfiles("test")
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
//@WebMvcTest(value = PublicPatientController.class)
//@EntityScan(basePackages = {"com.hertfordshire.model.psql"})
//@ContextConfiguration(classes = {PublicPatientController.class})
@ComponentScan(basePackages = {
        "com.hertfordshire.access",
        "com.hertfordshire.service.firebase",
//        "com.hertfordshire.pubsub.redis.service.portal_user",
        "com.hertfordshire.utils",
        "com.hertfordshire.service.psql.lab_test",
        "com.hertfordshire.service.psql.lab_test_categories",
        "com.hertfordshire.service.psql.country",
        "com.hertfordshire.service.psql.employee",
        "com.hertfordshire.service.psql.phoneCodes",
        "com.hertfordshire.service.psql.settings",
        "com.hertfordshire.service.psql.payment_method_config",
        "com.hertfordshire.service.psql.patient", "com.hertfordshire.service.psql.portaluser",
        "com.hertfordshire.service.psql.portal_account_role_mapper", "com.hertfordshire.service.sequence",
        "com.hertfordshire.service.psql.portalaccount", "com.hertfordshire.service.psql.role",
        "com.hertfordshire.model.psql", "com.hertfordshire.dao.psql"})
@EntityScan(basePackages = {"com.hertfordshire.model.psql"})
@EnableJpaRepositories(basePackages = {"com.hertfordshire.dao.psql"})
//@EnableMongoRepositories(basePackages = "com.hertfordshire.dao.mongodb")
@DataJpaTest
@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
public class PublicPatientControllerTest extends AbstractJUnit4SpringContextTests {

    private static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private WebApplicationContext context;

    private ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    //instantiate
//    @Autowired
    private PortalAccountServiceImp portalAccountService;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    private PortalUserSequenceService portalUserSequenceService;

    private PortalAccountSequenceService portalAccountSequenceService;

    @MockBean
    private PortalUserDao portalUserDao;

    @MockBean
    private PortalAccountDao portalAccountDao;

    @MockBean
    private RolesDao rolesDao;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @MockBean
    private PortalAccountAndPortalUserRoleMapperService portalAccountAndPortalUserRoleMapperService;

    PatientService patientService;

    private RolesService rolesService;

    @MockBean
    private PrivilegeService privilegeService;

    @MockBean
    private PortalAccountAndPortalUserRoleMapperDao portalAccountAndPortalUserRoleMapperDao;

    @MockBean
    private KafkaTopicService kafkaTopicService;

    @MockBean
    private KafkaSubscriptionService kafkaSubscriptionService;

    @Before
    public void setUp() {

        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        privilegeService = new PrivilegeServiceImp();

        portalUserSequenceService = new PortalUserSequenceService(jdbcTemplate);

        portalAccountSequenceService = new PortalAccountSequenceService(jdbcTemplate);

        portalAccountAndPortalUserRoleMapperService =
        new PortalAccountAndPortalUserRoleMapperServiceImpl(portalAccountAndPortalUserRoleMapperDao, rolesDao);

        rolesService = new RoleServiceImp(rolesDao, privilegeService, portalAccountAndPortalUserRoleMapperService);

        portalAccountService = new PortalAccountServiceImp(portalAccountDao, rolesDao, portalAccountSequenceService);

        patientService = new PatientServiceImpl(
                portalAccountService,
                portalUserSequenceService,
                portalUserDao,
                portalAccountAndPortalUserRoleMapperService,
                encoder,
                portalAccountSequenceService,
                rolesService,
                kafkaTopicService,
                kafkaSubscriptionService);
    }

    @Test
    public void create() throws Exception {

        Gson gson = new Gson();

        InputStream inputStream = ResourceUtil.getResourceAsStream("test-data" + File.separator + "patient.json");

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        PatientDto patientDto = gson.fromJson(bufferedReader, PatientDto.class);

        String inputJson = gson.toJson(patientDto);

        PortalUser portalUser = new PortalUser();


        //when(patientService.create(patientDto)).thenReturn(portalUser);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/default/patient/create")
                .contentType(APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(patientDto));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print()).andReturn();

        System.out.println(mvcResult.getResponse().getContentAsString());
        int status = mvcResult.getResponse().getStatus();
        System.out.println(status);
        assertEquals(201, status);


//         String content = mvcResult.getResponse().getContentAsString();
//         assertEquals(content, "Patient account is created successfully");
    }

}
