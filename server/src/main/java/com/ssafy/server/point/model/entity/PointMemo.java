package com.ssafy.server.point.model.entity;


import com.ssafy.server.audit.Auditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PointMemo extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer pointMemoId;

    //logical fk 설정.
    @Column
    Integer userPK;

    @Column
    Integer point = 0;

    @Column
    LocalDateTime lastTime = LocalDateTime.now(); //최초값 생성시간.


    public void setPointMemoId(Integer pointMemoId) {
        this.pointMemoId = pointMemoId;
    }

    public Integer getPointMemoId() {
        return pointMemoId;
    }
}
