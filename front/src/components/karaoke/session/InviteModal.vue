<template>
  <!-- 모달 -->
  <q-dialog v-model="notificationStore.inviteModal">
    <q-card>
      <q-card-section>
        <q-card-actions vertical>
          <h6 align="center">친구 초대하기</h6>
          <q-btn
            flat
            v-for="friend in friendIdAndNameList"
            :key="friend.userKey"
            clickable
            @click="inviteFriend(friend)"
          >
            <q-item-section>
              <q-item-label>
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
import useCookie from "@/js/cookie.js";

const { setCookie, getCookie, removeCookie } = useCookie();

const props = defineProps(["sessionName"]);
//친구목록
const friendList = ref([]);
const friendIdAndNameList = ref([]);
const notificationStore = useNotificationStore();

function closeModal() {
  notificationStore.inviteModal = false;
}
const inviteFriend = (friend) => {
  const body = {
    toUserKey: friend.userKey,
    info: `${props.sessionName}`,
    type: "karaoke",
    status: "0",
  };

  notificationStore.sendNotification(body);
  closeModal();
};

onMounted(async () => {
  const pageNo = 0; //실제 page로 변경해야함
  const sizeNo = 10; //실제 size로 변경해야함
  friendList.value = await fetchFriendList(pageNo, sizeNo);
  console.log("친구리스트 :",friendList.value )
  // Transform friendList to friendIdAndName
  friendIdAndNameList.value = await Promise.all(
    friendList.value.map(async (friend) => {

    console.log("friend.fromUserKey", friend.fromUserKey)
    console.log("friend.toUserKey", friend.toUserKey)
    console.log(`getCookie("uuid")`,getCookie('uuid'))
      const userKey = friend.fromUserKey === getCookie("uuid") ? friend.toUserKey : friend.fromUserKey;
      // Fetch the user name using getUserName function
      console.log("userKey :",userKey)
      const user = await fetchUser(userKey);
      const nickname = user.nickname;
      console.log('nickname : ',nickname)

      // Return the new object with userKey and name
      return { userKey, nickname };
    })
  );
});
</script>

<style scoped></style>
