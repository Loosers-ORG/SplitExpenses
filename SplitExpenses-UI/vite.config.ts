// vite.config.ts
import { defineConfig } from 'vite'

export default defineConfig({
  server: {
    host: '0.0.0.0',
    port: process.env.PORT ? parseInt(process.env.PORT, 10) : 5173,
    allowedHosts: ['splitexpenses-1.onrender.com']
  }
})
