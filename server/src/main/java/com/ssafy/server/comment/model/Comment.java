package com.ssafy.server.comment.model;

import com.ssafy.server.audit.Auditable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Setter
@Getter
@ToString
public class Comment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int commentId;

    private int userPk;
    private int feedId;
    private String content;
    private int rootCommentId;
    private int parentCommentId;

    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted;
}
