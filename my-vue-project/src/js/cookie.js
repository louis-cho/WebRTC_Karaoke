import { ref } from 'vue';
import Cookies from 'js-cookie';

export default function useCookie() {
  // 쿠키 값을 저장할 리액티브 변수 생성
  const cookieValue = ref(null);

  // 쿠키를 설정하는 함수
  const setCookie = (name, value, options) => {
    Cookies.set(name, value, options);
    // 쿠키 값을 업데이트
    cookieValue.value = value;
  };

  // 쿠키를 가져오는 함수
  const getCookie = (name) => {
    const value = Cookies.get(name);
    // 쿠키 값을 업데이트
    cookieValue.value = value;
    return value;
  };

  // 쿠키 값을 삭제하는 함수
  const removeCookie = (name, options) => {
    Cookies.remove(name, options);
    // 쿠키 값을 null로 설정하여 제거됨을 나타냄
    cookieValue.value = null;
  };

  return {
    cookieValue,
    setCookie,
    getCookie,
    removeCookie
  };
}
