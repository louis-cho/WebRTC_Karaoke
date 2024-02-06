package com.ssafy.server.song.model.entity;

import com.ssafy.server.audit.Auditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class SingLog extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int singLogId;

    @Column
    private int userPk;

    @Column
    private int songId;

    @Column
    private String singMode;

    @Column
    private String singStatus;

    @Column
    private int singScore;

}
