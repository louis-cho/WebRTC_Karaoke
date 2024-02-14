import app from "@/js/config/preference.js";
import useCookie from "@/js/cookie.js";
let pref = app;
const { getCookie } = useCookie();

export async function fetchUser(uuid) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.user.fetch + uuid;

  return await fetch(serverUrl, {
    method: "POST",
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",

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
  // userPk
) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.user.update;

  // const existingUser = await fetchUser(userPk);
  // console.log(existingUser)
  const data = {
    uuid: uuid,
    nickname: nickname,
    profileImgUrl: profileImgUrl,
    introduction: introduction
  };

  return await fetch(serverUrl, {
    method: "POST",
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",

    },
    body: JSON.stringify(data),
  })
    .then(response => response.json())
    .then((result) => {
      console.log('-------1111111111111111111------------------')
      console.log('유저정보수정 완료!',result);
      return result;
    })
    .catch((error) => {
      console.error("개인정보수정 실패", error);
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
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",
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
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",

    },
  })
    .then((response) => response.json())
    .then((result) => {
      // console.log('로그인유저피케이가져오기',result)
      return result;
    });
}
