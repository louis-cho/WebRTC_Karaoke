// 가상의 댓글 데이터 (프론트엔드에서 받아온 데이터)
const commentsData = [
  { commentId: 1, content: "댓글 1", parentCommentId: null },
  { commentId: 2, content: "댓글 2", parentCommentId: null },
  { commentId: 3, content: "대댓글 1-1", parentCommentId: 1 },
  { commentId: 4, content: "대댓글 1-2", parentCommentId: 1 },
  { commentId: 5, content: "댓글 3", parentCommentId: 3 },
  // ... (더 많은 댓글 데이터)
];

// 댓글을 계층 구조로 변환하는 함수
function buildCommentTree(comments) {
  const commentMap = new Map();

  comments.forEach(comment => {
      comment.children = [];
      commentMap.set(comment.commentId, comment);
  });

  const tree = [];

  comments.forEach(comment => {
      if (comment.parentCommentId !== null) {
          commentMap.get(comment.parentCommentId).children.push(comment);
      } else {
          tree.push(comment);
      }
  });

  return tree;
}

// 댓글을 출력하는 재귀적인 함수
function renderComments(comments, level = 0) {
  comments.forEach(comment => {
      console.log("  ".repeat(level) + comment.content);
      renderComments(comment.children, level + 1);
  });
}

// 댓글 데이터를 계층 구조로 변환
const commentTree = buildCommentTree(commentsData);

// 계층 구조로 변환된 댓글을 출력
renderComments(commentTree);
