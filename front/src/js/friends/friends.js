import app from "../config/preference.js";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
let pref = app;

/**
 * @param {Integer} userId
 * @param {Integer} pageNo
 * @param {Integer} sizeNo
 */
export async function fetchFriendList(pageNo, sizeNo) {

  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.get +  pref.app.api.friends.list;
  return await fetch(`${serverUrl}?page=${pageNo}&size=${sizeNo}`, {
    method: 'GET',
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",
    },
  })
  .then((response) => response.json())
  .then((result) => {
    return result;
  })
  .catch((err) => {console.log("err : ",err)})
}


export async function fetchFriendRequestList( pageNo, sizeNo) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.incomingRequest;
  return await fetch(`${serverUrl}?page=${pageNo}&size=${sizeNo}`, {
    method: 'GET',
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",

    },
  })
  .then((response) => response.json())
  .then((result) => {
    return result;
  })
  .catch((err) => {console.log("err : ",err)})

}

/**
 * @param {Integer} userId
 * @param {Integer} pageNo
 * @param {Integer} sizeNo
 */
export async function fetchFriendCount() {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.friends.get +
    pref.app.api.friends.count;

  return await fetch(`${serverUrl}`, {
    method: "GET",
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",
    },
  })
    .then((response) => response.json())
    .then((result) => {
      console.log('친구카운트!',result)
      return result;
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

// ------------------------------------------친구요청보내기
/**
 * @param {Integer} fromUser
 * @param {Integer} toUser
 */
export async function fetchFriendRequest(toUser) {

  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.request + toUser;
  return await fetch(serverUrl, {
    method: 'GET',
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",

    },
  })
  .then(response => console.log("친구 요청 생성완료"))
  .catch(err => console.log(err.message));
}
