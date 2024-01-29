package com.ssafy.server.point.model.entity;

import com.ssafy.server.audit.Auditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class PointLog extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer pointLogId;

    //일단 logical fk로 갈까
    @Column
    Integer fromUser;

    @Column
    Integer toUser;

    @Column
    Integer amount;


}
