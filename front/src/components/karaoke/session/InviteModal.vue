<template>
  <!-- 모달 -->
  <q-dialog v-model="notificationStore.inviteModal">
    <q-card>
    <q-card-section>
      <q-card-actions vertical>
        <q-btn flat
          v-for="friend in friendIdAndNameList"
          :key="friend.friendId"
          clickable
          @click="inviteFriend(friend)"
          >
            <q-item-section>
              <q-item-label >
               {{ friend.nickname }}님 노래방으로 초대하기.
              </q-item-label>
            </q-item-section>
         </q-btn>
       </q-card-actions>
    </q-card-section>
  </q-card>
  </q-dialog>
</template>

<script setup>
import { ref, defineProps, onMounted } from "vue";

import { useNotificationStore } from "@/stores/notificationStore";
import { fetchFriendList } from "@/js/friends/friends.js";
import { fetchUser } from "@/js/user/user.js";

const props = defineProps(['sessionName' ]);
//친구목록
const friendList = ref([]);
const friendIdAndNameList = ref([]);
const notificationStore = useNotificationStore();


function closeModal() {
  notificationStore.inviteModal = false;
}
const inviteFriend = (friend) => {
  const body ={
    toUser : friend.userPk,
    info : `${props.sessionName}`,
    type : "karaoke",
    status : '0'
  };

  notificationStore.sendNotification(body);
  console.log("노래방 초대 알림보냄!")
  closeModal();
}

onMounted(async () => {
  const userId = 1; //실제 유저Id로 변경해야함
  const pageNo = 0; //실제 page로 변경해야함
  const sizeNo = 10; //실제 size로 변경해야함
  friendList.value = await fetchFriendList(userId, pageNo, sizeNo)
  console.log("FriendList 불러옵니다. ", friendList.value)

  // Transform friendList to friendIdAndName
  friendIdAndNameList.value = await Promise.all(
  friendList.value.map(async (friend) => {
    // Determine userPk based on conditions
    const userPk = friend.fromUserPk === 2/* 로그인한 유저pk */ ? friend.toUserPk : friend.fromUserPk;

    // Fetch the user name using getUserName function
    const user = await fetchUser(userPk);
    const nickname = user.nickname;

    // Return the new object with userPk and name
    return { userPk, nickname };
  })
);
})
</script>

<style scoped></style>
