export function encrypt(data, key) {
  const dataWA = enc.Utf8.parse(data);
  const keyWA = enc.Utf8.parse(key);
  const ivWA = enc.Utf8.parse(key.substring(0, 16));

  const cipher = AES.encrypt(dataWA, keyWA, { iv: ivWA });
  return cipher.ciphertext.toString(enc.Base64url); // url query 파라미터 대응하기 위해 일반 base64 가 아닌 base64url 로 변환
}

export function decrypt(encData, key) {
  const keyWA = enc.Utf8.parse(key);
  const ivWA = enc.Utf8.parse(key.substring(0, 16));

  const cipher = AES.decrypt(enc.Base64url.parse(encData.replace(/=/gi, '')).toString(enc.Base64), keyWA, { iv: ivWA }); // 해쉬된 값이 url encode 일 수도 있으므로 base64url 로 파싱하고 다시 base64 로 인코딩
  return cipher.toString(enc.Utf8);
}

export async function generateAESKey() {
  try {
      // SubtleCrypto를 사용하여 AES-GCM 키 생성
      const key = await window.crypto.subtle.generateKey(
          {
              name: 'AES-GCM',
              length: 256, // 키 길이 (128, 192, 256 중 선택)
          },
          true, // 키 사용 가능 여부 (암호화 및 복호화에 모두 사용 가능하도록)
          ['encrypt', 'decrypt'] // 사용할 암호화 및 복호화 알고리즘
      );

      return key;
  } catch (error) {
      console.error('AES 키 생성 중 오류 발생:', error);
  }
}
