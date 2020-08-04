package com.hertfordshire.dao.psql;

import com.hertfordshire.model.psql.PortalUser;
import com.hertfordshire.utils.lhenum.SignUpTypeConstant;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;


@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = PortalUserDao.class)
@EntityScan(basePackageClasses = PortalUser.class)
public class PortalUserDaoTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PortalUserDao portalUserDao;

    @Test
    public void injectedComponentsAreNotNull() {

        PortalUser portalUser = new PortalUser();
        portalUser.setCode("test");
        portalUser.setAccountBlockedByAdmin(false);
        portalUser.setCreatedBy(null);
        portalUser.setFirstName("test");
        portalUser.setTwoFactor(false);
        portalUser.setSignUpType(SignUpTypeConstant.WEB);
        portalUser.setPortalAccounts(null);
        portalUser.setCode("test");
        portalUser.setCode("test");
        portalUser.setCode("test");

        this.entityManager.persist(portalUser);
        if(this.portalUserDao == null){
            System.out.println("xxxxxxx");
        }

        PortalUser newPortalUser = this.portalUserDao.findByCode("test");
        assertNotNull(newPortalUser);
    }
}
