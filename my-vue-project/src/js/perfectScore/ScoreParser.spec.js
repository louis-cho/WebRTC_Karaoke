import { parseScore } from "./ScoreParser";

const noteStrings = [
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

describe("ScoreParser", () => {
  let _notestart = 0; // 음표 시작 시간을 저장하는 변수

  // 문자열에 해당하는 음표의 인덱스를 반환하는 함수
  function getNote(ch) {
    return noteStrings.indexOf(ch.toUpperCase());
  }

  // 음표 객체를 생성하는 함수
  function makeNote(data) {
    const note = {
      note: data.note ?? 0,
      length: data.length ?? 0,
      octav: data.octav ?? 4,
      start: _notestart
    };
    if (data.lylic) {
      note.lylic = data.lylic;
    }
    _notestart += data.length ?? 0; // 다음 음표의 시작 시간 업데이트
    return note;
  }

  // 음표 시작 시간을 초기화하는 함수
  function resetNote() {
    _notestart = 0;
  }

  beforeEach(() => {
    resetNote(); // 각 테스트 전에 음표 시작 시간 초기화
  });

  // 빈 문자열이 입력됐을 때 빈 배열이 반환되는지 확인하는 테스트
  it("empty string returns empty array", () => {
    const input = "";

    const result = parseScore(input);

    expect(result).toEqual([]);
  });

  // 기본값이 제대로 설정되었는지 확인하는 테스트
  it("check default values", () => {
    const input = "a";

    const result = parseScore(input);

    expect(result).toEqual([{ note: 9, length: 500, octav: 4, start: 0 }]);
  });

  // 템포 변경이 제대로 이루어졌는지 확인하는 테스트
  it("change tempo", () => {
    const input = "t60 a";

    const result = parseScore(input);

    expect(result).toEqual([{ note: 9, length: 1000, octav: 4, start: 0 }]);
  });

  // 옥타브 변경이 제대로 이루어졌는지 확인하는 테스트
  it("change octav", () => {
    const input = "o3 a o4 a o0 a >a <a";

    const result = parseScore(input);

    expect(result).toEqual([
      { note: 9, length: 500, octav: 3, start: 0 },
      { note: 9, length: 500, octav: 4, start: 500 },
      { note: 9, length: 500, octav: 0, start: 1000 },
      { note: 9, length: 500, octav: 1, start: 1500 },
      { note: 9, length: 500, octav: 0, start: 2000 }
    ]);
  });

  // 기본 길이 변경이 제대로 이루어졌는지 확인하는 테스트
  it("change length", () => {
    const input = "l8 a l4 a l16 a";

    const result = parseScore(input);

    expect(result).toEqual([
      { note: 9, length: 250, octav: 4, start: 0 },
      { note: 9, length: 500, octav: 4, start: 250 },
      { note: 9, length: 125, octav: 4, start: 750 }
    ]);
  });

  // 점이 제대로 처리되는지 확인하는 테스트
  it("dots", () => {
    const input = "a. a.. a... a8. a16.";

    const result = parseScore(input);

    expect(result).toEqual([
      { note: 9, length: 500 + 250, octav: 4, start: 0 },
      { note: 9, length: 500 + 250 + 125, octav: 4, start: 750 },
      { note: 9, length: 500 + 250 + 125 + 62.5, octav: 4, start: 750 + 875 },
      { note: 9, length: 250 + 125, octav: 4, start: 750 + 875 + 937.5 },
      { note: 9, length: 125 + 62.5, octav: 4, start: 750 + 875 + 937.5 + 375 }
    ]);
  });

  // 샵(#) 기호 처리가 제대로 이루어졌는지 확인하는 테스트
  it("plus", () => {
    const input = "a+ a++ a+++";

    const result = parseScore(input);

    expect(result).toEqual([
      { note: 10, length: 500, octav: 4, start: 0 },
      { note: 11, length: 500, octav: 4, start: 500 },
      { note: 0, length: 500, octav: 5, start: 1000 }
    ]);
  });

  // 마이너(-) 기호 처리가 제대로 이루어졌는지 확인하는 테스트
  it("minus", () => {
    const input = "d- d-- d---";

    const result = parseScore(input);

    expect(result).toEqual([
      { note: 1, length: 500, octav: 4, start: 0 },
      { note: 0, length: 500, octav: 4, start: 500 },
      { note: 11, length: 500, octav: 3, start: 1000 }
    ]);
  });

  // 숫자 길이 처리가 제대로 이루어졌는지 확인하는 테스트
  it("number length", () => {
    const input = "c1 c2 c3 c4 c8 c16 c32 c64";

    const result = parseScore(input);

    expect(result).toEqual([
      makeNote({ length: 2000 }),
      makeNote({ length: 1000 }),
      makeNote({ length: 2000 / 3 }),
      makeNote({ length: 500 }),
      makeNote({ length: 250 }),
      makeNote({ length: 125 }),
      makeNote({ length: 62.5 }),
      makeNote({ length: 31.25 })
    ]);
  });

  // 잘못된 문자가 무시되는지 확인하는 테스트
  it("ignore illegal characters", () => {
    const input = "hi? c안녕하세요";

    const result = parseScore(input);

    expect(result).toEqual([{ note: 0, length: 500, octav: 4, start: 0 }]);
  });

  // 레가토(legato)가 제대로 처리되는지 확인하는 테스트
  it("legato makes two same note to one", () => {
    const input = "c&c8";

    const result = parseScore(input);

    expect(result).toEqual([{ note: 0, length: 750, octav: 4, start: 0 }]);
  });

  // 레가토가 서로 다른 음표에는 적용되지 않는지 확인하는 테스트
  it("legato does not make two different note to one", () => {
    const input = "c&d8";

    const result = parseScore(input);

    expect(result).toEqual([
      { note: 0, length: 500, octav: 4, start: 0 },
      { note: 2, length: 250, octav: 4, start: 500 }
    ]);
  });

  // 가사가 제대로 처리되는지 확인하는 테스트
  it("lylic string", () => {
    const input = "abc&cd[가나다라]";

    const result = parseScore(input);

    expect(result).toEqual([
      makeNote({ note: getNote("a"), lylic: "가", length: 500 }),
      makeNote({ note: getNote("b"), lylic: "나", length: 500 }),
      makeNote({ note: getNote("c"), lylic: "다", length: 1000 }),
      makeNote({ note: getNote("d"), lylic: "라", length: 500 })
    ]);
  });

  // 음표보다 긴 가사는 잘린다는 것을 확인하는 테스트
  it("cut off lylics longer than notes", () => {
    const input = "abc&cd[가나다라마바사]";

    const result = parseScore(input);

    expect(result).toEqual([
      makeNote({ note: getNote("a"), lylic: "가", length: 500 }),
      makeNote({ note: getNote("b"), lylic: "나", length: 500 }),
      makeNote({ note: getNote("c"), lylic: "다", length: 1000 }),
      makeNote({ note: getNote("d"), lylic: "라", length: 500 })
    ]);
  });

  // 올바른 음표에만 가사가 적용되는지 확인하는 테스트
  it("only correct notes takes the lylics", () => {
    const input = "abc&cd[가나]";

    const result = parseScore(input);

    expect(result).toEqual([
      makeNote({ note: getNote("a"), length: 500 }),
      makeNote({ note: getNote("b"), length: 500 }),
      makeNote({ note: getNote("c"), lylic: "가", length: 1000 }),
      makeNote({ note: getNote("d"), lylic: "나", length: 500 })
    ]);
  });

  // 가사에 빈 음표는 제외되는지 확인하는 테스트
  it("skip slient note for lylics string", () => {
    const input = ">c4<ef&f4rf[이대로널]";

    const result = parseScore(input);

    expect(result).toEqual([
      { note: 0, octav: 5, length: 500, start: 0, lylic: "이" },
      { note: 4, octav: 4, length: 500, start: 500, lylic: "대" },
      { note: 5, octav: 4, length: 1000, start: 1000, lylic: "로" },
      { note: -1, octav: 4, length: 500, start: 2000 },
      { note: 5, octav: 4, length: 500, start: 2500, lylic: "널" }
    ]);
  });
});
