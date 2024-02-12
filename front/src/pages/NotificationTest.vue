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
const bodyK = {
    toUser : friend.friendId, //받는사람 userPk.
    info : `${sessionName}`, //게시글, 댓글 내용이면 유저아이디, 노래초대면 노래방 세션아이디
    type : "karaoke",
    status : '0'
}

//댓글일때 알림 생성 body 예시
const body = {
    toUser : feed.value.user.userPk, //받는사람 userPk.
    info : `${feed.value.feedId}`, //게시글, 댓글 내용이면 유저아이디, 노래초대면 노래방 세션아이디
    type : "like",
    status : '0'
}

</script>

<style>

</style>
