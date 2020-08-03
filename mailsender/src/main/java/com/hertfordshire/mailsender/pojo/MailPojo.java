package com.hertfordshire.mailsender.pojo;

import javax.mail.internet.InternetAddress;
import java.util.List;
import java.util.Map;

public class MailPojo {

    private String from;
    private String to;
    private String subject;
    private InternetAddress[] peopleCopied;
    private List<Object> attachments;
    private Map<String, Object> model;
    private String templateName;


    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Object> attachments) {
        this.attachments = attachments;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

    public InternetAddress[] getPeopleCopied() {
        return peopleCopied;
    }

    public void setPeopleCopied(InternetAddress[] peopleCopied) {
        this.peopleCopied = peopleCopied;
    }
}
