const noteFreq = [262, 278, 294, 311, 330, 349, 370, 392, 415, 440, 466, 494];

// ToneGenerator 클래스 정의
export class ToneGenerator {
  constructor(ctx) {
    this._ctx = ctx; // 오디오 컨텍스트 설정
    this._oscillator = this._ctx.createOscillator(); // 오실레이터 생성
    this._oscillator.type = "sine"; // 오실레이터 타입 설정 (사인파)
    this._oscillator.frequency.setValueAtTime(0, this._ctx.currentTime); // 주파수 초기화
    this._oscillator.start(); // 오실레이터 시작

    this._gain = this._ctx.createGain(); // 게인 노드 생성
    this._oscillator.connect(this._gain); // 오실레이터를 게인 노드에 연결

    this._gain.gain.value = 0.5; // 기본 볼륨 설정
    this._gain.connect(this._ctx.destination); // 게인 노드를 오디오 출력에 연결
  }

  // 볼륨 설정 메서드
  setVolume(v) {
    this._gain.gain.value = v; // 게인 노드의 게인 값을 설정
  }

  // 볼륨 조회 메서드
  getVolume() {
    return this._gain.gain.value; // 게인 노드의 게인 값을 반환
  }

  // 특정 주파수로 톤 재생하는 메서드
  playTone(freq) {
    this._oscillator.frequency.setValueAtTime(freq, this._ctx.currentTime); // 주파수 설정
  }

  // 특정 음표 및 옥타브로 톤 재생하는 메서드
  playNote(note, modifierKey = 0) {
    const freq = this._noteToFreq(note, modifierKey); // 음표를 주파수로 변환
    this.playTone(freq); // 주파수로 톤 재생
  }

  // 음표를 주파수로 변환하는 내부 메서드
  _noteToFreq(note, modifierKey) {
    if (!note) return 0; // 노트가 없으면 0 반환

    let n = note.note + modifierKey; // 노트 인덱스에 변조키를 더함
    let octav = note.octav; // 옥타브 설정

    // 음계가 음수인 경우 옥타브 조정
    while (n < 0) {
      n += 12;
      octav--;
    }

    // 음계가 12 이상인 경우 옥타브 조정
    while (n > 11) {
      n -= 12;
      octav++;
    }

    let result = noteFreq[n]; // 음계에 해당하는 주파수 가져오기

    // 옥타브가 4보다 작은 경우 주파수를 줄임
    if (octav < 4) {
      for (let i = octav; i < 4; i++) {
        result = (result / 2) | 0; // 주파수를 절반으로 나눔
      }
    } 
    // 옥타브가 4보다 큰 경우 주파수를 늘림
    else if (octav > 4) {
      for (let i = 4; i < octav; i++) {
        result *= 2; // 주파수를 두 배로 증가
      }
    }
    return result; // 변환된 주파수 반환
  }
}
