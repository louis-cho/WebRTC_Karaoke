import { ScoreDrawer } from "./ScoreDrawer.js";
import { ToneDetector } from "./ToneDetector.js";
import { ToneGenerator } from "./ToneGenerator.js";
import { parseScore } from "./ScoreParser.js";
import { Sharer } from "./Sharer.js";
import { SongEditor } from "./SongEditor.js";
import { createElem } from "./DOMUtil.js";

const UPDATE_INTERVAL = 1000 / 60; // 애니메이션 업데이트 간격: 1/60 초

export class App {
  constructor(appContainer) {
    // 앱의 초기 상태 설정
    this.lastTime = 0; // 이전 프레임 시간
    this.elapsed = 0; // 경과 시간
    this.inited = false; // 초기화 여부
    this.key = 0; // 음악 키
    this.playMusic = true; // 음악 재생 상태
    // 각각의 앱 구성 요소를 생성
    this.sharer = new Sharer(); // 음악 공유기
    this.songEditor = new SongEditor(); // 음악 편집기
    this.drawer = new ScoreDrawer(); // 악보 그리기 객체

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
    // wrapper.appendChild(this.songEditor.render()); // 음악 편집기 추가
    // wrapper.appendChild(this.sharer.render()); // 음악 공유기 추가
    this.wrapper = wrapper; // 앱 래퍼 엘리먼트 설정
    this.bindEvents(); // 이벤트 핸들러 바인딩
  }

  // 앱 초기화
  async init() {
    this.audio = new AudioContext(); // 오디오 컨텍스트 생성
    this.detector = new ToneDetector(this.audio); // 톤 디텍터 생성
    this.player = new ToneGenerator(this.audio); // 톤 발생기 생성

    // 톤 디텍터 이벤트 핸들러 등록
    this.detector.on("note", this.onNote.bind(this)); // 노트 이벤트 핸들러 등록
    this.detector.on("inited", () => {
      this.inited = true; // 초기화 완료
      this.drawer.inited(); // 악보 그리기 초기화
    });
    await this.detector.start(); // 톤 디텍터 시작
    this.drawer.start([]); // 악보 그리기 시작
  }

  // 이벤트 핸들러 바인딩
  bindEvents() {
    // 음악 공유기 이벤트 핸들러 등록
    this.sharer.on("song-select", this.songSelected.bind(this));
    // 음악 편집기 이벤트 핸들러 등록
    this.songEditor.on("play", async () => {
      if (!this.songEditor.score) {
        window.alert("노래를 선택해주세요")
        return;
      } // 악보 렌더링이 안됐으면 무시
      console.log(this.songEditor.score)
      console.log(parseScore(this.songEditor.score))
      this.playSong(parseScore(this.songEditor.score)); // 음악 재생
    });
    this.songEditor.on("stop", this.stopSong.bind(this)); // 음악 정지 이벤트 핸들러 등록
    this.songEditor.on("key-up", this.keyUp.bind(this)); // 키 업 이벤트 핸들러 등록
    this.songEditor.on("key-down", this.keyDown.bind(this)); // 키 다운 이벤트 핸들러 등록
    // 음악 편집기 변경 이벤트 핸들러 등록
    this.songEditor.on("change", (prop, value) => {
      switch (prop) {
        case "melody":
          this.toggleSound(value); // 멜로디 토글
          break;
        case "volume":
          this.setVolume(value); // 볼륨 설정
          break;
      }
    });
  }

  // 음악 곡 선택 시
  songSelected(song) {
    console.log(song)
    this.songEditor.score = song.score; // 선택된 곡의 스코어 설정
  }


  // 악보 재생
  playSong() {
    console.log(parseScore(this.songEditor.score))
    this.drawer.start(parseScore(this.songEditor.score)); // 악보 그리기 시작
  }

  // 악보 정지
  stopSong() {
    this.drawer.start([]); // 악보 그리기 초기화
  }

  // 노트 이벤트 핸들러
  onNote(note) {
    this.drawer.pushNote(note); // 노트를 악보에 추가
  }

  // 애니메이션 루프
  loop(time) {
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
    requestAnimationFrame(this.loop.bind(this)); // 다음 프레임 요청
  }

  // 업데이트
  update(delta) {
    if (!this.inited) return; // 초기화되지 않았으면 무시

    this.detector.update(delta); // 디텍터 업데이트
    this.drawer.update(delta); // 악보 그리기 업데이트
    if (this.playMusic) {
      const note = this.drawer.getCurrentNote(); // 현재 노트 가져오기
      this.player.playNote(note, this.key); // 노트 재생
    }
  }

  // 렌더링
  render() {
    this.drawer.render(); // 악보 그리기 렌더링
  }

  // 볼륨 설정
  setVolume(v) {
    this.player.setVolume(v); // 플레이어 볼륨 설정
  }

  // 사운드 토글
  toggleSound(force) {
    if (force === undefined) {
      this.playMusic = !this.playMusic; // 상태 토글
    } else {
      this.playMusic = force; // 강제 설정
    }
    if (!this.playMusic) {
      this.player.playTone(0); // 사운드 재생
    }
  }

  // 키 업
  keyUp() {
    this.setKey(this.key + 1); // 키 증가
  }

  // 키 다운
  keyDown() {
    this.setKey(this.key - 1); // 키 감소
  }

  // 키 설정
  setKey(key) {
    this.key = key; // 키 설정
    this.songEditor.key = key; // 음악 편집기의 키 설정
    this.drawer.octav = this.key; // 악보 그리기의 옥타브 설정
  }
}
