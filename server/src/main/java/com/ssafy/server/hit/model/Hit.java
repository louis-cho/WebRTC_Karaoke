package com.ssafy.server.hit.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

@Entity
@Setter
@Getter
@ToString
public class Hit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int feedId;
    int hit;
    UUID userKey;
}
