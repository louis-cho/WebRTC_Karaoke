import { ScoreDrawer } from "./ScoreDrawer.js";
import { ToneDetector } from "./ToneDetector.js";
import { createElem } from "./DOMUtil.js";

const UPDATE_INTERVAL = 1000 / 60; // 애니메이션 업데이트 간격: 1/60 초

export class App {
  constructor(appContainer) {
    // 앱의 초기 상태 설정
    this.lastTime = 0; // 이전 프레임 시간
    this.elapsed = 0; // 경과 시간
    this.inited = false; // 초기화 여부
    this.key = 0; // 음악 키
    this.playMusic = false; // 음악 재생 상태
    this.hasNextLyric = false;
    // 각각의 앱 구성 요소를 생성
    this.drawer = new ScoreDrawer(); // 악보 그리기 객체
    this.score = "";
    this.lyrics = []; // 렌더링 할 가사
    this.lyricIndex = 0;
    this.startTimeRef = 0;  // 노래 시작 시간(가사 렌더링에 필요)
    this.lyricFlag = true;   // 윗가사를 update할지, 아랫가사를 update할지.
    this.songLength = 0;
    this.prelude = 0;

    // 앱 UI 생성
    this.createElements();
    appContainer.appendChild(this.wrapper); // 앱 컨테이너에 UI 추가
    this.init();
    // 애니메이션 시작
    requestAnimationFrame(this.loop.bind(this)); // loop 메서드에 this 바인딩
  }

  // 앱의 UI 엘리먼트 생성
  createElements() {
    const wrapper = createElem("div", {}); // 앱 전체를 감싸는 래퍼 엘리먼트
    const canvasContainer = createElem("div", {}); // 악보를 담을 컨테이너
    const canvas = this.drawer.renderElement(); // 악보를 렌더링하는 캔버스
    canvasContainer.appendChild(canvas); // 캔버스를 컨테이너에 추가
    this.drawer.start([]); // 악보 그리기 시작

    // UI 요소를 래퍼에 추가
    wrapper.appendChild(canvasContainer); // 악보 컨테이너 추가
    this.wrapper = wrapper; // 앱 래퍼 엘리먼트 설정
  }

  // 앱 초기화
  async init() {
    this.audio = new AudioContext(); // 오디오 컨텍스트 생성
    this.detector = new ToneDetector(this.audio); // 톤 디텍터 생성

    // 톤 디텍터 이벤트 핸들러 등록
    this.detector.on("note", this.onNote.bind(this)); // 노트 이벤트 핸들러 등록
    this.detector.on("inited", () => {
      this.inited = true; // 초기화 완료
      this.drawer.inited(); // 악보 그리기 초기화
    });
    await this.detector.start(); // 톤 디텍터 시작
    this.drawer.start([]); // 악보 그리기 시작
  }

  // 악보 재생
  playSong() {
    this.startTimeRef = Date.now()
    this.playMusic = true;
    this.hasNextLyric = true;
    this.lyricIndex = 2;
    this.lyricFlag = true;
    this.drawer.lyricUpper = this.lyrics[0].lyric;
    this.drawer.lyricLower = this.lyrics[1].lyric;
    this.drawer._elapsed = -1 * this.prelude;
    this.drawer.start(this.score); // 악보 그리기 시작
  }

  // 악보 정지
  stopSong() {
    this.drawer.start([]); // 악보 그리기 초기화
    this.drawer.lyricUpper = "";
    this.drawer.lyricLower = "";
    this.drawer.isLastLyric = false;
    this.drawer.lyricFlag = true;
    this.playMusic = false;
    this.lyricIndex = 1;
  }

  // 노트 이벤트 핸들러
  onNote(note) {
    this.drawer.pushNote(note); // 노트를 악보에 추가
  }

  // 애니메이션 루프
  loop(time) {
    if(this.hasNextLyric) {
      if(this.lyricIndex != 1) {
        if((Date.now() - this.startTimeRef) >= this.lyrics[this.lyricIndex-1].start+this.prelude) {
          if(this.lyricFlag) {  // 윗가사 업데이트
            this.drawer.lyricUpper = this.lyrics[this.lyricIndex].lyric;
            this.lyricFlag = !this.lyricFlag
            this.drawer.lyricFlag = !this.drawer.lyricFlag
            this.lyricIndex++;
          } else {  // 아랫가사 업데이트
            this.drawer.lyricLower = this.lyrics[this.lyricIndex].lyric;
            this.lyricFlag = !this.lyricFlag
            this.drawer.lyricFlag = !this.drawer.lyricFlag
            this.lyricIndex++;
          }
          if(this.lyricIndex >= this.lyrics.length) {
            this.drawer.isLastLyric = true;
            this.hasNextLyric = false;
          }
        }
      }
    }

    if (this.lastTime === 0) {
      this.lastTime = time; // 이전 프레임 시간 설정
    }
    const delta = time - this.lastTime; // 경과 시간 계산
    this.elapsed += delta; // 경과 시간 추가
    this.lastTime = time; // 이전 프레임 시간 갱신

    while (this.elapsed > UPDATE_INTERVAL) {
      this.update(UPDATE_INTERVAL); // 업데이트 수행
      this.elapsed -= UPDATE_INTERVAL; // 경과 시간 감소
    }

    this.render(); // 렌더링
    if((Date.now() - this.startTimeRef) >= (this.songLength*1000)) {
      this.stopSong();
    }
    requestAnimationFrame(this.loop.bind(this)); // 다음 프레임 요청
  }

  // 업데이트
  update(delta) {
    if (!this.inited) return; // 초기화되지 않았으면 무시

    this.detector.update(delta); // 디텍터 업데이트
    this.drawer.update(delta); // 악보 그리기 업데이트
  }

  // 렌더링
  render() {
    this.drawer.render(); // 악보 그리기 렌더링
  }
}
