package com.ssafy.server.like.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.audit.Auditable;
import com.ssafy.server.feed.model.Feed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Objects;


@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "likes")
@Table(name = "likes")
public class Like implements Serializable {
    private static final long serialVersionUID = 7283781432465243784L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Integer likeId;

    @Column(name = "user_pk")
    private Integer userPk;
    @Column(name = "feed_id", nullable = false)
    private Integer feedId;
    @Column(name = "status")
    private boolean status;
    @Column(name = "timestamp", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", nullable = false)
    private Timestamp timestamp;

    // transient 키워드를 사용하여 직렬화에서 제외시킬 필드
    @Transient
    private transient Feed transientFeed;

    public Like(int likeId, int feedId, int userPk, boolean status) {
        this.likeId = likeId;
        this.feedId = feedId;
        this.userPk = userPk;
        this.status = status;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Like that = (Like) obj;
        return Objects.equals(userPk, that.userPk) && Objects.equals(feedId, that.feedId);
    }

    @PrePersist
    public void prePersist() {
        if (this.timestamp == null) {
            this.timestamp = Timestamp.from(Instant.now());
        }
    }

    // 객체를 JSON 문자열로 직렬화하여 반환하는 메서드
    public String serializeObject() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    // JSON 문자열을 이용하여 객체를 역직렬화하여 반환하는 메서드
    public static Like deserializeObject(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Like.class);
    }
    @Override
    public int hashCode() {
        return Objects.hash(userPk, feedId);
    }
}