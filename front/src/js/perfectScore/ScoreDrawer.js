const noteTop = [0, 0.5, 1, 1.5, 2, 3, 3.5, 4, 4.5, 5, 5.5, 6];

// ScoreDrawer 클래스 정의
export class ScoreDrawer {
  constructor() {
    // 초기화
    this._oct = 0; // 현재 옥타브
    this._elapsed = 0; // 경과 시간
    this._lastTime = 0; // 마지막 시간
    this._screenWidth = 1000; // 캔버스 너비
    this._screenTime = 600 * (1000 / 60); // 캔버스 시간
    this._playScore = []; // 재생되는 음표
    this._inited = false; // 초기화 여부
    this._currentNote = null; // 현재 음표
    this.lyricUpper = ""; // 위에 띄울 가사
    this.lyricLower = ""; // 아래 띄울 가사
    this.eval = "";
    this.lyricFlag = true;
    this.isLastLyric = false;

    // 캔버스 생성 및 설정
    this._canvas = document.createElement("canvas");
    this._canvas.width = this._screenWidth;
    this._canvas.height = 350;
    this._canvas.style.width = "100%";

    // 음표 배열 생성 및 리사이즈 콜백 등록
    this._notes = new Array(300).fill(-1);
    const resizeObserver = new ResizeObserver(this._resizeCallback.bind(this));
    resizeObserver.observe(this._canvas);
  }

  // 옥타브 getter/setter
  get octav() {
    return this._oct;
  }
  set octav(v) {
    this._oct = v;
  }

  // 캔버스 너비 getter/setter
  get screenWidth() {
    return this._screenWidth;
  }
  set screenWidth(v) {
    this._screenWidth = v;
    this._screenTime = this._screenWidth * (1000 / 60);
  }

  // 렌더링된 요소 반환
  renderElement() {
    return this._canvas;
  }

  // 캔버스 크기 조정 콜백
  _resizeCallback() {
    this._fitToContainer();
  }

  // 캔버스 크기에 맞게 조정
  _fitToContainer() {
    const { offsetWidth, offsetHeight } = this._canvas;
    this._canvas.width = offsetWidth;
    this._screenWidth = offsetWidth;
    const len = Math.floor(offsetWidth / 2);
    if (this._notes.length < len) {
      this._notes.unshift(...new Array(len - this._notes.length).fill(-1));
    } else if (this._notes.length > len) {
      this._notes.splice(0, this._notes.length - len);
    }
  }

  // 재생 시작
  start(notes) {
    this._playScore = notes.slice();
  }

  // 재생 중지
  stop() {
    this._playScore = [];
  }

  // 현재 시간 반환
  get currentTime() {
    return this._elapsed;
  }

  // 음표 렌더링
  _renderNotes(ctx) {
    ctx.save();
    const fps = 1000 / 60;
    const screenLength = this._screenWidth;
    const halfLength = screenLength / 2;
    ctx.translate(halfLength, 0);
    const screenTime = screenLength * fps;
    const half = screenTime / 2;
    const latency = 40 / fps;
    let current = null;

    this._playScore.forEach((note) => {
      if (note.note === -1) return;

      const x = (note.start - this._elapsed) / fps;
      if (x > halfLength) return;

      const width = note.length / fps - 1;
      if (x + width < -halfLength) return;

      const y =
        noteTop[note.note] * 5 +
        (note.octav - 3) * 35 +
        150 +
        this._oct * 5 -
        2.5;
      if (
        note.start <= this._elapsed &&
        note.start + note.length - fps >= this._elapsed
      ) {
        current = note;
        ctx.fillStyle = "orange";
      } else {
        ctx.fillStyle = "skyblue";
      }
      ctx.fillRect(x + latency, y, width, 5);
      if (note.lylic) {
        ctx.save();
        ctx.fillStyle = "black";
        ctx.translate(x + latency, y);
        ctx.scale(1, -1);
        ctx.fillText(note.lylic, 0, 5);
        ctx.restore();
      }
    });
    ctx.restore();
    this._currentNote = current;
  }

  // 현재 음표 반환
  getCurrentNote() {
    return this._currentNote;
  }

  // 음표 추가
  pushNote(note) {
    this._notes.push(note);
    this._notes.shift();
  }

  // 경과 시간 업데이트
  update(delta) {
    this._elapsed += delta;
  }

