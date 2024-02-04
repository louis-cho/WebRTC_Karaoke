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
    private boolean isDeleted;
    private int feedId;


    public Like(Integer likeId, Integer userPk, Boolean status, Boolean isDeleted) {
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
