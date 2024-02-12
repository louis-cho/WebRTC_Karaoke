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
      console.log(result.content)
      return result.content;
    });
}

export async function fetchFriendCount(feedId) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.comment.count +
    feedId;

  return await fetch(serverUrl, {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
    },
  })
    .then((response) => response.json())
    .then((result) => {
      return result.data;
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}


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
