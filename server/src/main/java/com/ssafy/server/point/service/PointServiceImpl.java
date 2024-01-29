package com.ssafy.server.point.service;

import com.ssafy.server.point.model.dto.PointLogDto;
import com.ssafy.server.point.model.entity.PointLog;
import com.ssafy.server.point.model.entity.PointMemo;
import com.ssafy.server.point.repository.PointMemoRepository;
import com.ssafy.server.point.repository.PointLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService{


    private final RedisTemplate<String, Object> redisTemplate;
    private final PointMemoRepository pointMemoRepository;
    private final PointLogRepository pointLogRepository;

    @Override
    public Integer checkPoint(int userPK) {
        Integer point = null;
        //레디스에 있으면 바로 리턴
        point = (Integer) redisTemplate.opsForValue().get("point_"+userPK);
        if(point != null){
            System.out.println("레디스에 값있음.");
            return point;
        }

        //레디스에 없으면 계산
        point = calculatePoint(userPK);

        //레디스에 올리기
        System.out.println("레디스에 값을 갱신혹은 올리기");
        redisTemplate.opsForValue().set("point_"+userPK, point);

        return point;
    }

    @Override
    public Integer chargePoint(PointLogDto pointLogDto) {
        System.out.println(pointLogDto.getToUser()+"충전. "+pointLogDto.getPoint());
        //잔액 확인
        Integer point = checkPoint(pointLogDto.getToUser());

        //포인트 로그찍기
        PointLog pointLog = new PointLog();
        pointLog.setFromUser(pointLogDto.getFromUser());
        pointLog.setToUser(pointLogDto.getToUser());
        pointLog.setAmount(pointLogDto.getPoint());
        pointLogRepository.save(pointLog);

        //레디스갱신
        redisTemplate.opsForValue().set("point_"+pointLogDto.getToUser(), point + pointLogDto.getPoint());

        return point + pointLogDto.getPoint();
    }

    @Override
    public Integer donatePoint(PointLogDto pointLogDto) {
        //잔액확인
        Integer fromUserPoint = checkPoint(pointLogDto.getFromUser());
        Integer toUserPoint =  checkPoint(pointLogDto.getToUser());

        //후원가능하면
        if(fromUserPoint >= pointLogDto.getPoint()){
            //포인트 로그찍기
            PointLog pointLog = new PointLog();
            pointLog.setFromUser(pointLogDto.getFromUser());
            pointLog.setToUser(pointLogDto.getToUser());
            pointLog.setAmount(pointLogDto.getPoint());
            pointLogRepository.save(pointLog);

            //레디스갱신
            redisTemplate.opsForValue().set("point_"+pointLogDto.getFromUser(), fromUserPoint - pointLogDto.getPoint());
            redisTemplate.opsForValue().set("point_"+pointLogDto.getToUser(), toUserPoint + pointLogDto.getPoint());

            return fromUserPoint - pointLogDto.getPoint();
        }
        //후원불가능하면
        //널 리턴
        return null;
    }

    Integer calculatePoint(int userPK){
        System.out.println("calculatePoint 호출");
        Integer point = null;
        List<PointMemo> pointMemos;

        //포인트 중간 메모 가져오기
        //추후 옵셔널 추가.
        pointMemos = pointMemoRepository.findTop2ByUserPKOrderByLastTimeDesc(userPK); //유저아이디, 중간값 두개 가져오기.
        System.out.println("포인트메모개수 : "+ pointMemos.size());

        //중간 메모값 0개면
        if(pointMemos.size() <= 1){
            if(pointMemos.size() == 0) {
                //한개 생성
                System.out.println("pointMemos 널.");
                PointMemo pointMemo = new PointMemo();
                pointMemo.setUserPK(userPK);
                pointMemo.setPoint(0);
                pointMemoRepository.save(pointMemo);
                System.out.println("생성완료.");
            }
            //처음부터 계산
            point = pointLogRepository.calculatePoint(userPK, LocalDateTime.MIN);
            return point;
        }


        //중간메모값 2개 이상이면 검산로직 수행.
        if(pointMemos.size() == 2){
            int tmp1 = pointMemos.get(0).getPoint() + pointLogRepository.calculatePoint(userPK, pointMemos.get(0).getLastTime());
            int tmp2 = pointMemos.get(1).getPoint() + pointLogRepository.calculatePoint(userPK, pointMemos.get(1).getLastTime());
            if(tmp1 == tmp2){
                point = tmp1;
                return point;
            }
            point = pointLogRepository.calculatePoint(userPK, LocalDateTime.MIN);
            return point;
        }


       return null;
    }
}
