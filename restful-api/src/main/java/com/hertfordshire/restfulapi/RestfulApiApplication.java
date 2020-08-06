package com.hertfordshire.restfulapi;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.io.IOException;

@SpringBootApplication(scanBasePackages = "com.hertfordshire")
@EnableJpaRepositories(basePackages = {"com.hertfordshire.dao", "com.hertfordshire.payment.dao", "com.hertfordshire.pubsub.redis.dao"})
@EntityScan(basePackages = {"com.hertfordshire.model","com.hertfordshire.access", "com.hertfordshire.payment.model"})
@EnableMongoRepositories(basePackages = "com.hertfordshire.dao.mongodb")
@EnableJpaAuditing
public class RestfulApiApplication extends SpringBootServletInitializer {


    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean bean = new LocalValidatorFactoryBean();
        bean.setValidationMessageSource(messageSource());
        return bean;
    }

    @Bean(name="freemarkerConfiguration")
    public freemarker.template.Configuration getFreeMarkerConfiguration() {
        freemarker.template.Configuration config = new freemarker.template.Configuration(freemarker.template.Configuration.getVersion());
        config.setClassForTemplateLoading(this.getClass(), "/templates/");
        return config;
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(RestfulApiApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(RestfulApiApplication.class, args);

        try {
            String fileUrl = "/firebase/firebase.json";
            String databaseUrl = "https://projectmerlin-2e62d.firebaseio.com";
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(fileUrl).getInputStream()))
                    .setDatabaseUrl(databaseUrl)
                    .build();
            if(FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
