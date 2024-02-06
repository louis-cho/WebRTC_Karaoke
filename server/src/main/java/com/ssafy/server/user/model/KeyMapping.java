package com.ssafy.server.user.model;

import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
public class KeyMapping implements Serializable {
    private UUID uuid;
    private int userPk;

    // 생성자, 게터, 세터 등 필요한 메소드를 추가하세요.

    public KeyMapping() {
        // 기본 생성자
    }

    public KeyMapping(UUID uuid, int userPk) {
        this.uuid = uuid;
        this.userPk = userPk;
    }

    // 게터, 세터 등은 생략하고 필요시에 추가하세요.
}