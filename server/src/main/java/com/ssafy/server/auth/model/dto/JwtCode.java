package com.ssafy.server.auth.model.dto;

import lombok.Getter;

@Getter
public enum JwtCode {
    DENIED,
    ACCESS,
    EXPIRED;
}