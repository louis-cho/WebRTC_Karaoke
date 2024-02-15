import { route } from "quasar/wrappers";
import {
  createRouter,
  createMemoryHistory,
  createWebHistory,
  createWebHashHistory,
} from "vue-router";
import routes from "./routes";
import app from "../js/config/preference.js";
import useCookie from "../js/cookie.js";
import axios from "axios";

let pref = app;
const { setCookie, getCookie, removeCookie } = useCookie();

/*
 * 만약 SSR 모드로 빌드되지 않았다면,
 * 라우터 인스턴스를 직접적으로 내보낼 수 있습니다.
 *
 * 아래의 함수는 async를 사용할 수도 있습니다. async/await를 사용하거나
 * Promise를 반환하여 라우터 인스턴스로 해결하는 것 중 하나를 선택하세요.
 */

export default route(function (/* { store, ssrContext } */) {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : process.env.VUE_ROUTER_MODE === "history"
    ? createWebHistory
    : createWebHashHistory;

  const Router = createRouter({
    // 페이지 간 전환 시 스크롤 위치를 맨 위로 이동합니다.
    scrollBehavior: () => ({ left: 0, top: 0 }),

    // './routes.js' 파일에서 가져온 라우터 경로들이 포함됩니다.
    routes,

    // quasar.conf.js 대신 여기서 변경하세요!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.VUE_ROUTER_BASE),
  });

  Router.beforeResolve((to, from, next) => {
    const serverUrl =
      pref.app.api.protocol + pref.app.api.host + "/auth/filter";

    axios
      .get(serverUrl, {
        method: "GET",
        headers: {
          Authorization: getCookie("Authorization"),
          refreshToken: getCookie("refreshToken"),
          "Content-Type": "application/json",
        },
      })
      .then((response) => {
        console.log("ComponenGuardResponse---");
        console.log(response.data["authStatus"]);
        const authStatus = response.data["authStatus"];

        // const pattern = /^http:\/\/localhost:9000\/.+/;
        // const destUrl = "http://localhost:9000" + to.fullPath

        const pattern = /^https:\/\/i10a705.p.ssafy.io\/.+/;
        const destUrl = "https://i10a705.p.ssafy.io" + to.fullPath;

        if (
          (authStatus == 0 ||
            authStatus == 3 ||
            authStatus == 4 ||
            authStatus == null) &&
          pattern.test(destUrl)
        ) {
          alert("로그인 후 이용가능합니다.");
          removeCookie("Authorization");
          removeCookie("refreshToken");
          removeCookie("uuid");
          window.location.replace("/");
          // next();`
        } else {
          next();
        }
      })
      .catch((error) => {
        console.error("Error: ", error);
        console.log("router/index.js ComponentGuard axios 통신 에러");
        next();
      });
  });

  return Router;
});
