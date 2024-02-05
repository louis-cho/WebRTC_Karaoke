package com.ssafy.server.like.model;

import lombok.*;
import org.springframework.data.redis.core.RedisHash;

@Getter
@Setter
@ToString
@NoArgsConstructor
@RedisHash("like")
public class Like {

    private int likeId;
    private int userPk;
    private boolean status;
    private int feedId;


    public Like(Integer likeId, Integer userPk, Boolean status, Integer feedId) {
    }

    @Override
    public boolean equals(Object obj) {
        try {
            Like other = (Like) obj;
            return this.getLikeId() == other.getLikeId();
        } catch(Exception e) {
            return false;
        }
    }
}
