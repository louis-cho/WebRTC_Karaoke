import { defineStore } from "pinia";
import pref from "@/js/config/preference.js";
import axios from "axios";
import useCookie from "@/js/cookie.js";
import { EventSourcePolyfill } from 'event-source-polyfill';

export const useLoginStore = defineStore("login", {
  state: () => ({
    isLoggedIn : false,


  }),
});
