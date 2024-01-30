<<<<<<< HEAD
import { createApp } from 'vue'
import { createPinia } from 'pinia'


import App from './App.vue'
import router from '@/router'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
=======
import { createApp } from "vue";
import { createPinia } from "pinia";
// import { useKaraokeStore } from "./stores/karaokeStore";
>>>>>>> e9c9979a6ae91e42fc76e89b8dbc00f52ac2181a

import App from "./App.vue";
import router from "./router";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";

// app.config.productionTip = false

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

// app.use(createPinia())
<<<<<<< HEAD
app.use(router)
app.use(pinia)
=======
app.use(router);
app.use(pinia);
app.mount("#app");
>>>>>>> e9c9979a6ae91e42fc76e89b8dbc00f52ac2181a
