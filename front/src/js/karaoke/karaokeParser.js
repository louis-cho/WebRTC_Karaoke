// 가사 txt 관련 파싱 함수들 정의
// parseScore: mml to score / parseLyric: score to lyrics / parseBundle: lyrics to bundles


export function parseLyric(scoreData) {   // score to lyrics
  const lyrics = [];
  let lyricSet = [];

  for (const item of scoreData) {
    if (item.lyric == null) {
      continue;
    }

    if (item.lyric.includes('\t')) {
        // 탭이 포함된 경우 빈 문자열 추가
        lyricSet.push({ start: Math.floor(item.start), lyric: item.lyric.replace('\t', '') });
        lyricSet.push({ start: 0, lyric: " " });
    } else if (item.lyric.includes('\n')) {
        // 줄바꿈이 포함된 경우 lyricSet을 추가하고 새로운 lyricSet 시작
        lyricSet.push({ start: Math.floor(item.start), lyric: item.lyric.replace('\n', '') });
        lyrics.push(lyricSet);
        lyricSet = [];
    } else {
        // 일반적인 경우 lyricSet에 추가
        lyricSet.push({ start: Math.floor(item.start), lyric: item.lyric });
    }
  }

  // 마지막 lyricSet이 비어있지 않은 경우 추가
  if (lyricSet.length > 0) {
      lyrics.push(lyricSet);
  }

  return lyrics;
}

