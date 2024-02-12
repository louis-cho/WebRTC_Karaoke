import { ref, computed } from 'vue'
import app from "../config/preference.js";

let pref = app;


export async function fetchFeedList(pageNo, pageSize) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.fetchAll;

  return await fetch(`${serverUrl}?page=${pageNo}&size=${pageSize}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })
  .then(response => response.json())
  .then(result => {
      return result.content;
    });
}

export async function fetchFeed(feedId) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.fetchOne + feedId;

  return await fetch(serverUrl, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
    },
  })
  .then(response => response.json())
  .then(result => {
      return result;
    });
}


export async function getFeedsByUser(userPk) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.getByUser+ userPk;

  return await fetch(serverUrl, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/json',
      },
    })
    .then(response => response.json())
    .then(result => {
      console.log('유저아이디별로 피드 가져오기',result.data)
      return result.data
    });
}
