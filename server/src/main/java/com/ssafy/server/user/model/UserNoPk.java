package com.ssafy.server.user.model;

import com.ssafy.server.auth.model.entity.RefreshToken;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserNoPk {
    private UUID userKey;

    private String nickname;

    private Character role;

    private String profileImgUrl;

    private String introduction;

    private List<RefreshToken> refreshToken;

    public UserNoPk(User user){
        this.userKey = user.getUserKey();
        this.nickname = user.getNickname();
        this.role = user.getRole();
        this.profileImgUrl = user.getProfileImgUrl();
        this.introduction = user.getIntroduction();
        this.refreshToken = user.getRefreshToken();
    }
}
