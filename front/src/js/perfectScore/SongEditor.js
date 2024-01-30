import { createElem } from "./DOMUtil.js"; // DOM 조작을 위한 유틸리티 함수
import { EventEmitter } from "./EventEmitter.js"; // 이벤트 관리를 위한 EventEmitter 클래스

export class SongEditor extends EventEmitter {
  constructor() {
    super(); // EventEmitter의 생성자 호출
    this.initElements(); // SongEditor 요소 초기화 메서드 호출
  }

  // SongEditor의 요소들을 초기화하는 메서드
  initElements() {
    // 버튼 요소들 생성
    this.btnPlay = cBtn("Play");
    this.btnStop = cBtn("Stop");
    this.btnKeyUp = cBtn("Key Up");
    this.btnKeyDown = cBtn("Key Down");

    // 입력 필드 요소 생성
    this.inKey = createElem("input", { type: "number", value: "0" });

    // 체크박스 요소 생성
    this.chkMelody = createElem("input", { type: "checkbox", checked: true });
    const chkLabel = createElem("label", {}, "play melody"); // 체크박스 라벨 요소 생성
    chkLabel.appendChild(this.chkMelody); // 체크박스 라벨에 체크박스 요소 추가

    // 볼륨 조절 input range 요소 생성
    this.inVolume = createElem("input", {
      type: "range",
      min: 0,
      max: 100,
      value: 30,
      step: 1
    });

    // 악보 입력 textarea 요소 생성
    // this.inScore = createElem("textarea", { class: "inScore" });

    // SongEditor의 최상위 요소인 div 요소 생성
    this.element = createElem("div", { class: "song-editor" }, [
      chkLabel, // 체크박스 라벨 추가
      this.inVolume, // 볼륨 조절 input range 추가
      this.btnKeyDown, // Key Down 버튼 추가
      this.btnKeyUp, // Key Up 버튼 추가
      // this.inScore, // 악보 입력 textarea 추가
      this.btnPlay, // Play 버튼 추가
      this.btnStop // Stop 버튼 추가
    ]);

    // 각 요소의 이벤트 리스너 등록
    this.btnPlay.addEventListener("click", this._clickHandler.bind(this, "play"));
    this.btnStop.addEventListener("click", this._clickHandler.bind(this, "stop"));
    this.btnKeyDown.addEventListener("click", this._clickHandler.bind(this, "key-down"));
    this.btnKeyUp.addEventListener("click", this._clickHandler.bind(this, "key-up"));

    // 체크박스와 볼륨 조절 input range의 변경 이벤트 처리
    this.chkMelody.addEventListener("input", () => {
      this.emit("change", "melody", this.chkMelody.checked);
    });
    this.inVolume.addEventListener("input", () => {
      this.emit("change", "volume", parseInt(this.inVolume.value, 10) / 100);
    });
  }

  // key 속성에 대한 getter와 setter 정의
  set key(v) {
    this.inKey.value = v.toString();
  }

  get key() {
    return parseInt(this.inKey.value, 10);
  }

  // 버튼 클릭 이벤트 핸들러
  _clickHandler(type) {
    this.emit(type); // 이벤트 발생
  }

  // SongEditor 요소를 랜더링하는 메서드
  render() {
    return this.element;
  }

  // score 속성에 대한 getter와 setter 정의
  // get score() {
    // return this.inScore.value;
  // }

  // set score(v) {
  //   this.inScore.value = v;
  // }
}

// 버튼을 생성하는 함수
function cBtn(text) {
  return createElem("button", {}, text);
}
