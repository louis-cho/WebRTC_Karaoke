<template>
  <div class="q-pa-md">
    <div class="q-gutter-y-md">
      <div>
        <img
          src="src/assets/로고1-removebg-preview.png"
          alt="Logo"
          width="150"
          class="d-inline-block align-text-top"
        />
      </div>
      <q-tabs
        v-model="tab"
        class=" full-width"
      >
        <q-tab name="feed" icon="feed" label="피드" />
        <q-tab name="karaoke" icon="music_note" label="노래방" />
        <q-tab name="karaoke" icon="library_music" label="노래방" />
        <q-tab name="karaoke" icon="interpreter_mode" label="노래방" />
        <q-tab name="message" icon="send" label="메시지" />
        <q-tab name="person" icon="person"  />
        <q-tab name="notifications" icon="notifications" />
        <q-tab name="menu" icon="menu" />
      </q-tabs>

      <!-- <q-tabs
        v-model="tab"
        inline-label
        class="bg-black text-white shadow-2"
      >
        <q-tab name="feed" icon="feed" label="피드" />
        <q-tab name="karaoke" icon="music_note" label="노래방" />
        <q-tab name="karaoke" icon="library_music" label="노래방" />
        <q-tab name="karaoke" icon="interpreter_mode" label="노래방" />
        <q-tab name="message" icon="send" label="메시지" />
        <q-tab name="person" icon="person"  />
        <q-tab name="notifications" icon="notifications" />
        <q-tab name="menu" icon="menu" />
      </q-tabs> -->

    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  setup () {
    return {
      tab: ref('mails')
    }
  }
}
</script>
