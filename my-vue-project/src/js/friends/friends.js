import app from "../config/preference.js";

let pref = app;

/**
 * @param {Integer} userId
 * @param
 */
export async function fetchFriendList(userId) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.friends.get  + userId +  pref.app.api.friends.list;
  return await fetch(serverUrl, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })
  .then(response => response.json())
  .then(result => {
      console.log(result)
      return result;
    });
}

