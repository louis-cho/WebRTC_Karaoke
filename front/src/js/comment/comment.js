import app from "../config/preference.js";
import useCookie from "@/js/cookie.js";
const { setCookie, getCookie, removeCookie } = useCookie();
let pref = app;

/**
 * 이전에 그려진 댓글 지우기
 */
export function clearComment() {
  const container = document.getElementById("comments-container");
  container.innerHTML = "";
}

/**
 * 주어진 페이지 번호 게시글을 10개 (서버 기본 설정값) 가져와 댓글 계층 구조를 설정한다.
 * @param {Integer} feedId 피드 아이디
 * @param {Integer} pageNo 페이지 번호 (0부터 시작)
 */
export async function fetchComment(feedId, pageNo) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.comment.fetch +
    feedId;

  const data = {
    startIndex: pageNo,
    pageSize: 10,
  };

  return await fetch(serverUrl, {
    method: "POST",
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => response.json())
    .then((result) => {
      return buildCommentTree(result);
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

/**
 * 주어진 댓글 데이터로부터 댓글 계층 구조를 생성한다.
 * @param {Array} comments
 * @returns
 */
function buildCommentTree(comments) {
  const commentMap = new Map();

  if(comments.data == null) {
    return null;
  }
  comments.data.forEach((elem) => {
    elem.children = [];
    commentMap.set(elem.comment.commentId, elem);
  });

  const tree = [];

  comments.data.forEach((elem) => {
    elem.level = 0; // Initialize level for top-level comments

    if (
      elem.comment.parentCommentId !== null &&
      elem.comment.parentCommentId >= 0
    ) {
      const parentComment = commentMap.get(elem.comment.parentCommentId);

      // Set the level of the current comment based on the parent's level
      elem.comment.level = parentComment.level + 1;

      // Add the current comment as a child of the parent comment
      parentComment.children.push(elem);
    } else {
      tree.push(elem);
    }
  });

  return tree;
}

/**
 * 댓글 계층 구조 데이터로부터 div를 렌더링한다
 * @param {Object} comments
 * @param {Integer} level
 */
export function renderComments(comments, level = 0) {
  const container = document.getElementById("comments-container");
  comments.forEach((comment) => {
    const commentElement = document.createElement("div");
    commentElement.innerHTML += "&nbsp;".repeat(level) + comment.content;
    container.appendChild(commentElement);
    renderComments(comment.children, level + 1);
  });
}

// const commentTree = buildCommentTree(commentsData);
// renderComments(commentTree);

export async function fetchCommentCount(feedId) {
  const serverUrl =
    pref.app.api.protocol +
    pref.app.api.host +
    pref.app.api.comment.count +
    feedId;

  return await fetch(serverUrl, {
    method: "GET",
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
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

// 댓글 추가
export async function addComment(comment) {
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.comment.add;

  return await fetch(serverUrl, {
    method: "POST",
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      uuid: getCookie("uuid"),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(comment),
  })
    .then((response) => response.json())
    .then((result) => {
      return result.data;
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

// 댓글 조회수 추가
export async function addCommentCount(comment) {
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.comment.add;

  return await fetch(serverUrl, {
    method: "GET",
    headers: {
      Authorization: getCookie("Authorization"),
      refreshToken: getCookie("refreshToken"),
      "Content-Type": "application/json",
    },
    body: JSON.stringify(comment),
  })
    .then((response) => response.json())
    .then((result) => {
      return result.data;
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}
