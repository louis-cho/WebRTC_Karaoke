# firstPjtTest
공통테스트
## src/pages/HomePage.vue 
- vertical carousel 
- 일단 페이지 문구만 기입함
- 로그인/회원가입 진행되는 모달 만듦(src/components/StartTest.vue)
- >src/components/SignIn.vue는 로그인 모달, 회원가입 모달 따로 만든 것


- 써드 파티 로그인은 더 공부해야 함
- 홈페이지 배경색, 아이콘, gif 추후 추가 해야함

# OPENVIDU TEST

## 기존 server 변경점

 - application.properities 값 추가
 - OpenViduAPI.java 파일 추가
 - build.gradle에 dependency 추가


## DOCKER에 OPENVIDU 띄우기

 - DOCKER 설치 후, cmd 창에서
 ```
 docker run -p 4443:4443 --rm -e OPENVIDU_SECRET=MY_SECRET openvidu/openvidu-dev:2.29.0
 ```

## 백서버는 그대로 실행(port=5000)

## 프론트 서버 띄우기

```
cd S10P12A705/front
npm install
npm run serve
```

- 이후 localhost:8080으로 접속해서 확인
- localhost:4443에서 connectionTest 가능
- 필터 추가