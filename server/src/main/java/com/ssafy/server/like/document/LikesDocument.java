package com.ssafy.server.like.document;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;


@Document(indexName = "likes")
@Getter
@Setter
@ToString
public class LikesDocument {

    @Id
    @Field(type = FieldType.Keyword)
    private int likeId;

    @Field(type = FieldType.Integer)
    private int userPk;

    @Field(type = FieldType.Integer)
    private int feedId;

    @Field(type = FieldType.Keyword) // Assuming you want to use FieldType.Keyword for 'status'
    private boolean status;

    @Field(type = FieldType.Date)
    private Date timestamp;


    // Constructor, getters, setters, and other necessary methods
}
