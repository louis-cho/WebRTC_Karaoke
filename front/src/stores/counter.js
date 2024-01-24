import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import { useRouter } from 'vue-router'
import axios from 'axios'

// export const useCounterStore = defineStore('counter', () => {
//   ///api_url 임의로
//   const API_URL = 'http://127.0.0.1:8000'
//   const token = ref(null)

//   const logIn = function(payload) {
//     const user_id = payload.user_id
//     const user_password = payload.user_password
//     axios({
//       method:'post',
//       url:`${API_URL}/api/v1/user/login`,
//       data: {
//         user_id, user_password
//       }
//     })
//     .then((res)=> {
//       token.value = res.data.key
//       // console.log(res.data)
//       console.log('로그인 완료')
//       // router.push({ name : 'home' })
//     })
//     .catch((err)=> {
//       console.log('로그인 에러')
//       console.log(err)
//     })
//   }






//   return {
//     API_URL, token,logIn
//   }}, { persist : true }
// )
