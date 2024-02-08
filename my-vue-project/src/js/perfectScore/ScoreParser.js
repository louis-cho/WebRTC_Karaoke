// 음표 문자열에 대응하는 음계 문자열 배열
var noteStrings = [
  "C",
  "C#",
  "D",
  "D#",
  "E",
  "F",
  "F#",
  "G",
  "G#",
  "A",
  "A#",
  "B"
];

// 악보를 파싱하여 음표 정보로 반환하는 함수
export function parseScore(txt) {
  let octav = 4; // 기본 옥타브
  let ch; // 현재 문자
  let tempo = 120; // 기본 템포
  let timeDelta = (60 * 1000) / tempo; // 기본 시간 간격
  let defaultBit = 4; // 기본 박자
  let defaultLength = timeDelta; // 기본 길이
  let i = 0; // 현재 위치
  txt = txt.toUpperCase().replace(/\b/g, ""); // 대문자 변환 및 공백 제거
  let len = txt.length; // 문자열 길이

  let curTime = 0; // 현재 시간

  let tmp = ""; // 임시 문자열

  let lastNote = null; // 이전 음표

  const results = []; // 파싱된 음표들을 담을 배열

  // 다음 문자로 이동하는 함수
  function next() {
      i++;
      ch = txt[i];
  }

  // 음표를 파싱하는 함수
  function parseNote() {
      let note = {
          note: 0,
          octav: octav,
          length: defaultLength,
          start: curTime
      };

      let dots = [1]; // 점

      let legato = false; // 리가토 여부

      note.note = noteStrings.indexOf(ch); // 음표 인덱스 찾기
      next();

      while (i < len) {
          if (["+", "#", "-"].includes(ch)) {
              note.note += ch !== "-" ? 1 : -1; // 변화 음계
              if (note.note < 0) {
                  note.octav--;
                  note.note += 12;
              } else if (note.note > 11) {
                  note.octav++;
                  note.note -= 12;
              }
              next();
          } else if (/[0-9]/.test(ch)) {
              tmp = "";
              while (/[0-9]/.test(ch)) {
                  tmp += ch;
                  next();
              }
              if (tmp.length > 0) {
                  note.length = (timeDelta * 4) / parseInt(tmp, 10); // 길이 계산
              }
          } else if (ch === ".") {
              dots.push(dots[dots.length - 1] / 2); // 점 추가
              next();
          } else if (/['"]/.test(ch)) {
              next();
              tmp = "";
              while (!/['"]/.test(ch)) {
                  tmp += ch;
                  next();
              }
              note.lyric = tmp; // 가사 설정
              tmp = "";
              next();
          } else if (ch === "&") {
              legato = true; // 리가토 설정
              next();
          } else {
              break;
          }
      }
      let notelen = 0;
      dots.forEach(l => {
          notelen += l * note.length;
      });
      note.length = notelen; // 실제 길이 계산

      if (
          lastNote &&
          lastNote.note === note.note &&
          lastNote.octav === note.octav
      ) {
          lastNote.length += note.length; // 이전 음표와 합치기
          note = lastNote;
      } else {
          results.push(note);
      }
      lastNote = null;

      if (legato) {
          lastNote = note; // 리가토 설정
      }

      curTime += notelen; // 시간 업데이트
  }

  // 문자열 가사 파싱 함수
  function parseStringLyric() {
      tmp = "";
      next();
      while ("]" !== ch) {
          tmp += ch;
          next();
      }
      const len = tmp.length;
      if (len === 0) return;
      let cnt = 0;
      let start = 0;
      for (let i = results.length - 1; i >= 0; i--) {
          if (results[i].note !== -1) {
              cnt++;
          }
          if (cnt === len) {
              start = i;
              break;
          }
      }
      for (let i = start, cur = 0; cur < len && i < results.length; i++) {
          if (results[i].note === -1) continue;
          results[i].lyric = tmp[cur];
          cur++;
      }
      tmp = "";
  }

  // 템포 파싱 함수
  function parseTempo() {
      tmp = "";
      next();
      while (/[0-9]/.test(ch)) {
          tmp += ch;
          i++;
          ch = txt[i];
      }
      tempo = parseInt(tmp);
      timeDelta = (60 * 1000) / tempo;
      defaultLength = (timeDelta * 4) / defaultBit;
      tmp = "";
  }

  // 기본 길이 파싱 함수
  function parseDefaultLength() {
      i++;
      tmp = "";
      while (/[0-9]/.test(txt[i])) {
          tmp += txt[i];
          i++;
      }
      defaultBit = parseInt(tmp, 10);
      defaultLength = (timeDelta * 4) / defaultBit;
  }

  while (i < len) {
      ch = txt[i];
      switch (ch) {
          case "T":
              parseTempo();
              break;
          case "C":
          case "D":
          case "E":
          case "F":
          case "G":
          case "A":
          case "B":
          case "R":
              parseNote();
              break;
          case ">":
              octav = Math.min(8, octav + 1); // 옥타브 증가
              i++;
              break;
          case "<":
              octav = Math.max(0, octav - 1); // 옥타브 감소
              i++;
              break;
          case "O":
              i++;
              ch = txt[i];
              octav = parseInt(ch); // 옥타브 설정
              break;
          case "L":
              parseDefaultLength(); // 기본 길이 설정
              break;
          case "[":
              parseStringLyric(); // 문자열 가사 파싱
              break;
          default:
              i++;
              break;
      }
  }
  return results; // 파싱된 결과 반환
}
