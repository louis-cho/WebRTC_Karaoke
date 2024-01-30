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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService{

    public static final long REDIS_EXPIRE_SECOND =  3 * 7 * 24 * 60 * 60L;

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
        System.out.println("레디스에 캐싱");
        redisTemplate.opsForValue().set("point_"+userPK, point, REDIS_EXPIRE_SECOND, TimeUnit.SECONDS);

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
        redisTemplate.opsForValue().set("point_"+pointLogDto.getToUser(), point + pointLogDto.getPoint(),REDIS_EXPIRE_SECOND, TimeUnit.SECONDS);

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
            redisTemplate.opsForValue().set("point_"+pointLogDto.getFromUser(), fromUserPoint - pointLogDto.getPoint(), REDIS_EXPIRE_SECOND, TimeUnit.SECONDS);
            redisTemplate.opsForValue().set("point_"+pointLogDto.getToUser(), toUserPoint + pointLogDto.getPoint(), REDIS_EXPIRE_SECOND, TimeUnit.SECONDS);

            return fromUserPoint - pointLogDto.getPoint();
        }
        //후원불가능하면
        //널 리턴
        return null;
    }

    //중간메모 저장.
    @Override
    public void refreshMemo(int userPK) {
        //마지막으로 저장된 중간메모 불러오기.
        List<PointMemo> pointMemos = pointMemoRepository.findTop2ByUserPKOrderByLastTimeDesc(userPK);
        //redis에 올라갔을때 이미 pointMemo는 무조건 1개 이상. 0일경우 잘못된 PK로의 요청
        if(pointMemos.size() == 0){
            System.out.println("잘못된 요청임.");
            return;
        }

        PointMemo pointMemo = pointMemos.get(0);

        //중간값 갱신로직. 기존 중간값 ~ 만료 5분전까지의 로그를 더함.
//        새로운 중간값 = 기존 중간값 + pointLogRepository.calculatePoint(userPK, 기존중간값 lasttime, 현재시간 - 5분전);
        LocalDateTime endTime = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);

        //불가능한경우이지만 잘못도니요청임
        if(endTime.isBefore(pointMemo.getLastTime())) {
            System.out.println("잘못된 시간값임.");
            return;
        }

        Integer newPoint = pointMemo.getPoint() + pointLogRepository.calculatePoint(userPK, pointMemo.getLastTime(), endTime);
        PointMemo newPointMemo = new PointMemo();
        newPointMemo.setUserPK(userPK);
        newPointMemo.setPoint(newPoint);
        newPointMemo.setLastTime(endTime);
        pointMemoRepository.save(newPointMemo);
        System.out.println("중간값 갱신 완료.");
    }

    Integer calculatePoint(int userPK){
        System.out.println("calculatePoint 호출");
        Integer point = null;
        List<PointMemo> pointMemos;

        //포인트 중간 메모 가져오기
        //추후 옵셔널 추가.
        pointMemos = pointMemoRepository.findTop2ByUserPKOrderByLastTimeDesc(userPK); //유저아이디, 중간값 두개 가져오기.

        //중간 메모값 0개면
        if(pointMemos.size() <= 1){
            if(pointMemos.size() == 0) {
                //한개 생성
                PointMemo pointMemo = new PointMemo();
                pointMemo.setUserPK(userPK);
                pointMemo.setPoint(0);
                pointMemoRepository.save(pointMemo);
                System.out.println("생성완료.");
            }
            //처음부터 계산
            point = pointLogRepository.calculatePoint(userPK, LocalDateTime.MIN, LocalDateTime.now());
            return point;
        }


        //중간메모값 2개 이상이면 검산로직 수행.
        if(pointMemos.size() == 2){
            int tmp1 = pointMemos.get(0).getPoint() + pointLogRepository.calculatePoint(userPK, pointMemos.get(0).getLastTime(), LocalDateTime.now());
            int tmp2 = pointMemos.get(1).getPoint() + pointLogRepository.calculatePoint(userPK, pointMemos.get(1).getLastTime(), LocalDateTime.now());
            if(tmp1 == tmp2){
                point = tmp1;
                return point;
            }
            point = pointLogRepository.calculatePoint(userPK, LocalDateTime.MIN, LocalDateTime.now());
            return point;
        }
       return null;
    }

}
