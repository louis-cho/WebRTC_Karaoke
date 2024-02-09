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
    private long notificationId;

    @Column(name="user_pk", nullable = false)
    private long userPk;

    @Column(nullable = false)
    private String info;

    @Column(name="push_url")
    private String pushUrl;

    @Column(nullable = false)
    private char status;
}
