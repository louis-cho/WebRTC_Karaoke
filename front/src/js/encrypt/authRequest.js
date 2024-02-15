import * as RSA from "./rsa.js";
import app from "../config/preference.js";
import useCookie from "../cookie.js";
import { useKaraokeStore } from "@/stores/karaokeStore.js";
const store = useKaraokeStore();

import {useLoginStore} from "@/stores/loginStore.js"
const loginStore = useLoginStore();

import { ref } from "vue";
let pref = app;
let modulus = null;
let exponent = null;
export let rsa = new RSA.RSAKey();
const { setCookie, getCookie, removeCookie } = useCookie();

// Empty function for the "공개키 받아오기" button
async function getPublicKey() {
  // Add your logic here or leave it empty
  // const serverUrl = "http://localhost:8081/api/v1/user/login"; // Update the URL accordingly
  // const serverUrl = "https://i10a705.p.ssafy.io/api/user/login"; // Update the URL accordingly
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.user.login;

  // Create a data object with the user credentials
  const data = {
    type: "getPublicKey",
  };

  // Send a POST request to the server
  await fetch(serverUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => response.json())
    .then((result) => {
      // Handle the result from the server as needed
      console.log(
        "서버로부터 받은 공개키 >> " + result.modulus + "," + result.exponent
      );
      modulus = result.modulus;
      exponent = result.exponent;

      rsa.setPublic(modulus, exponent);
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

export async function sendAESKey(aesKeyData) {
  let encryptedKey = rsa.encrypt(aesKeyData);

  // const serverUrl = "http://localhost:8081/user/login"; // Update the URL accordingly
  // const serverUrl = "https://i10a705.p.ssafy.io/user/login"; // Update the URL accordingly
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.user.login;

  const data = {
    type: "setAESKey",
    key: encryptedKey,
  };

  // Send a POST request to the server
  await fetch(serverUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => response.json())
    .then((result) => {
      // Handle the result from the server as needed
      console.log(result);
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

async function login(id, pw) {
  // Add your logic here or leave it empty
  // const serverUrl = "http://localhost:8081/api/v1/user/login"; // Update the URL accordingly
  // const serverUrl = "https://i10a705.p.ssafy.io/user/login"; // Update the URL accordingly

  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.user.login;

  // Create a data object with the user credentials
  const data = {
    type: "login",
    id: id,
    pw: rsa.encrypt(pw),
  };

  // Send a POST request to the server
  await fetch(serverUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => response.json())
    .then((result) => {
      // Handle the result from the server as needed
      console.log("서버에서 넘어온 ACCESS_TOKEN ==> " + result.Authorization);
      console.log("서버에서 넘어온 REFRESH_TOKEN ==> " + result.refreshToken);
      console.log("서버에서 넘어온 UUID ==> " + result.uuid);
      if (
        result.Authorization != null &&
        result.refreshToken != null &&
        result.uuid != null
      ) {
        const ACCESS_TOKEN = result.Authorization;
        const REFRESH_TOKEN = result.refreshToken;
        const UUID = result.uuid;
        setCookie("Authorization", ACCESS_TOKEN, {
          path: "/",
          secure: true,
          sameSite: "none",
        });
        setCookie("refreshToken", REFRESH_TOKEN, {
          path: "/",
          secure: true,
          sameSite: "none",
        });
        setCookie("uuid", UUID, {
          path: "/",
          secure: true,
          sameSite: "none",
        });
        store.userName = result.nickname;
        loginStore.isLoggedIn = true;
        // location.reload();
      } else {
        console.log("로그인 실패");
        alert("아이디 혹은 비밀번호가 올바르지 않습니다.");
      }
    })
    .catch((error) => {
      console.error("Error:", error);
      // isLoggedIn.value = false;
    });
}

async function register(id, pw, email, nickname) {
  // const serverUrl = "http://localhost:8081/api/v1/user/register"; // Update the URL accordingly
  // const serverUrl = "https://i10a705.p.ssafy.io/user/register"; // Update the URL accordingly
  const serverUrl =
    pref.app.api.protocol + pref.app.api.host + pref.app.api.user.register;

  // Create a data object with the user credentials
  const data = {
    type: "register",
    id: id,
    pw: rsa.encrypt(pw),
    email: email,
    nickname: nickname,
  };

  // Send a POST request to the server
  await fetch(serverUrl, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
  })
    .then((response) => response.json())
    .then((result) => {
      if (result.token) {
        // 토큰 관리 js 파일의 기능을 호출하기
      }
    })
    .catch((error) => {
      console.error("Error:", error);
    });
}

function handleRegister() {
  let id = document.getElementById("id").value;
  let pw = document.getElementById("pw").value;
  let email = document.getElementById("email").value;
  let nickname = document.getElementById("nickname").value;

  register(id, pw, email, nickname);
}

export { getPublicKey, modulus, exponent, handleRegister, register, login };
