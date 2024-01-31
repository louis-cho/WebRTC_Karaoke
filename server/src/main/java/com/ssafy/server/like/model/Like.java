package com.ssafy.server.like.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int LikeId;
    private int userPk;
    private int feedId;
    private char status;
}
