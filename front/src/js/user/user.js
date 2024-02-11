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
) {
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.user.update;

  const data = {
    userKey: userKey,
    nickname: nickname,
    profileImgUrlNode: profileImgUrl,
    introductionNode: introduction,
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
