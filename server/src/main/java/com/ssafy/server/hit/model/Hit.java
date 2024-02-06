// Hit.java
package com.ssafy.server.hit.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.server.feed.model.Feed;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;


@Setter
@Getter
@ToString
@NoArgsConstructor
@Entity(name = "hit")
@Table(name = "hit")
public class Hit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hit_id")
    private Integer hitId;
    @Column(name = "user_pk")
    private Integer userPk;
    @Column(name = "feed_id", insertable = false, updatable = false)
    private Integer feedId;

    @ManyToOne
    @JoinColumn(name = "feed_id")
    private Feed feed;

    // transient 키워드를 사용하여 직렬화에서 제외시킬 필드
    @Transient
    private transient Feed transientFeed;

    Hit(int hitId, int userPk) {
        this.hitId = hitId;
        this.userPk = userPk;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Hit that = (Hit) obj;
        return Objects.equals(userPk, that.userPk) && Objects.equals(feed, that.feed);
    }

    // 객체를 JSON 문자열로 직렬화하여 반환하는 메서드
    public String serializeObject() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    // JSON 문자열을 이용하여 객체를 역직렬화하여 반환하는 메서드
    public static Hit deserializeObject(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        Hit hit = objectMapper.readValue(json, Hit.class);
        return hit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userPk, feed);
    }
}
