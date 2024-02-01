package com.ssafy.server.point.controller;

import com.ssafy.server.point.model.dto.PointLogDto;
import com.ssafy.server.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.sql.SQLOutput;

@RestController
@RequestMapping("/api/v1/point")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    //포인트 조회
    @GetMapping("/")
    ResponseEntity<?> checkPoint() throws Exception{


        //유저아이디 받아오기. 추후 인증토큰에서 찾기
        Integer userPK = 1;
        Integer point = null;

        if(userPK != null){
            point = pointService.checkPoint(userPK);
        }else{
            //예외처리
            throw new Exception("로그인 해주세요.");
        }

        if(point == null){
            //예외처리
            throw new Exception("잘못된 요청입니다.");
        }

        return new ResponseEntity<Integer>(point, HttpStatus.OK);
    }

    //포인트 충전
    @PostMapping("/charge")
    ResponseEntity<?> chargePoint(@RequestBody PointLogDto pointLogDto) throws Exception{
        //유저 아이디 받아오기. 추후 인증토큰에서 찾기
        Integer userPK = 1;
        Integer point = null;

        pointLogDto.setToUser(userPK);

        if(userPK != null){
            point = pointService.chargePoint(pointLogDto);
        }else{
            //예외처리
            throw new Exception("로그인 해주세요.");
        }

        if(point == null){
            //예외처리
            throw new Exception("잘못된 요청입니다.");
        }

        return new ResponseEntity<Integer>(point, HttpStatus.OK);
    }

    //포인트 반환
    @PostMapping("/donation")
    ResponseEntity<?> donatePoint(@RequestBody PointLogDto pointLogDto) throws Exception{
        //유저 아이디 받아오기. 추후 인증토큰에서 찾기
        Integer userPK = 1;
        Integer point = null;

        //로그인한 유저 아이디
        pointLogDto.setFromUser(userPK);

        System.out.println(pointLogDto.getToUser());

        if(userPK != null){
            point = pointService.donatePoint(pointLogDto);
        }else{
            //예외처리
            throw new Exception("로그인 해주세요.");
        }

        if(point == null){
            //예외처리
            throw new Exception("잘못된 요청입니다.");
        }

        return new ResponseEntity<Integer>(point, HttpStatus.OK);
    }

}