  // 캔버스 렌더링
  render() {
    const ctx = this._canvas.getContext("2d");
    ctx.save();
    ctx.font = "14px monospace";
    ctx.textBaseline = "top";
    ctx.fillStyle = "black"; // 배경
    ctx.fillRect(0, 0, this._canvas.width, this._canvas.height);
    ctx.scale(1, -1);
    ctx.translate(0, -300);

    ctx.globalAlpha = 0.5;
    ctx.fillStyle = "skyblue";
    this._renderNotes(ctx); // 음

    this._renderVoice(ctx);

    ctx.strokeStyle = "yellowgreen";
    ctx.beginPath();
    ctx.moveTo(this._screenWidth / 2, 0);
    ctx.lineTo(this._screenWidth / 2, 400);
    ctx.stroke();

    ctx.restore();

    ctx.font = "20px YCloverBold";
    // 가사 렌더링
    // 시작~전주
    if (this._elapsed < 0) {
      ctx.fillStyle = "white";
      ctx.fillText(
        this.lyricUpper,
        this._screenWidth / 2 - 60,
        this._canvas.height - 50
      );
      ctx.fillText(
        this.lyricLower,
        this._screenWidth / 2 - 60,
        this._canvas.height - 20
      );
    } else if (this._elapsed >= 0) {
      if (!this.isLastLyric) {
        if (this.lyricFlag) {
          ctx.fillStyle = "yellow";
          ctx.fillText(
            this.lyricUpper,
            this._screenWidth / 2 - 60,
            this._canvas.height - 50
          );
          ctx.fillStyle = "white";
          ctx.fillText(
            this.lyricLower,
            this._screenWidth / 2 - 60,
            this._canvas.height - 20
          );
        } else {
          ctx.fillStyle = "white";
          ctx.fillText(
            this.lyricUpper,
            this._screenWidth / 2 - 60,
            this._canvas.height - 50
          );
          ctx.fillStyle = "yellow";
          ctx.fillText(
            this.lyricLower,
            this._screenWidth / 2 - 60,
            this._canvas.height - 20
          );
        }
      } else {
        ctx.fillStyle = "yellow";
        ctx.fillText(
          this.lyricUpper,
          this._screenWidth / 2 - 60,
          this._canvas.height - 50
        );
        ctx.fillText(
          this.lyricLower,
          this._screenWidth / 2 - 60,
          this._canvas.height - 20
        );
      }
    }

    // score 렌더링
    if (this.eval === "PERFECT!") {
      ctx.fillStyle = "cyan";
    } else if (this.eval === "GOOD!") {
      ctx.fillStyle = "lime";
    } else {
      ctx.fillStyle = "Tomato";
    }
    ctx.font = "20px Arial Rounded MT Bold";
    ctx.fillText(this.eval, this._screenWidth / 2 - 50, 50);
  }

  // 음성 렌더링
  _renderVoice(ctx) {
    ctx.fillStyle = "red";
    this._notes.forEach((note, x) => {
      if (note !== -1) {
        const octav = Math.floor(note / 12) - 4;
        const n = note % 12;
        ctx.fillRect(x, noteTop[n] * 5 + 150 + octav * 35 - 2.5, 1, 5);
      }
    });
    const latestNote = this._notes[this._notes.length - 1];
    const octav = Math.floor(latestNote / 12) - 4;
    const n = latestNote % 12;
    if (this._currentNote != null && latestNote !== -1) {
      // console.log("현재: "+ (noteTop[this._currentNote.note] * 5 +
      // (this._currentNote.octav - 3) * 35 +
      // 150 +
      // this._oct * 5 -
      // 2.5))
      // console.log("입력: " + (noteTop[n] * 5 + 150 + octav * 35 - 2.5))

      const voice =
        noteTop[this._currentNote.note] * 5 +
        (this._currentNote.octav - 3) * 35 +
        150 +
        this._oct * 5 -
        2.5;
      const score = noteTop[n] * 5 + 150 + octav * 35 - 2.5;
      const abs = Math.abs(voice - score);

      if (abs <= 2) {
        this.eval = "PERFECT!";
      } else if (abs <= 6) {
        this.eval = "GOOD!";
      } else {
        this.eval = "BAD";
      }
    } else {
      this.eval = "";
    }
  }

  // 초기화 완료
  inited() {
    this._inited = true;
  }
}
