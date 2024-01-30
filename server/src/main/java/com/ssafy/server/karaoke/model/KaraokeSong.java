package com.ssafy.server.karaoke.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class KaraokeSong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String artist;

    // 기타 필요한 필드들...

    // 생성자, 게터 및 세터 메서드...

}
