package com.ssafy.server.point.service;

import com.ssafy.server.point.model.dto.PointLogDto;


public interface PointService {
    Integer checkPoint(int userPK);
    Integer chargePoint(PointLogDto pointLogDto);
    Integer donatePoint(PointLogDto pointLogDto);
    void refreshMemo(int userPK);
}
