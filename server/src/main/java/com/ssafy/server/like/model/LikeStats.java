package com.ssafy.server.like.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity(name = "like_stats")
@Table(name = "like_stats")
public class LikeStats {

    @Id
    @Column(name = "feed_id")
    private Integer feedId;  // feedId를 식별자로 사용
    @Column(name = "like_count")
    private Integer likeCount;

    // getters, setters, constructors 등 생략
    public void decrement() {
        if(this.likeCount > 0)
            this.likeCount -= 1;
    }

    public void increment() {
        if(this.likeCount < Integer.MAX_VALUE) {
            this.likeCount += 1;
        }
    }
}