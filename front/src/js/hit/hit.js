import app from "../config/preference.js";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
let pref = app;

/**
 * 주어진 페이지 번호 게시글을 10개 (서버 기본 설정값) 가져와 댓글 계층 구조를 설정한다.
 * @param {Integer} feedId 피드 아이디
 */
export async function fetchHitCount(feedId) {
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.hit.count + feedId;

  return await fetch(serverUrl, {
    method: "POST",
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((result) => {
      if (!isNaN(result.data)) return result.data;
      return 0;
    });
}

export async function createHit(feedId) {
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.hit.create;

  const data = {
    feedId: String(feedId),
  };

  return await fetch(serverUrl, {
    method: "POST",
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      uuid: getCookie("uuid"),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  });
}
