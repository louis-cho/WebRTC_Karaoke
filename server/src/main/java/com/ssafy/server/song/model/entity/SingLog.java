package com.ssafy.server.song.model.entity;

import com.ssafy.server.audit.Auditable;
import com.ssafy.server.user.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "sing_log")
@Getter
@Setter
public class SingLog extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sing_log_id")
    private int singLogId;

    @Column(name = "user_pk")
    private int userPk;

    @Column(name = "song_id")
    private int songId;

    @Column(name = "sing_mode", length = 2)
    private String singMode;

    @Column(name = "sing_status", length = 2)
    private String singStatus;

    @Column(name = "sing_score")
    private int singScore;

}
