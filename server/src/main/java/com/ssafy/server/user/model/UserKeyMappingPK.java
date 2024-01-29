package com.ssafy.server.user.model;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserKeyMappingPK implements Serializable {
    private UUID uuid;
    private int userPk;
}