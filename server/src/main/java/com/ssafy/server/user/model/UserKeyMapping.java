package com.ssafy.server.user.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.UUID;

@Entity
@Data
@IdClass(UserKeyMappingPK.class)
public class UserKeyMapping {

    @Id
    @Column(name = "user_key")
    private UUID uuid;

    @Id
    @Column(name = "user_pk")
    private int userPk;
}
