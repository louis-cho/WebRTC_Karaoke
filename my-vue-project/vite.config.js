import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { quasar } from '@quasar/vite-plugin';

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [vue(), quasar()],
    resolve: {
        alias: {
            '@': '/src', // 여기에 필요한 alias를 추가해주세요.
        },
    },
});
