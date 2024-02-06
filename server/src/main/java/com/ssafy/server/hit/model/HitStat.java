package com.ssafy.server.hit.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class HitStat {

    @Id
    private Integer feedId;  // feedId를 식별자로 사용

    private Integer hitCount;

    // getters, setters, constructors 등 생략
    public void decrement() {
        if(this.hitCount > 0)
            this.hitCount -= 1;
    }

    public void increment() {
        if(this.hitCount < Integer.MAX_VALUE) {
            this.hitCount += 1;
        }
    }
}