import { createRequire } from 'node:module'
import { fileURLToPath } from 'node:url'
import express from 'express'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

const require = createRequire(import.meta.url)
const apiMockSetup = require('./src/dev/ApiMockSetup.js')

export default defineConfig({
  plugins: [
    vue(),
    {
      name: 'admin-api-mocks',
      configureServer(server) {
        const app = express()
        app.use(express.json())
        apiMockSetup(app)
        server.middlewares.use(app)
      }
    }
  ],
  resolve: {
    extensions: ['.mjs', '.js', '.mts', '.ts', '.jsx', '.tsx', '.json', '.vue'],
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  base: '/'
})
