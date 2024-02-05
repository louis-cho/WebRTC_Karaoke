package com.ssafy.server.user.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.UUID;

@Entity(name = "user_key_mapping")
@Data
@IdClass(UserKeyMappingPK.class)
public class UserKeyMapping {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "uuid", length = 36)
    private UUID uuid;
    @Id
    @Column(name = "user_pk")
    private int userPk;
}