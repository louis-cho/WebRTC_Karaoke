package com.ssafy.server.auth.model.entity;

import com.ssafy.server.audit.Auditable;
import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity(name = "refresh_token")
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private int tokenId;
    @Column(name = "token_value")
    private String tokenValue;
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_pk")
    private User user;

}

