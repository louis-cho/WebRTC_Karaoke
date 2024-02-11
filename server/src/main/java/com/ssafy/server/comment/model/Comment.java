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
@NamedStoredProcedureQuery(
        name = "GetCommentsByFeedIdWithPagination",
        procedureName = "GetCommentsByFeedIdWithPagination",
        resultClasses = Comment.class,
        parameters = {
                @StoredProcedureParameter(name = "feedIdParam", mode = ParameterMode.IN, type = Integer.class),
                @StoredProcedureParameter(name = "startIndexParam", mode = ParameterMode.IN, type = Integer.class),
                @StoredProcedureParameter(name = "pageSizeParam", mode = ParameterMode.IN, type = Integer.class)
        }
)
@Table(name = "comment", indexes = {
        @Index(name = "idx_feed_id", columnList = "feed_id")
})
public class Comment extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int commentId;

    @Column(name = "user_pk")
    private int userPk;

    @Column(name = "feed_id")
    private int feedId;

    @Column(name = "content")
    private String content;

    @Column(name = "root_comment_id")
    private int rootCommentId;

    @Column(name = "parent_comment_id")
    private int parentCommentId;

    @Column(name = "is_deleted", columnDefinition = "boolean default false")
    private boolean isDeleted;

}
