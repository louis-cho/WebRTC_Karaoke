package com.ssafy.server.user.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;


@Data
@Entity(name = "user_key_mapping")
@Table(name = "user_key_mapping")
@IdClass(KeyMapping.class)
public class UserKeyMapping {

    @Id
    @Type(type = "uuid-char")
    @Column(name = "uuid", length = 36)
    private UUID uuid;
    @Id
    @Column(name = "user_pk")
    private int userPk;
}