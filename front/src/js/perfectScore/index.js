import { App } from "./App.js";

/**
 * 주파수에서 음표를 계산하여 반환하는 함수입니다.
 * @param {number} frequency - 주파수 값입니다.
 * @returns {number} - 주어진 주파수에 해당하는 음표를 반환합니다.
 */
export function noteFromPitch(frequency) {
  var noteNum = 12 * (Math.log(frequency / 440) / Math.log(2));
  return Math.round(noteNum) + 69;
}

// App 클래스를 인스턴스화하여 앱을 시작합니다.
// export const app = new App(document.querySelector("#app1"));

let appInstance = null;

export function initializeApp(div) {
    // appInstance = new App(document.querySelector("#app1"));
    appInstance = new App(div);
}

export function getAppInstance() {
    return appInstance;
}
