import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/Eatsily/rest': {
        target: 'http://localhost:9080',
        changeOrigin: true,
      }
    }
  }
})
