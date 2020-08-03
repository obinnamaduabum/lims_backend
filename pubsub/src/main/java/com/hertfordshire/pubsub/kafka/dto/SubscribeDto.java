package com.hertfordshire.pubsub.kafka.dto;

import javax.validation.constraints.NotBlank;
import java.util.List;

public class SubscribeDto {

    @NotBlank
    private String groupId;


    private List<String> topics;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<String> getTopics() {
        return topics;
    }

    public void setTopics(List<String> topics) {
        this.topics = topics;
    }
}
