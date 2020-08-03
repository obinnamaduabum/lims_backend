package com.hertfordshire.mailsender.service;

import com.hertfordshire.mailsender.pojo.MailPojo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class.getSimpleName());

    private JavaMailSender emailSender;

    @Qualifier("freeMarkerConfiguration")
    private Configuration freemarkerConfig;

    public EmailService() {
        emailSender = new JavaMailSenderImpl();
    }

    private void sendSimpleMessage(MailPojo mailPojo) throws MessagingException, IOException, TemplateException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());

        String html;
        try {
            freemarkerConfig.setClassForTemplateLoading(this.getClass(), "/templates/");
            Template template = freemarkerConfig.getTemplate(mailPojo.getTemplateName());
            html = FreeMarkerTemplateUtils.processTemplateIntoString(template, mailPojo.getModel());
            helper.setTo(mailPojo.getTo());
            helper.setText(html, true);
            helper.setSubject(mailPojo.getSubject());
            helper.setFrom(mailPojo.getFrom());
            if(mailPojo.getPeopleCopied() != null) {
                helper.setCc(mailPojo.getPeopleCopied());
            }
            emailSender.send(message);
        } catch (Exception e) {
            logger.error("Exception occurred while processing freeMarker template: {} ", e.getMessage(), e);
        }
    }

    @Async("mailExecutor")
    @Transactional
    public void sendActualEmail (MailPojo mailPojo) {
        try {
            this.sendSimpleMessage(mailPojo);
        } catch (MessagingException | IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}

