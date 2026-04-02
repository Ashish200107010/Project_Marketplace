// vite.config.js
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api/colleges': {
        target: 'https://colleges-api-india.fly.dev',
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/colleges/, '/colleges')
      }
    }
  }
});
