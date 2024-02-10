<template>
  <div>
  </div>
</template>

<script setup>
//친구일때 알림 생성 body 예시
const friendNotification = {
    userKey : userKey, //받는사람.
    info : "",//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "friend", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
}

//노래방일때 알림 생성 body 예시
const karaokeNotification = {
    userKey : userKey, //받는사람 uuid.
    info : `i10a705.p.ssafy.io/karaoke${sessionId}`,//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "karaoke", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
}

//댓글일때 알림 생성 body 예시
const commentNotification = {
    userKey : userKey, //받는사람 uuid.
    info : `${feedId}`,//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "comment", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
}

//게시글일떄 알림 생성 body 예시
const feedNotification = {
    userKey : userKey, //받는사람 uuid.
    info : `${feedId}`,//친구요청이면 빈 문자열, 좋아요, 댓글이면 게시글 아이디, 노래초대면 노래방주소.
    type : "feed", //친구요청이면 frined, 좋아요면 like, 댓글이면 comment, 노래초대면 karaoke
    status : '0'
}
</script>

<style>

</style>
