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
  console.log("pageNo : ",pageNo)
  console.log("sizeNo : :",sizeNo)
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
    console.log('fetchfriendlist콘솔에 찍어봐야징',result)
    return result;
  })
  .catch((err) => {console.log("err : ",err)})
}


export async function fetchFriendRequestList( pageNo, sizeNo) {
  console.log("pageNo : ",pageNo)
  console.log("sizeNo : :",sizeNo)
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
    // console.log('친구카운트',result.totalElements)
    return result;
  })
  .catch((err) => {console.log("err : ",err)})

}

/**
 * @param {Integer} userId
 * @param {Integer} pageNo
 * @param {Integer} sizeNo
 */
export async function fetchFriendCount(pageNo,sizeNo) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.friends.get +
    pref.app.api.friends.list;

  return await fetch(`${serverUrl}?page=${pageNo}&size=${sizeNo}`, {
    method: "GET",
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",
    },
  })
    .then((response) => response.json())
    .then((result) => {
      console.log(result)
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
export async function fetchFriendRequest(fromUser, toUser) {
  const data = {
    fromUser: fromUser,
    toUser: toUser
  };
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.request ;
  return await fetch(serverUrl, {
    method: 'POST',
    headers: {
      "Authorization" : getCookie("Authorization"),
      "refreshToken" : getCookie("refreshToken"),
      "Content-Type" : "application/json",

    },
    body: JSON.stringify(data),
  })
  .then(response => response.json())
  .then(result => {
      console.log(result)
      return result;
    });
}