export function parseBundle(lyrics) { // lyrics to bundle
  const bundles = [];

    for (let i = 0; i < lyrics.length; i++) {
        let combinedLyric = { start: lyrics[i][0].start, lyric: '' };

        for (let j = 0; j < lyrics[i].length; j++) {
            combinedLyric.lyric += lyrics[i][j].lyric;
        }

        bundles.push(combinedLyric);
    }

    return bundles;
}

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
export function parseScore(txt) {   // mml to score
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

// sample datas
const bundles = [  // 가사 묶음 data
  { "start": 0, "lyric": "동해물과 백두산이" },
  { "start": 7058, "lyric": "마르고 닳도록" },
  { "start": 14116, "lyric": "하느님이 보우하사" },
  { "start": 21174, "lyric": "우리나라 만세" },
  { "start": 28232, "lyric": "무궁화 삼천리" },
  { "start": 35290, "lyric": "화려강산" },
  { "start": 42348, "lyric": "대한사람 대한으로" },
  { "start": 49406, "lyric": "길이 보전하세" }
]

const lyrics = [  // 개별 가사 data
  [
    { start: 0, lyric: '동' },
    { start: 882, lyric: '해' },
    { start: 2205, lyric: '물' },
    { start: 2647, lyric: '과' },
    { start: 0, lyric: ' ' },
    { start: 3529, lyric: '백' },
    { start: 4411, lyric: '두' },
    { start: 5294, lyric: '산' },
    { start: 6176, lyric: '이' },
  ],
  [
    { start: 7058, lyric: '마' },
    { start: 7941, lyric: '르' },
    { start: 8382, lyric: '고' },
    { start: 0, lyric: ' ' },
    { start: 8823, lyric: '닳' },
    { start: 10147, lyric: '도' },
    { start: 10588, lyric: '록' },
  ],
  [
    { start: 14117, lyric: '하' },
    { start: 15441, lyric: '느' },
    { start: 15882, lyric: '님' },
    { start: 16764, lyric: '이' },
    { start: 0, lyric: ' ' },
    { start: 17647, lyric: '보' },
    { start: 18529, lyric: '우' },
    { start: 19411, lyric: '하' },
    { start: 20294, lyric: '사' },
  ],
  [
    { start: 21176, lyric: '우' },
    { start: 22058, lyric: '리' },
    { start: 22941, lyric: '나' },
    { start: 23382, lyric: '라' },
    { start: 0, lyric: ' ' },
    { start: 23823, lyric: '만' },
    { start: 24705, lyric: '세' },
  ],
  [
    { start: 28235, lyric: '무' },
    { start: 30000, lyric: '궁' },
    { start: 30882, lyric: '화' },
    { start: 0, lyric: ' ' },
    { start: 31764, lyric: '삼' },
    { start: 33529, lyric: '천' },
    { start: 34411, lyric: '리' },
  ],
  [
    { start: 35294, lyric: '화' },
    { start: 36176, lyric: '려' },
    { start: 37058, lyric: '강' },
    { start: 38823, lyric: '산' },
  ],
  [
    { start: 42352, lyric: '대' },
    { start: 43676, lyric: '한' },
    { start: 44117, lyric: '사' },
    { start: 45000, lyric: '람' },
    { start: 0, lyric: ' ' },
    { start: 45882, lyric: '대' },
    { start: 46764, lyric: '한' },
    { start: 47647, lyric: '으' },
    { start: 48529, lyric: '로' },
  ],
  [
    { start: 49411, lyric: '길' },
    { start: 50294, lyric: '이' },
    { start: 0, lyric: ' ' },
    { start: 51176, lyric: '보' },
    { start: 51617, lyric: '전' },
    { start: 52058, lyric: '하' },
    { start: 52941, lyric: '세' }
  ],
]

const scoreData = [ // MML에서 ScoreParser.js로 최초로 parse된 data
{note: 2, octav: 3, length: 882.3529411764706, start: 0, lyric: '동'},
{note: 7, octav: 3, length: 1323.5294117647059, start: 882.3529411764706, lyric: '해'},
{note: 6, octav: 3, length: 441.1764705882353, start: 2205.8823529411766, lyric: '물'},
{note: 4, octav: 3, length: 882.3529411764706, start: 2647.0588235294117, lyric: '과\t'},
{note: 7, octav: 3, length: 882.3529411764706, start: 3529.4117647058824, lyric: '백'},
{note: 2, octav: 3, length: 882.3529411764706, start: 4411.764705882353, lyric: '두'},
{note: 11, octav: 2, length: 882.3529411764706, start: 5294.117647058823, lyric: '산'},
{note: 2, octav: 3, length: 882.3529411764706, start: 6176.470588235294, lyric: '이\n'},
{note: 7, octav: 3, length: 882.3529411764706, start: 7058.823529411764, lyric: '마'},
{note: 9, octav: 3, length: 441.1764705882353, start: 7941.176470588234, lyric: '르'},
{note: 11, octav: 3, length: 441.1764705882353, start: 8382.35294117647, lyric: '고\t'},
{note: 0, octav: 4, length: 1323.5294117647059, start: 8823.529411764706, lyric: '닳'},
{note: 11, octav: 3, length: 441.1764705882353, start: 10147.058823529413, lyric: '도'},
{note: 9, octav: 3, length: 2647.0588235294117, start: 10588.235294117649, lyric: '록\n'},
{note: -1, octav: 3, length: 882.3529411764706, start: 13235.29411764706},
{note: 2, octav: 4, length: 1323.5294117647059, start: 14117.64705882353, lyric: '하'},
{note: 0, octav: 4, length: 441.1764705882353, start: 15441.176470588236, lyric: '느'},
{note: 11, octav: 3, length: 882.3529411764706, start: 15882.352941176472, lyric: '님'},
{note: 9, octav: 3, length: 882.3529411764706, start: 16764.705882352944, lyric: '이\t'},
{note: 7, octav: 3, length: 882.3529411764706, start: 17647.058823529416, lyric: '보'},
{note: 6, octav: 3, length: 441.1764705882353, start: 18529.41176470589, lyric: '우'},
{note: 4, octav: 3, length: 441.1764705882353, start: 18970.588235294123},
{note: 2, octav: 3, length: 882.3529411764706, start: 19411.764705882357, lyric: '하'},
{note: 11, octav: 2, length: 882.3529411764706, start: 20294.11764705883, lyric: '사\n'},
{note: 2, octav: 3, length: 882.3529411764706, start: 21176.4705882353, lyric: '우'},
{note: 7, octav: 3, length: 882.3529411764706, start: 22058.823529411773, lyric: '리'},
{note: 9, octav: 3, length: 441.1764705882353, start: 22941.176470588245, lyric: '나'},
{note: 9, octav: 3, length: 441.1764705882353, start: 23382.35294117648, lyric: '라\t'},
{note: 11, octav: 3, length: 882.3529411764706, start: 23823.529411764714, lyric: '만'},
{note: 7, octav: 3, length: 2647.0588235294117, start: 24705.882352941186, lyric: '세\n'},
{note: -1, octav: 3, length: 882.3529411764706, start: 27352.9411764706},
{note: 6, octav: 3, length: 1323.5294117647059, start: 28235.29411764707, lyric: '무'},
{note: 7, octav: 3, length: 441.1764705882353, start: 29558.823529411777},
{note: 9, octav: 3, length: 882.3529411764706, start: 30000.00000000001, lyric: '궁'},
{note: 6, octav: 3, length: 882.3529411764706, start: 30882.352941176483, lyric: '화\t'},
{note: 11, octav: 3, length: 1323.5294117647059, start: 31764.705882352955, lyric: '삼'},
{note: 0, octav: 4, length: 441.1764705882353, start: 33088.23529411766},
{note: 2, octav: 4, length: 882.3529411764706, start: 33529.411764705896, lyric: '천'},
{note: 11, octav: 3, length: 882.3529411764706, start: 34411.764705882364, lyric: '리\n'},
{note: 9, octav: 3, length: 882.3529411764706, start: 35294.11764705883, lyric: '화'},
{note: 7, octav: 3, length: 882.3529411764706, start: 36176.4705882353, lyric: '려'},
{note: 6, octav: 3, length: 882.3529411764706, start: 37058.82352941177, lyric: '강'},
{note: 7, octav: 3, length: 882.3529411764706, start: 37941.17647058824},
{note: 9, octav: 3, length: 2647.0588235294117, start: 38823.529411764706, lyric: '산\n'},
{note: -1, octav: 3, length: 882.3529411764706, start: 41470.58823529412},
{note: 2, octav: 4, length: 1323.5294117647059, start: 42352.94117647059, lyric: '대'},
{note: 0, octav: 4, length: 441.1764705882353, start: 43676.470588235294, lyric: '한'},
{note: 11, octav: 3, length: 882.3529411764706, start: 44117.64705882353, lyric: '사'},
{note: 9, octav: 3, length: 882.3529411764706, start: 45000, lyric: '람\t'},
{note: 7, octav: 3, length: 882.3529411764706, start: 45882.35294117647, lyric: '대'},
{note: 6, octav: 3, length: 441.1764705882353, start: 46764.70588235294, lyric: '한'},
{note: 4, octav: 3, length: 441.1764705882353, start: 47205.882352941175},
{note: 2, octav: 3, length: 882.3529411764706, start: 47647.05882352941, lyric: '으'},
{note: 11, octav: 2, length: 882.3529411764706, start: 48529.41176470588, lyric: '로\n'},
{note: 2, octav: 3, length: 882.3529411764706, start: 49411.76470588235, lyric: '길'},
{note: 7, octav: 3, length: 882.3529411764706, start: 50294.11764705882, lyric: '이\t'},
{note: 9, octav: 3, length: 441.1764705882353, start: 51176.47058823529, lyric: '보'},
{note: 9, octav: 3, length: 441.1764705882353, start: 51617.647058823524, lyric: '전'},
{note: 11, octav: 3, length: 882.3529411764706, start: 52058.82352941176, lyric: '하'},
{note: 7, octav: 3, length: 2647.0588235294117, start: 52941.17647058823, lyric: '세'},
{note: -1, octav: 3, length: 882.3529411764706, start: 55588.23529411764},
]

const mml = `t68 o3 l4
  d'동'g.'해'f+8'물'e'과\t'g'백'd'두'c-'산'd'이\n' g'마'a8'르'b8'고'b+.'닳'b8'도' a2'록'.r
  >d.'하'c8'느'<b'님'a'이' g'보'f+8'우'e8d'하'c-'사' d'우'g'리'a8'나'a8'라'b'만' g2.'세'r
  f+.'무'g8a'궁'f+'화' b.'삼'>c8d'천'<b'리' a'화'g'려'f+'강'g a2.'산'r
  >d.'대'c8'한'<b'사'a'람' g'대'f+8'한'e8d'으'c-'로' d'길'g'이'a8'보'a8'전'b'하'g2.'세'r`;
