import app from "../config/preference.js";

let pref = app;


export async function fetchFeedList(feedId, pageNo, pageSize) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.fetch  + feedId;

  return await fetch(serverUrl, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })
  .then(response => response.json())
  .then(result => {
      return result.data;
    });
}
