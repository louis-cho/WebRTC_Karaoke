package com.ssafy.server.auth.model.entity;

import com.ssafy.server.audit.Auditable;
import com.ssafy.server.song.model.entity.Song;
import com.ssafy.server.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tokenId;
    private String tokenValue;
    private LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_pk")
    private User user;

}

