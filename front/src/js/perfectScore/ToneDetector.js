import { noteFromPitch } from "./index.js"; // pitch를 음계로 변환하는 함수 import
import { EventEmitter } from "./EventEmitter.js"; // 이벤트 처리를 위한 EventEmitter import

export class ToneDetector extends EventEmitter {
  // 멤버 변수 초기화

  constructor(ctx) {

    super(); // EventEmitter의 생성자 호출
    this.ctx = ctx; // 오디오 컨텍스트 설정

    this.buf = new Float32Array(2048); // 버퍼 크기가 2048인 Float32Array
    this.pitch = -1; // 음높이를 나타내는 변수 초기화
    this.note = 0; // 음계를 나타내는 변수 초기화
    this.octav = 0; // 옥타브를 나타내는 변수 초기화
    this.inited = false; // 초기화 여부를 나타내는 변수 초기화

  }

  start() {
    if (!this.inited) {
      const analyser = this.ctx.createAnalyser(); // AnalyserNode 생성
      this.analyser = analyser; // AnalyserNode 저장
      this.analyser.fftSize = 2048; // FFT 크기 설정
      this.getUserMedia(); // 사용자 미디어 액세스 요청
    }
  }

  getUserMedia() {
    const n = navigator;
    if (n.mediaDevices === undefined) {
      n.mediaDevices = {};
    }

    if (n.mediaDevices.getUserMedia === undefined) {
      // getUserMedia 메서드가 지원되지 않을 경우에 대한 폴백
      n.mediaDevices.getUserMedia = function(constraints) {
        const getUserMedia =
          n.getUserMedia ||
          n.webkitGetUesrmedia ||
          n.mozGetUserMedia ||
          n.msGetUserMedia;

        if (!getUserMedia) {
          return Promise.reject(new Error("getUserMedia is not supported"));
        }

        return new Promise(function(rs, rj) {
          getUserMedia.call(navigator, constraints, rs, rj);
        });
      };
    }

    const constraints = { audio: true }; // 오디오에 대한 제약 조건 설정
    // 사용자 미디어 액세스 요청
    n.mediaDevices.getUserMedia(constraints).then(stream => {
      const source = this.ctx.createMediaStreamSource(stream); // 오디오 스트림을 소스로 변환
      source.connect(this.analyser); // 소스를 AnalyserNode에 연결
      this.inited = true; // 초기화 상태 변경
      this.emit("inited"); // 초기화 이벤트 발생
    });
  }

  update(delta) {
    if (!this.inited) return; // 초기화되지 않았으면 종료

    this.analyser.getFloatTimeDomainData(this.buf); // 시간 영역 데이터를 버퍼에 채움
    const ac = this.correlate(this.buf, this.ctx.sampleRate); // 주파수 계산

    this.pitch = ac; // 주파수 업데이트
    if (ac === -1) {
      this.note = -1;
    } else {
      this.note = noteFromPitch(ac); // 주파수를 음계로 변환
      this.octav = Math.floor(this.note / 12) - 1; // 옥타브 계산
    }

    this.emit("note", this.note); // 음표 이벤트 발생
  }

  // 교차 상관 함수를 사용하여 주파수 계산
  correlate(buffer, sampleRate) {
    if (this.isSilentBuffer(buffer)) return -1; // 무음 버퍼인지 확인

    const threshold = 0.2; // 임계값 설정
    const buf = this.trimBuffer(buffer, threshold); // 임계값을 기준으로 버퍼 자르기
    const size = buf.length; // 버퍼 크기

    const c = new Array(size).fill(0); // 교차 상관 함수 배열 초기화

    // 교차 상관 함수 계산
    for (let i = 0; i < size; i++) {
      for (let j = 0; j < size - i; j++) {
        c[i] = c[i] + buf[j] * buf[j + i];
      }
    }

    let d = 0;
    while (c[d] > c[d + 1]) d++; // 최대값 위치 계산

    let maxval = -1,
      maxpos = -1;
    // 최대값 찾기
    for (let i = d; i < size; i++) {
      if (c[i] > maxval) {
        maxval = c[i];
        maxpos = i;
      }
    }

    let T0 = maxpos;
    const x1 = c[T0 - 1],
      x2 = c[T0],
      x3 = c[T0 + 1];
    const a = (x1 + x3 - 2 * x2) / 2;
    const b = (x3 - x1) / 2;
    if (a) T0 = T0 - b / (2 * a);

    return sampleRate / T0; // 주파수 반환
  }

  // 버퍼가 무음인지 확인
  isSilentBuffer(buf) {
    const size = buf.length; // 버퍼 크기
    let soundLevel = 0;

    // 사운드 레벨 계산
    for (let i = 0; i < size; i++) {
      soundLevel += buf[i] * buf[i];
    }
    soundLevel = Math.sqrt(soundLevel / size);

    return soundLevel < 0.01; // 사운드 레벨이 임계값보다 작으면 true 반환
  }

  // 임계값을 기준으로 버퍼 자르기
  trimBuffer(buf, threshold = 0.2) {
    const size = buf.length; // 버퍼 크기
    let r1 = 0,
      r2 = size - 1,
      thres = 0.2;
    for (let i = 0; i < size / 2; i++) {
      if (Math.abs(buf[i]) < thres) {
        r1 = i;
        break;
      }
    }
    for (let i = 1; i < size / 2; i++) {
      if (Math.abs(buf[size - i]) < thres) {
        r2 = size - i;
        break;
      }
    }
    return buf.slice(r1, r2); // 임계값을 기준으로 잘린 버퍼 반환
  }
}
