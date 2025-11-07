import { defineConfig, loadEnv } from 'vite'
import path from 'path'
import createVitePlugins from './vite/plugins'

const host = "localhost"
const post = "2001"

// https://vitejs.dev/config/
export default defineConfig(({ mode, command }) => {
  const env = loadEnv(mode, process.cwd())
  const { VITE_APP_ENV } = env

  return {
    base: VITE_APP_ENV === 'production' ? '/' : '/',
    plugins: createVitePlugins(env, command === 'build'),

    resolve: {
      alias: {
        '~': path.resolve(__dirname, './'),
        '@': path.resolve(__dirname, './src')
      },
      extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
    },

    server: {
      port: 80,
      host: true,
      open: true,
      proxy: {
        // 保留原有的开发API代理
        '/dev-api': {
          target: `http://${host}:8080`,
          changeOrigin: true,
          rewrite: (p) => p.replace(/^\/dev-api/, '')
        },
      },
      headers: {
        'X-Frame-Options': `ALLOW-FROM http://192.140.189.79:${post}`,
        // 或使用 CSP
        'Content-Security-Policy': `frame-ancestors 'self' http://192.140.189.79:${post};`
      }

    },

    css: {
      postcss: {
        plugins: [
          {
            postcssPlugin: 'internal:charset-removal',
            AtRule: {
              charset: (atRule) => {
                if (atRule.name === 'charset') {
                  atRule.remove()
                }
              }
            }
          }
        ]
      }
    },

    // 如果需要worker-loader功能，Vite有内置支持
    worker: {
      format: 'es', // 或 'iife'
      plugins: []
    },

    build: {
      // 如果需要排除worker文件
      rollupOptions: {
        output: {
          manualChunks: undefined
        },
        // external: [/\.worker\.js$/] // 如果需要排除worker文件
      }
    }
  }
})
