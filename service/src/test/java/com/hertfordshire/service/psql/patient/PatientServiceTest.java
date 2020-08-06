package com.hertfordshire.service.psql.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hertfordshire.dao.psql.PortalAccountDao;
import com.hertfordshire.dao.psql.PortalUserDao;
import com.hertfordshire.dao.psql.RolesDao;
import com.hertfordshire.dto.PatientDto;
import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.service.ServiceApplicationTests;
import com.hertfordshire.service.psql.kafka.subscription.KafkaSubscriptionService;
import com.hertfordshire.service.psql.kafka.topic.KafkaTopicService;
import com.hertfordshire.service.psql.portal_account_role_mapper.PortalAccountAndPortalUserRoleMapperServiceImpl;
import com.hertfordshire.service.psql.portalaccount.PortalAccountService;
import com.hertfordshire.service.psql.portalaccount.PortalAccountServiceImp;
import com.hertfordshire.service.psql.privileges.PrivilegeService;
import com.hertfordshire.service.psql.role.RoleServiceImp;
import com.hertfordshire.service.psql.role.RolesService;
import com.hertfordshire.service.sequence.portal_account_id.PortalAccountSequenceService;
import com.hertfordshire.service.sequence.portal_user_id.PortalUserSequenceService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

//@RunWith(MockitoJUnitRunner.class)
//@SpringBootTest
////@ActiveProfules(value = "test")//or @TestPropertySource(s)

//@SpringBootTest(classes = ServiceApplicationTests.class)
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {ServiceApplicationTests.class})
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PatientServiceTest {

    private PatientService patientService;

    private PortalAccountService portalAccountService;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    private PortalUserSequenceService portalUserSequenceService;

    private PortalAccountSequenceService portalAccountSequenceService;

    @MockBean
    private PortalUserDao portalUserDao;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private PortalAccountAndPortalUserRoleMapperServiceImpl portalAccountAndPortalUserRoleMapperService;

    @MockBean
    private PortalAccountDao portalAccountDao;

    @MockBean
    private RolesDao rolesDao;

    private RolesService rolesService;

    @MockBean
    private PrivilegeService privilegeService;

    @MockBean
    private KafkaTopicService kafkaTopicService;

    @MockBean
    private KafkaSubscriptionService kafkaSubscriptionService;

    @Before()
    public void init() {


        portalUserSequenceService = new PortalUserSequenceService(jdbcTemplate);

        portalAccountSequenceService = new PortalAccountSequenceService(jdbcTemplate);

    }

    @Test
    @Transactional
    public void create() {

        assertThat(portalAccountService).isNotNull();
        assertThat(portalUserSequenceService).isNotNull();
        assertThat(portalAccountSequenceService).isNotNull();
        assertThat(portalUserDao).isNotNull();
        assertThat(passwordEncoder).isNotNull();

        Gson gson = new Gson();

        PatientDto patientDto = gson.fromJson("{\n" +
                "  \"email\": \"obinnatester@fmail.com\",\n" +
                "  \"firstName\": \"test1\",\n" +
                "  \"gender\": \"MALE\",\n" +
                "  \"lastName\": \"test1\",\n" +
                "  \"nextOFKinFirstName\": \"cool\",\n" +
                "  \"nextOFKinLastName\": \"test\",\n" +
                "  \"nextOFKinPhoneNumber\": \"23408067123344\",\n" +
                "  \"nextOFKinPhoneNumberObj\": {\"id\": \"161\", \"name\": \"Nigeria\", \"alpha2\": \"NG\", \"alpha3\": \"NGA\", \"internationalPhoneNumber\": \"234\"},\n" +
                "  \"otherName\": \"test\",\n" +
                "  \"otherPhoneNumberObj\": {\"id\": \"161\", \"name\": \"Nigeria\", \"alpha2\": \"NG\", \"alpha3\": \"NGA\", \"internationalPhoneNumber\": \"234\"},\n" +
                "  \"password\": \"flash\",\n" +
                "  \"phoneNumber\": \"23408056718996\",\n" +
                "  \"phoneNumberObj\": {\"id\": \"161\", \"name\": \"Nigeria\", \"alpha2\": \"NG\", \"alpha3\": \"NGA\", \"internationalPhoneNumber\": \"234\"},\n" +
                "  \"twoFactor\": \"\"\n" +
                "}", PatientDto.class);


        PortalUser savedUser = this.patientService.create(patientDto, false);

        if(savedUser != null){
            System.out.println("gggg");
        } else {
            System.out.println("zzz gggg");
        }

//        assertThat(savedUser).isNotNull();
    }
}
