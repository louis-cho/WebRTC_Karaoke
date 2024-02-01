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

```
// docker-compose.yml
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

// end of docker-compose.yml
```

```
$ mkdir logstash-config
$ cd logstash-config
$ gedit logstash-config
```

```
// logstash-config
# Beats 입력을 설정합니다.
input {
  beats {
    port => 5044   # Beats 클라이언트로부터 데이터를 수신할 포트
  }
}

# 데이터를 처리하고 가공하기 위한 필터를 설정합니다.
filter {
  # 여기에 필터 플러그인을 추가하여 데이터를 가공할 수 있습니다.
  # 예: grok, date, mutate 등을 사용하여 로그 메시지에서 필요한 정보 추출
}

# Elasticsearch에 데이터를 출력하기 위한 설정을 추가합니다.
output {
  elasticsearch {
    hosts => "localhost:9200"   # Elasticsearch 호스트와 포트 설정
    index => "logs-%{+YYYY.MM.dd}"   # 인덱스 이름을 날짜 기반으로 지정
  }
}
// end of logstash-config
```

```
$ docker-compose up
```

localhost:5601로 접속하면 확인 가능
