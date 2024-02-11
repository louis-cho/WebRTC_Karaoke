package com.ssafy.server.notification.entity;

import com.ssafy.server.audit.Auditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "notification")
@Getter
@Setter
public class Notification extends Auditable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="notification_id", updatable = false)
    private Integer notificationId;

    @Column(name="toUser", nullable = false)
    private Integer toUser;

    @Column(name="fromUser", nullable = false)
    private Integer fromUser; //백

    @Column(name="message")
    private String message;

    @Column(nullable = false)
    private String info; //게시글번호, 노래방주소, 친구번호

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Character status;
}
