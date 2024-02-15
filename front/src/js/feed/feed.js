import { ref, computed } from 'vue'
import app from "../config/preference.js";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
let pref = app;

export async function fetchFeedList(pageNo, pageSize, select) {
  let serverUrl = '';

  switch (select) {
    case 0:
      serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.fetchAll;
      break;
    case 1:
      serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.fetchOld;
      break;
    case 2:
      serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.fetchTop100;
      break;
    default:
      throw new Error('Invalid value for select parameter');
  }

  return await fetch(`${serverUrl}?page=${pageNo}&size=${pageSize}`, {
    method: 'GET',
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
  })
  .then(response => response.json())
  .then(result => {
      return result;
    });
}

export async function fetchFeed(feedId) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.fetchOne + feedId;

  return await fetch(serverUrl, {
    method: 'GET',
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
  })
  .then(response => response.json())
  .then(result => {
      return result;
    });
}


export async function getFeedsByUser(uuid) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.getByUser + uuid;
  return await fetch(serverUrl, {
      method: 'GET',
      headers: {
        "Authorization": getCookie("Authorization"),
        "refreshToken": getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
    })
    .then(response => response.json())
    .then(result => {
      // console.log('유저아이디별로 피드 가져오기',result.data)
      return result.data
    })
    .catch(err => console.log(err.message))
}


export async function fetchFeedDelete(feedId) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.delete + feedId;

  try {
    const response = await fetch(serverUrl, {
      method: 'POST',
      headers: {
        Authorization: getCookie("Authorization"),
        refreshToken: getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
    });

    if (response.ok) {
      const result = await response.json();
      console.log('피드 삭제 성공', result);
      return result;
    } else {
      // 실패한 경우
      const errorData = await response.json();
      console.error('피드 삭제 실패', errorData);
      throw new Error('피드 삭제에 실패했습니다.');
    }
  } catch (error) {
    console.error('피드 삭제 중 오류 발생', error);
    throw error;
  }
}

export async function fetchFeedUpdate(feedId,newContent,newStatus) {
  const serverUrl = pref.app.api.protocol + pref.app.api.host + pref.app.api.feed.update + feedId;

  const existingFeed = await fetchFeed(feedId);
  console.log("existingFeed : ",existingFeed);
  const data = {
    userUUID: existingFeed.userUUID,
    songId: existingFeed.songId,
    title: existingFeed.title,
    content: newContent || existingFeed.content,
    // content: newContent,
    thumbnailUrl: existingFeed.thumbnailUrl,
    videoUrl: existingFeed.videoUrl,
    videoLength: existingFeed.videoLength,
    status: newStatus !== undefined ? newStatus : existingFeed.status,
    // status:newStatus
    totalPoint: existingFeed.totalPoint,
    time: existingFeed.time
  }

  return await fetch(serverUrl, {
      method: 'POST',
      headers: {
        Authorization: getCookie("Authorization"),
        refreshToken: getCookie("refreshToken"),
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    })
    .then(response => response.json())
    .then(result => {
      console.log('피드수정 완료!',result)
      return result.data
    })
    .catch(error => {
      console.error('피드수정 실패:', error);
      throw error;
    });
}
