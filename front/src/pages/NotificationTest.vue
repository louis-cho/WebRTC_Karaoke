<template>
  <div>
  </div>
</template>

<script setup>
//친구일때 알림 생성 body 예시
const friendNotification = {
  toUser : userKey, //받는사람.
    info : "i10a705.p.ssafy.io/friend_list",//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "friend", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
}

//노래방일때 알림 생성 body 예시
const karaokeNotification = {
  toUser : userKey, //받는사람 uuid.
    info : `/karaoke${sessionId}`,//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "karaoke", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
}

//댓글일때 알림 생성 body 예시
const commentNotification = {
  toUser : userKey, //받는사람 uuid.
    info : `${feedId}`,//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "comment", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
}

//게시글일떄 알림 생성 body 예시
const feedNotification = {
    toUser : userKey, //받는사람 uuid.
    info : `${feedId}`,//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "like", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
}


// axios.post(serverUrl, JSON.stringify(myObject), {
//   headers: {
//     'Content-Type': 'application/json',
//   },
// })
//   .then(response => {
//     console.log('서버 응답:', response.data);
//   })
//   .catch(error => {
//     console.error('오류 발생:', error);
//   });
</script>

<style>

</style>
