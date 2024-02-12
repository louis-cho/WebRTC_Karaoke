import app from "../config/preference.js";

let pref = app;

/**
 * @param {Integer} userId
 * @param {Integer} pageNo
 * @param {Integer} sizeNo
 */
export async function fetchFriendList(userId, pageNo, sizeNo) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.get  + userId +  pref.app.api.friends.list;
  return await fetch(`${serverUrl}?page=${pageNo}&size=${sizeNo}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })
  .then(response => response.json())
  .then(result => {
      // console.log(result.content)
      return result.content;
    });
}

/**
 * @param {Integer} userId
 * @param {Integer} pageNo
 * @param {Integer} sizeNo
 */
export async function fetchFriendCount(userId,pageNo,sizeNo) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.friends.get + userId +
    pref.app.api.friends.list;

  return await fetch(`${serverUrl}?page=${pageNo}&size=${sizeNo}`, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((result) => {
      // console.log('친구카운트',result.totalElements)
      return result.totalElements;
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

// ------------------------------------------
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
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  })
  .then(response => response.json())
  .then(result => {
      console.log(result)
      return result;
    });
}
