package com.ssafy.server.point.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PointLogDto {
    private Integer toUser = -1;
    private Integer fromUser = -1;
    private Integer point = 0;
}
