## OPENVIDU TEST

# 기존 server 변경점

 -application.properities 값 추가
 -OpenViduAPI.java 파일 추가
 -build.gradle에 dependency 추가

# DOCKER에 OPENVIDU 띄우기

 -DOCKER 설치 후, cmd 창에서
 ```
 docker run -p 4443:4443 --rm -e OPENVIDU_SECRET=MY_SECRET openvidu/openvidu-dev:2.29.0
 ```

# 백서버는 그대로 실행(port=5000)

# 프론트 서버 띄우기

```
cd S10P12A705/front
npm install
npm run serve
```

# 이후 localhost:8080으로 접속해서 확인

# localhost:4443에서 connectionTest 가능