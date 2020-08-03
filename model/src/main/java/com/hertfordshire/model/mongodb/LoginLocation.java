package com.hertfordshire.model.mongodb;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.sql.Timestamp;

public class LoginLocation {

    @Id
    private ObjectId _id;

    private Timestamp createdAt;

    private Timestamp updatedAt;
}
