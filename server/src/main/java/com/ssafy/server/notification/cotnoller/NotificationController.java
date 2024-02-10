package com.ssafy.server.notification.cotnoller;

import com.ssafy.server.notification.dto.NotificationRequestDto;
import com.ssafy.server.notification.dto.NotificationResponseDto;
import com.ssafy.server.notification.entity.Notification;
import com.ssafy.server.notification.service.NotificationService;
import com.ssafy.server.notification.util.SseEmitters;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/notifications")
public class NotificationController {

    private final SseEmitters sseEmitters; //콜백함수 다른 쓰레드에서 실행되므로 thread-safe한 자료구조 사용.
    private final NotificationService notificationService;


    //요청보낸 유저의 구독(연결) 요청
    @GetMapping(value = "/subscribe")
    public ResponseEntity<SseEmitter> subscribe() throws IOException {
        System.out.println("subscribe() 컨트롤러 메소드 호출");
        Integer userPk = 1; //추후 헤더에서 가져온다. 로그인한 유저 아이디
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); //새로운 연결 객체 생성. 매개변수로 만료시간 줄 수 있다. 1시간.
        sseEmitters.add(userPk, emitter); //객체 메모리에 저장.

        //연결했으면, 더미데이터 연결확인 메시지 보내기.
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")    //해당 이벤트의 이름 지정.
                    .data("notification connected!")); //503 에러 방지를 위한 더미 데이터
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //보내고나서, 연결 잘됐다고 응답해주기.
        return ResponseEntity.ok(emitter);
    }

    //알림메시지 생성 및 전송.
    @PostMapping("/sendNotification")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequestDto notificationRequestDto) {
        int fromUser = 1; //추후 헤더에서 가져온다. 보낼 유저 아이디
        notificationRequestDto.setFromUser(fromUser);
//        notificationRequestDto.setTo(notificationRequestDto.getTo().keyToPk);
        Notification notification = notificationService.makeNotification(notificationRequestDto);

        SseEmitter emitter = sseEmitters.getSseEmitter(notification.getToUser()); //메시지 보낼 유저의 emitter객체찾기.
        if (emitter != null  && notification.getToUser() != null ) {
            System.out.println("메시지 보낼 emitter : "+emitter);
            try {
                //메시지보내고
                emitter.send(SseEmitter.event()
                        .name("message")
                        /////////////@@@@@@@@@@@@@@@ UUID로 바꿔서보내야하나.
                        .data(notification.getNotificationId().toString(), MediaType.TEXT_PLAIN));
           } catch (IOException e) {
                sseEmitters.remove(notification.getNotificationId(), emitter);
            }
        }
        else{
            System.out.println("에미터 없음. 보낼필요없음");
        }
        return ResponseEntity.ok().build();
    }

    //안읽은 알림개수
    @GetMapping("/count")
    Integer countUnreadNotifications(){
        int toUser = 1; //추후 헤더같은곳에서 가져온 로그인한 유저아이디.
        Integer count = notificationService.countUnReadNotifications(toUser);
        if(count == null) count = 0;
        return count;
    }

    //안읽은 알림객체리스트
    @GetMapping
    ResponseEntity<List<NotificationResponseDto>> getNotificationList(){
        int toUser = 1; //추후 토큰같은데서 가져오는게 아니지.
        List<NotificationResponseDto> notificationResponseDtoList = notificationService.getNotificationList(toUser);
        return new ResponseEntity<>(notificationResponseDtoList, HttpStatus.OK);
    }

    @GetMapping("/{notificationId}")
    ResponseEntity<NotificationResponseDto> getNotification(@PathVariable Integer notificationId){
        NotificationResponseDto notificationResponseDto = notificationService.getNotification(notificationId);
//        System.out.println("알림목록갱신위한 추가 알림받아옴. userPk : "+userPk);
        NotificationResponseDto dto1 = new NotificationResponseDto();
        dto1.setNotificationId(3);
        dto1.setMessage("유저a가 당신을 노래방에 좋아요를 눌렀습니다.");
        dto1.setInfo("노래방주소.");
        dto1.setType("karaoke");

        return new ResponseEntity<>(notificationResponseDto,HttpStatus.OK);
    }

    //알림 하나 읽음 처리
    @GetMapping("/read/{notificationId}")
    ResponseEntity<Void> changeNotificationStatus(@PathVariable Integer notificationId){
        System.out.println("@@@@@@@@@@@@@@@@@@@@@알림수정요청 날라옴@@@@@@@@@@@@@@@");
        notificationService.readNotification(notificationId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    //안읽은 알림 모두 읽음 처리
    @PostMapping("/readAll")
    ResponseEntity<Void> changeNotificationStatus(@RequestBody List<NotificationResponseDto> notificationResponseDtoList){
        Integer userPk = 1;
        List<Integer> notificationIds = notificationResponseDtoList.stream()
                .map(NotificationResponseDto::getNotificationId)
                .collect(Collectors.toList());
        notificationService.readAllNotifications(notificationIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
