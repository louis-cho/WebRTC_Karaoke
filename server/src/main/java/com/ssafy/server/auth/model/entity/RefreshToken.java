package com.ssafy.server.auth.model.entity;

import com.ssafy.server.audit.Auditable;
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

    @Column
    private int userPk;

    @Column
    private String tokenValue;

    @Column
    private LocalDateTime expiredAt;
}

