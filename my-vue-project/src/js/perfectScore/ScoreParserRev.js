export function generateScore(results) {
  let txt = '';

  // 템포 설정
  txt += `T${Math.round((60 * 1000) / timeDelta)}`;

  // 기본 길이 설정
  txt += `L${4 / ((timeDelta * 4) / defaultLength)}`;

  // 음표와 가사 설정
  results.forEach(note => {
      if (note.lylic) {
          txt += `[${note.lylic}]`;
      } else {
          txt += noteStrings[note.note];
          if (note.note !== -1) {
              let octaveDiff = note.octav - octav;
              if (octaveDiff > 0) {
                  txt += '>'.repeat(octaveDiff);
              } else if (octaveDiff < 0) {
                  txt += '<'.repeat(-octaveDiff);
              }
              let length = note.length / defaultLength;
              let dotted = false;
              while (length >= 1) {
                  txt += length === 1 ? '' : length;
                  length /= 2;
                  if (length === 0.5) {
                      txt += '.';
                      dotted = true;
                  }
              }
              if (!dotted && length === 0.5) {
                  txt += '.';
              }
              txt += note.lylic ? '"' : '';
          }
      }
  });

  return txt;
}

[
  { note: 2, octav: 3, length: 500, start: 0, lylic: '동' },
  { note: 7, octav: 3, length: 500, start: 500, lylic: '해' },
  { note: 6, octav: 3, length: 125, start: 1000, lylic: '물' },
  { note: 5, octav: 3, length: 500, start: 1125, lylic: '과' },
  { note: 7, octav: 3, length: 500, start: 1625, lylic: '백' },
  { note: 2, octav: 3, length: 500, start: 2125, lylic: '두' },
  { note: 1, octav: 3, length: 500, start: 2625, lylic: '산' },
  { note: 2, octav: 3, length: 500, start: 3125, lylic: '이' },
  { note: 7, octav: 3, length: 125, start: 3625, lylic: '마' },
  { note: 8, octav: 3, length: 125, start: 3750, lylic: '르' },
  { note: 9, octav: 3, length: 62.5, start: 3875, lylic: '고' },
  { note: 9, octav: 3, length: 125, start: 3937.5, lylic: '닳' },
  { note: 9, octav: 3, length: 62.5, start: 4062.5, lylic: '도' },
  { note: 8, octav: 3, length: 1000, start: 4125, lylic: '록' },
  { note: -1, length: 1000, start: 5125 },
  { note: 2, octav: 3, length: 250, start: 6125, lylic: '하' },
  { note: 1, octav: 3, length: 125, start: 6375, lylic: '느' },
  { note: 0, octav: 3, length: 250, start: 6500, lylic: '님' },
  { note: -1, length: 250, start: 6750 },
  { note: 2, octav: 3, length: 125, start: 7000, lylic: '보' },
  { note: 1, octav: 3, length: 125, start: 7125, lylic: '우' },
  { note: 0, octav: 3, length: 500, start: 7250, lylic: '하' },
  { note: 1, octav: 3, length: 125, start: 7750, lylic: '사' },
  { note: 2, octav: 3, length: 500, start: 7875, lylic: '우' },
  { note: 7, octav: 3, length: 500, start: 8375, lylic: '리' },
  { note: 8, octav: 3, length: 125, start: 8875, lylic: '나' },
  { note: 9, octav: 3, length: 125, start: 9000, lylic: '라' },
  { note: 10, octav: 3, length: 125, start: 9125, lylic: '만' },
  { note: 7, octav: 3, length: 1000, start: 9250, lylic: '세' },
  { note: -1, length: 1000, start: 10250 },
  { note: 6, octav: 3, length: 125, start: 11250, lylic: '무' },
  { note: 7, octav: 3, length: 500, start: 11375, lylic: '궁' },
  { note: 6, octav: 3, length: 125, start: 11875, lylic: '화' },
  { note: 11, octav: 3, length: 500, start: 12000, lylic: '삼' },
  { note: 7, octav: 3, length: 125, start: 12500, lylic: '천' },
  { note: 6, octav: 3, length: 125, start: 12625, lylic: '리' },
  { note: 11, octav: 3, length: 125, start: 12750, lylic: '화' },
  { note: 7, octav: 3, length: 125, start: 12875, lylic: '려' },
  { note: 6, octav: 3, length: 125, start: 13000, lylic: '강' },
  { note: 7, octav: 3, length: 500, start: 13125, lylic: '산' },
  { note: -1, length: 1000, start: 13625 },
  { note: 2, octav: 3, length: 250, start: 14625, lylic: '대' },
  { note: 1, octav: 3, length: 125, start: 14875, lylic: '한' },
  { note: 0, octav: 3, length: 250, start: 15000, lylic: '사' },
  { note: -1, length: 250, start: 15250 },
  { note: 2, octav: 3, length: 125, start: 15500, lylic: '람' },
  { note: 1, octav: 3, length: 125, start: 15625, lylic: '대' },
  { note: 0, octav: 3, length: 125, start: 15750, lylic: '한' },
  { note: 11, octav: 3, length: 125, start: 15875, lylic: '으' },
  { note: 10, octav: 3, length: 125, start: 16000, lylic: '로' },
  { note: -1, length: 125, start: 16125 },
  { note: 2, octav: 3, length: 125, start: 16250, lylic: '길' },
  { note: 1, octav: 3, length: 125, start: 16375, lylic: '이' },
  { note: 0, octav: 3, length: 500, start: 16500, lylic: '보' },
  { note: 1, octav: 3, length: 125, start: 17000, lylic: '전' },
  { note: 2, octav: 3, length: 125, start: 17125, lylic: '하' },
  { note: 7, octav: 3, length: 1000, start: 17250, lylic: '세' }
]
