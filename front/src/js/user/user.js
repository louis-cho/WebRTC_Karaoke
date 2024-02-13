import useCookie from "../cookie.js";
import app from "../config/preference.js";

let pref = app;

const { setCookie, getCookie, removeCookie } = useCookie();

export async function fetchUser(uuid) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.user.fetch + uuid;

  return await fetch(serverUrl, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((result) => {
      return result;
    });
}

// ----------------
export async function updateUser(
  uuid,
  nickname,
  profileImgUrl,
  introduction
) {
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.user.update;

  const data = {
    uuid: uuid,
    nickname: nickname,
    profileImgUrl: profileImgUrl,
    introduction: introduction,
  };

  return await fetch(serverUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => response.json())
    .then((result) => {
      console.log(result);
      return result;
    })
    .catch((error) => {
      console.error("개인정보수정", error);
    });
}
// -----------------------------------

export async function searchUser(nickname) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.user.search +
    nickname;

  return await fetch(serverUrl, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((result) => {
      return result;
    });
}

export async function getUserPk(uuid) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.user.getUserPK;

  return await fetch(`${serverUrl}?uuid=${uuid}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((result) => {
      // console.log('로그인유저피케이가져오기',result)
      return result;
    });
}
