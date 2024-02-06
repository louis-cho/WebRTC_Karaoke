package com.ssafy.server.like.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class LikeStat {

    @Id
    private Integer feedId;  // feedId를 식별자로 사용

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