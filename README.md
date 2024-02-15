<img src="https://capsule-render.vercel.app/api?type=waving&color=#a374db&height=150&section=header" />

# 노래 해방 🎵
![homePage](/uploads/82b358dc3a816ca1cf7360edab0021e2/homePage.png)

## 프로젝트 소개
- 내가 부른 노래를 sns형태로 공유 할 수 있는 웹 노래방 서비스
- 실시간으로 사람들 앞에서 노래를 부르고, 피드에 공유할수 있습니다.

## 링크
[![EC2](https://img.shields.io/badge/EC2HOME-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white)](https://i10a705.p.ssafy.io/)

[![logo1-removebg-preview](/uploads/f209be2d2ca10643fce6737fb70ed623/logo1-removebg-preview.png)](https://i10a705.p.ssafy.io/)

[![Notion](https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white)](https://www.notion.so/d3b9636405c8451499659f8d8b4ba876)


#개발 환경


# 기술 스택
<div align=center><h1>📚 STACKS</h1></div>
<div align=center>
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white">
<img src="https://img.shields.io/badge/Quasar-16B7FB?style=for-the-badge&logo=Quasar&logoColor=white"/>

<br/>

<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/redis-DC382D?style=for-the-badge&logo=redis&logoColor=white">
<img src="https://img.shields.io/badge/amazons3-569A31?style=for-the-badge&logo=amazons3&logoColor=white">
<img src="https://img.shields.io/badge/JPA-000000?style=for-the-badge&logo=jpa&logoColor=white"/>
<img src="https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white"/>

<br/>

<img src="https://img.shields.io/badge/amazonaws-232F3E?style=for-the-badge&logo=amazonaws&logoColor=white">
<img src="https://img.shields.io/badge/amazonec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">
<img src="https://img.shields.io/badge/nginx-009639?style=for-the-badge&logo=nginx&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white"/>
<img src="https://img.shields.io/badge/jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white"/>

<br/>

<img src="https://img.shields.io/badge/webrtc-FFE033?style=for-the-badge&logo=webrtc&logoColor=white">
<img src="https://img.shields.io/badge/rabbitmq-FF6600?style=for-the-badge&logo=rabbitmq&logoColor=white">
<img src="https://img.shields.io/badge/elasticsearch-005571?style=for-the-badge&logo=elasticsearch&logoColor=white">
<img src="https://img.shields.io/badge/logstash-005571?style=for-the-badge&logo=logstash&logoColor=white">
<img src="https://img.shields.io/badge/kibana-005571?style=for-the-badge&logo=kibana&logoColor=white">


</div>

# 아키넥처 구성도
![a705.drawio](/uploads/2f07ef3d0808a1239f11647ffaeb4f9a/a705.drawio.png)

# 요구사항
[![Notion](https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white)](https://www.notion.so/f7c62f5c6d1a441bbb191911ca3a1e7b?v=13e99663afb74afcb843af0fbe7daef9)

# 화면정의서 
[![Notion](https://img.shields.io/badge/notion-000000?style=for-the-badge&logo=notion&logoColor=white)](https://www.notion.so/32635f45919f4e62bea19d9727fc7b5f)

# erd
[![ERD Cloud](https://img.shields.io/badge/ERDCloud-000000?style=for-the-badge&logo=ERDCloud&logoColor=white)](https://www.erdcloud.com/d/3SsAyJ6rGXSMzyPcw)

# Figma
[![Figma](https://img.shields.io/badge/figma-F24E1E?style=for-the-badge&logo=figma&logoColor=white)](https://www.figma.com/file/cnHpMUN4kpZ55qLu2FKBD6/%5BNEW%5D-Quick-UXUI-Wireframe-templates!-(%EA%B8%B0%ED%9A%8D%EC%9E%90%EC%99%80-%EB%94%94%EC%9E%90%EC%9D%B4%EB%84%88%EB%A5%BC-%EC%9C%84%ED%95%9C-UXUI-%ED%99%94%EB%A9%B4%EA%B8%B0%ED%9A%8D%EC%84%9C%2F%ED%99%94%EB%A9%B4%EC%84%A4%EA%B3%84%EC%84%9C-%ED%85%9C%ED%94%8C%EB%A6%BF)-(Community)?type=design&node-id=103-2&mode=design&t=VZrVBeL6BKOWOGX8-0)

# 접속 주소
 [노래해방](http://i10a705.p.ssafy.io)

# 프로젝트 한 일(0129 ~ 0202)

### 이준범
BE
- 포인트(마일리지)
  - 포인트(마일리지) 제도에 필요한 api, repository작성
  - redis를 이용한 현재 포인트값 캐싱
  - redis 만료시 중간 포인트 값 갱신 작업 (service)

INFRA
- 인프라
  - ec2에 mariadb, elk, openvidu 배포
  - ec2에 프론트 npm run build를 통한 statc한 파일로 배포
  - ec2에 백엔드 이미지로 배포
  - application.properties에 배포환경에 맞는 포트설정
  - nginx에서 https를 통한 리버스 프록시 구축. 프론트-백,    -백-오픈비두 연결 구현.
  - 프론트-오픈비두 미구현
---

### 노성은
- FE
  - [DM] 메시지 파싱 처리 (메시지 입력 타입 따라 다르게 처리하는)
  - [DM] 1:1 dm 페이지 생성 
  - [DM] 텍스트/이미지 전송 기능
  - [DM] 스크롤 기능
  - [DM] ChatPage dm 작성자에 따른 기능 분리 미구현
  - [FEED] SNS 상세 피드 페이지 (FeedDetail) 생성
  - [FEED] 본인 계정 피드 페이지 (MyProfile)생성
  - [FEED] 피드 리스트 페이지 (FeedPage) 생성
---
### 송준석
- BE
  - [노래방] 백엔드 노래 스켈레톤 코드 작성 
  - [AWS S3] 버킷 생성 및 인증키 발급
  - [AWS S3] 커넥션 설정
  - [AWS S3] S3fileUploader 구현

- FE
  - [노래방] 퍼펙트스코어, MML형식 -> ScoreData 파싱하는 ScoreParser 구현
  - [노래방] 퍼펙트스코어, 음성 인식 및 주파수로 변환하는 ToneDetector 구현
  - [노래방] 퍼펙트스코어, 입력 주파수 및 Scoredata를 그리는 ScoreDrawer 구현
  - [노래방] 일반모드, 노래 데이터 변환 및 출력 구현
  - [노래방] 일반모드, 진행중인 노래 싱크에 맞게 가사 렌더링 구현
---
### 조현우

- BE
  - [유저인증] 서버 측 RSA 복호화
  - [유저인증] 서버 측 클라이언트 암호화 정보 해시 비교 (bcrypt 해시 비교 및 복호화)
  - [유저인증] AOP를 활용해 백엔드 서버 함수 호출 시 함수 정보 출력
  - [유저인증] RSA Key Manager 개발 key-value : ip- 비대칭 KeyPair
  - [유저인증] RSA Key Manager Clean up 스케줄러 개발 - 마지막 요청으로부터 10분 이후 삭제
  - [유저] 시스템 내부에서는 Autoincrement INT Type user primary key, 시스템 외부에서는 UUID를 사용하도록 구성 (성능, 보안)
  - [웹소켓] 백엔드 웹소켓 스켈레톤 코드 개발
  - [알림] 알림 기능 구현 (SSE)
  - [검색] ELK docker-compose 설정
  - [검색] Logstash를 통해 MySQL - Elasticsearch 데이터 동기화
  - [검색] Elasticsearch Spring boot 연동
  - [검색] Elasticsearch를 통해 유저 닉네임 검색 시 INT Type user primary key 반환
  - [검색] Elasticsearch + Logstash + MySQL을 통한 게시글 피드 랭킹 서비스 개발
  - [발표] 프로젝트 기획 발표 담당
  - [댓글] 백엔드 댓글 스켈레톤 코드 작성
  - [피드] 백엔드 피드 스켈레톤 코드 작성
  - [좋아요] 백엔드 좋아요 스켈레톤 코드 작성
  - [DM] 백엔드 Websocket 스켈레톤 코드 작성
  - [DB] Redis <-> MySQL 동기화 스켈레톤 코드 작성

- FE
  - [환경설정] 프론트엔드 vue & quasar 프레임워크 환경 설정
  - [유저인증] 클라이언트 RSA 암호화
  - [DM] 클라이언트 Websocket 스켈레톤 코드 작성


---
### 연정흠
[BE] DM
- STOMP pub sub & RabbitMQ & Redis & MySQL 활용 DM(채팅) 구현
- [DM] STOMP 실시간 채팅 구현
- [DM] STOMP와 RabbitMQ 연동
- [DM] 채팅방 & 채팅내역 JPA 활용 DB 저장
- [DM] 이전 채팅 내역 및 새로운 채팅 내역 mySQL <-> Redis 로딩 및 저장
- [DM] redis에 이전 채팅 내역이 존재하지 않을 경우 db 조회 후 redis 저장
- [DM] 스케줄링(Batch)을 위한 로직 구현 및 redis 캐시 삭제
- [DM] 채팅방 입장 시, 이전 채팅 내역 중 가장 최근 날짜 데이터 로딩
---
### 고정원
- __BE__
  - __[노래방_세션] 노래방 생성 및 입장__
    - SessionName으로 OpenVidu Session 객체 생성, 이미 존재하면 생성하지 않고 가져옴
    - 세션에 연결된 Connection 객체 생성(입장하기)
    - BE 서버에 Session과 Token을 저장하여 관리
    - Connection 객체의 토큰 반환

  - __[노래방_세션]  노래방 퇴장 및 제거__
    - SessionName과 Token으로 해당하는 Session에서 Token 제거
    - 해당 Session에 더이상 Token이 존재하지 않으면 Session도 제거

  - __[노래방_세션] 노래방 제거__
    - SessionName에 해당하는 Session 강제 제거
    - Session에 들어와있던 Token도 모두 제거

  - __[노래방_세션] 노래방 리스트__
    - OpenVidu 서버에 생성된 모든 Session 정보 반환

  - __[노래방_세션] 노래방 정보__
    - SessionName에 해당하는 Session의 정보 보기

  - __[노래방_녹화] 녹화 시작__
    - SessionName에 해당하는 세션 녹화 시작
    - OutputMode를 설정할 수 있음(Computed, Individual)

  - __[노래방_녹화] 녹화 종료__
    - 녹화 종료, 녹화된 영상은 OpenVidu 서버에 저장
    - url을 통해 저장된 영상 확인 가능

  - __[노래방_녹화] 녹화 영상 제거__
    - RecordingId에 해당하는 녹화 영상을 OpenVidu 서버에서 제거

  - __[노래방_녹화] 녹화 영상 정보__
    - RecordingId에 해당하는 녹화 영상의 정보 반환

  - __[노래방_녹화] 녹화 영상 리스트__
    - OpenVidu 서버에 저장되어있는 모든 영상 정보 반환

  - __[노래방_녹화] 녹화 영상 업로드__
    - OpenVidu에 저장되어 있는 영상을 AWS S3에 업로드
    - OpenVidu 서버 -> BackEnd 서버에 저장 -> AWS S3에 저장

- __FE__
  - [노래방] 생성된 노래방을 리스트로 출력
  - [노래방] 제목과 일치하는 노래방 생성 및 입장
  - [노래방] 노래방 나가기
  - [노래방] 노래방 내 화상채팅 구현
  - [노래방] 음성필터 적용하기(에코, 증폭, 피치), 적용할 필터를 고르고 강도 설정
  - [노래방] 노래방 내 채팅 구현
  - [노래방] 카메라, 마이크 on/off 기능 구현
  - [노래방] 카메라, 마이크 변경 기능 구현
  - [노래방] 녹화 시작, 종료, 확인, 삭제 구현 -> 저장모드, 카메라, 마이크 사용여부 선택 가능

___
# firstPjtTest

공통테스트

## src/pages/HomePage.vue

-   vertical carousel
-   일단 페이지 문구만 기입함
-   로그인/회원가입 진행되는 모달 만듦(src/components/StartTest.vue)
-   > src/components/SignIn.vue는 로그인 모달, 회원가입 모달 따로 만든 것

-   써드 파티 로그인은 더 공부해야 함
-   홈페이지 배경색, 아이콘, gif 추후 추가 해야함

# OPENVIDU TEST

## 기존 server 변경점

-   application.properities 값 추가
-   OpenViduAPI.java 파일 추가
-   build.gradle에 dependency 추가

## DOCKER에 OPENVIDU 띄우기

-   DOCKER 설치 후, cmd 창에서

```
docker run -p 4443:4443 --rm -e OPENVIDU_SECRET=MY_SECRET -e OPENVIDU_RECORDING=true -e OPENVIDU_RECORDING_PATH=/opt/openvidu/recordings -v /var/run/docker.sock:/var/run/docker.sock -v /opt/openvidu/recordings:/opt/openvidu/recordings openvidu/openvidu-dev:2.29.0
```

## 백서버는 그대로 실행(port=5000)

## 프론트 서버 띄우기

```
cd S10P12A705/front
npm install
npm run serve
```

-   이후 localhost:8080으로 접속해서 확인
-   localhost:4443에서 connectionTest 가능
-   필터 추가

## ELK stack 설치 및 확인하기

### 패키지 목록 업데이트

```
sudo apt-get update
```

### Docker 설치

```
sudo apt-get install docker.io
```

### Docker 서비스 시작

```
sudo systemctl start docker
```

### 부팅 시에 Docker 자동 실행 설정

```
sudo systemctl enable docker
```

### Docker Compose 설치

```
sudo apt-get install docker-compose
```

### gedit 텍스트 편집기 설치

```
sudo apt-get install gedit
```

```
$ mkdir [프로젝트 폴더]
$ cd [프로젝트 폴더]
$ gedit docker-compose.yml
```

// docker-compose.yml
```
version: '3.6'
services:
  Elasticsearch:
    image: elasticsearch:7.17.16
    container_name: elasticsearch
    restart: always
    volumes:
    - elastic_data:/usr/share/elasticsearch/data/
    environment:
      ES_JAVA_OPTS: "-Xmx256m -Xms256m"
      discovery.type: single-node
    ports:
    - '9200:9200'
    - '9300:9300'
    networks:
      - elk

  Logstash:
    image: logstash:7.17.16
    container_name: logstash
    restart: always
    volumes:
    - ./logstash/:/logstash_dir
    command: logstash -f /logstash_dir/logstash.conf
    depends_on:
      - Elasticsearch
    ports:
    - '9600:9600'
    environment:
      LS_JAVA_OPTS: "-Xmx256m -Xms256m"
    networks:
      - elk

  Kibana:
    image: kibana:7.17.16
    container_name: kibana
    restart: always
    ports:
    - '5601:5601'
    environment:
      - ELASTICSEARCH_URL=http://elasticsearch:9200
    depends_on:
      - Elasticsearch
    networks:
      - elk
volumes:
  elastic_data: {}

networks:
  elk:

```
// end of docker-compose.yml

```
$ mkdir logstash
$ cd logstash
$ gedit logstash.conf
```

// mysql 설정
```
# mysql connector java jar 다운로드
wget https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.30/mysql-connector-java-8.0.30.jar

# 압축 해제
tar -xzf mysql-connector-java-8.0.30.tar.gz

# Logstash 설치 디렉토리로 이동
cd [logstash_dir]

# 다운로드 받은 JAR 파일을 해당 디렉토리로 이동
mv /path/to/mysql-connector-java-8.0.30.jar /path/to/logstash/
```

// logstash.conf
```
# synchronization with elasticseasrch & mysql
input {
  
  jdbc {
    jdbc_connection_string => "jdbc:mysql://i10a705.p.ssafy.io:3306/testuser"
    jdbc_user => "root"
    jdbc_password => "1234"
    jdbc_driver_library => "/logstash_dir/mysql-connector-java-8.0.30.jar"
    jdbc_driver_class => "com.mysql.cj.jdbc.Driver"
    
    statement => "SELECT * FROM likes WHERE timestamp > :sql_last_value"
    
    schedule => "*/10 * * * * *"
    use_column_value => true
    tracking_column => "timestamp"
    tracking_column_type => "timestamp"
    clean_run => false
    type => "like"
  }
  
  jdbc {
    jdbc_connection_string => "jdbc:mysql://i10a705.p.ssafy.io:3306/testuser"
    jdbc_user => "root"
    jdbc_password => "1234"
    jdbc_driver_library => "/logstash_dir/mysql-connector-java-8.0.30.jar"
    jdbc_driver_class => "com.mysql.cj.jdbc.Driver"
    
    statement => "SELECT * FROM hit WHERE timestamp > :sql_last_value"
    
    schedule => "*/10 * * * * *"
    use_column_value => true
    tracking_column => "timestamp"
    tracking_column_type => "timestamp"
    clean_run => false
    type => "hit"
  }
}

filter {
  # mutate {
  #  remove_field => ["introduction", "profile_img_url", "role", "user_key"]
  # }
}

output {
  if [type] == "like" {
    elasticsearch {
      hosts => ["elasticsearch:9200"]
      index => "likes"
    }
  }
  
  if [type] == "hit" {
  	elasticsearch {
  	  hosts => ["elasticsearch:9200"]
  	  index => "hit"
  	}
  }
  
  stdout { codec => rubydebug }
}


filter {
}

output {
  if [type] == "like" {
    elasticsearch {
      hosts => ["localhost:9200"]
      index => "like"                             # index를 like로 데이터 동기화
    }
  }

  if [type] == "view" {
    elasticsearch {
      hosts => ["localhost:9200"]
      index => "hit"                              # index를 hit로 데이터 동기화
    }
  }
}

```
// end of logstash-config

```
$ docker-compose up
```

localhost:5601로 접속하면 확인 가능

# 디렉토리 구조
```
├─front
│  ├─docker
│  ├─public
│  │  └─resources
│  │      └─images
│  └─src
│      ├─assets
│      │  ├─icon
│      │  └─img
│      ├─boot
│      ├─components
│      │  └─karaoke
│      ├─css
│      ├─js
│      │  ├─chat
│      │  ├─config
│      │  ├─encrypt
│      │  ├─karaoke
│      │  ├─perfectScore
│      │  └─websocket
│      ├─layouts
│      ├─pages
│      ├─router
│      └─stores
└─server
    └─src
        ├─main
        │  ├─java
        │  │  └─com
        │  │      └─ssafy
        │  │          └─server
        │  │              ├─audit
        │  │              ├─chat
        │  │              │  ├─controller
        │  │              │  ├─model
        │  │              │  ├─repository
        │  │              │  └─service
        │  │              ├─comment
        │  │              │  ├─controller
        │  │              │  ├─model
        │  │              │  ├─repository
        │  │              │  └─service
        │  │              ├─common
        │  │              │  └─util
        │  │              ├─config
        │  │              ├─exception
        │  │              │  ├─feed
        │  │              │  ├─like
        │  │              │  ├─request
        │  │              │  └─user
        │  │              ├─feed
        │  │              │  ├─controller
        │  │              │  ├─model
        │  │              │  ├─rank
        │  │              │  │  ├─document
        │  │              │  │  ├─model
        │  │              │  │  └─service
        │  │              │  ├─repository
        │  │              │  └─service
        │  │              ├─hit
        │  │              │  ├─controller
        │  │              │  ├─document
        │  │              │  ├─model
        │  │              │  ├─repository
        │  │              │  └─service
        │  │              ├─karaoke
        │  │              │  ├─controller
        │  │              │  ├─model
        │  │              │  ├─repository
        │  │              │  └─service
        │  │              ├─like
        │  │              │  ├─controller
        │  │              │  ├─document
        │  │              │  ├─model
        │  │              │  ├─repository
        │  │              │  └─service
        │  │              ├─notification
        │  │              ├─point
        │  │              │  ├─controller
        │  │              │  ├─model
        │  │              │  │  ├─dto
        │  │              │  │  └─entity
        │  │              │  ├─repository
        │  │              │  └─service
        │  │              ├─syncdata
        │  │              └─user
        │  │                  ├─auth
        │  │                  ├─controller
        │  │                  ├─document
        │  │                  ├─model
        │  │                  ├─repository
        │  │                  ├─secure
        │  │                  ├─service
        │  │                  └─util
        │  └─resources
        │      └─templates
        └─test
            └─java
                └─com
                    └─ssafy
                        └─server
```

<img src="https://capsule-render.vercel.app/api?section=footer&type=waving&color=#a374db&height=150" />
