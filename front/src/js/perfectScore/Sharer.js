import { EventEmitter } from "./EventEmitter.js";
import { FileModel } from "./Model.js";
import { SongList } from "./SongList.js";

export class Sharer extends EventEmitter {
  constructor() {
    super(); // EventEmitter의 생성자 호출

    // SongList와 FileModel 인스턴스 생성
    this._list = new SongList();
    this.model = new FileModel();

    // Sharer의 래퍼 엘리먼트 생성
    this._wrapper = document.createElement("div");
    this._wrapper.classList.add("sharer");

    // 검색 입력 필드 생성
    this._searchInput = document.createElement("input");
    this._searchInput.type = "search";
    this._searchInput.placeholder = "검색어를 입력하세요";

    // 검색 입력 필드를 래퍼에 추가
    // 검색 기능은 주석 처리되어 있음
    // this._wrapper.appendChild(this._searchInput);

    // SongList의 랜더링 결과를 래퍼에 추가
    this._wrapper.appendChild(this._list.render());

    // SongList에서 발생하는 click 이벤트에 대한 핸들러를 등록
    // 수동으로 바인딩하여 this 컨텍스트를 유지
    this._list.on("click", this._listClick.bind(this));

    // FileModel에서 최신 항목을 가져와 SongList에 설정
    this._list.list = this.model.getLatest();
  }

  // Sharer를 랜더링하는 메서드
  render() {
    return this._wrapper;
  }

  // SongList에서 항목을 클릭했을 때 실행되는 콜백 메서드
  _listClick(target) {
    // song-select 이벤트를 발생시켜 선택한 항목을 전달
    this.emit("song-select", target);
  }
}
