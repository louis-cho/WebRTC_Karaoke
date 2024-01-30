import { EventEmitter } from "./EventEmitter.js"; // 이벤트 처리를 위한 EventEmitter import

export class SongList extends EventEmitter {
  constructor() {
    super(); // EventEmitter의 생성자 호출
    this._list = []; // 노래 목록을 저장하는 배열
    this._element = document.createElement("div"); // 노래 목록을 표시하는 요소 생성
    this._element.classList.add("song-list"); // CSS 클래스 추가
    this._element.addEventListener("click", this._clickHandler.bind(this)); // 클릭 이벤트 핸들러 등록
  }

  // 노래 목록 설정하는 setter 메서드
  set list(v) {
    this._list = v.slice(); // 전달된 배열을 복사하여 할당
    this._update(); // 노래 목록을 업데이트
  }

  // 클릭 이벤트 핸들러
  _clickHandler(e) {
    let t = e.target; // 클릭된 요소
    // 클릭된 요소의 부모 요소가 노래 항목인지 확인
    while (t && t.dataset["index"] === undefined) {
      t = t.parentElement;
    }
    // 클릭된 요소가 노래 항목이 아니면 종료
    if (!t) {
      return;
    }
    // 클릭된 노래 항목의 인덱스를 가져와서 해당 항목을 이벤트로 발생시킴
    const item = this._list[parseInt(t.dataset["index"], 10)];
    this.emit("click", item);
  }

  // SongList 요소를 렌더링하는 메서드
  render() {
    return this._element;
  }

  // 노래 목록을 화면에 업데이트하는 메서드
  _update() {
    this._element.innerHTML = ""; // 요소 내용 초기화
    // 각 노래 항목을 순회하면서 화면에 추가
    this._list.forEach((item, index) => {
      const el = document.createElement("div"); // 새로운 노래 항목 요소 생성
      el.dataset.index = index.toString(); // 인덱스 데이터 속성 추가
      // 노래 정보를 포함한 HTML 문자열을 생성하여 요소에 추가
      el.innerHTML = `<h2>${item.title} - ${item.singer}<small>(${item.author})</small></h2>`;
      this._element.appendChild(el); // 요소를 SongList에 추가
    });
  }
}
