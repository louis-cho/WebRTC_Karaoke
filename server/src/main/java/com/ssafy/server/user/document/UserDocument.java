package com.ssafy.server.user.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import org.springframework.data.annotation.Id;  // 추가

import java.util.UUID;

@Getter
@Setter
@Document(indexName = "user")
public class UserDocument {

    @Id  // 추가
    @Field(type = FieldType.Keyword)
    private int userPk;  // 변경: UUID 대신 String 사용

    @Field(type = FieldType.Text)
    private String nickname;

    // Constructors, getters, setters, etc.

    public UserDocument() {
        // Default constructor required by Elasticsearch
    }

    public UserDocument(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "[UserDocument] >> " + this.userPk + "," + this.nickname + "\n";
    }
}
