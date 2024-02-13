import app from "../config/preference.js";

let pref = app;

export async function fetchUser(userPk) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.user.fetch +
    userPk;

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
  userKey,
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
    userKey: userKey, // UUID 형태의 userKey 전달
    nickname: nickname,
    profileImgUrl: profileImgUrl,
    introduction: introduction
  };

  return await fetch(serverUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
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
