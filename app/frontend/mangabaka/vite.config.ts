import VueI18nPlugin from "@intlify/unplugin-vue-i18n/vite";
import tailwindcss from "@tailwindcss/vite";
import vue from "@vitejs/plugin-vue";
import path, { dirname, resolve } from "path";
import { fileURLToPath } from "url";
import { defineConfig } from "vite";

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
    VueI18nPlugin({
      include: resolve(
        dirname(fileURLToPath(import.meta.url)),
        "./src/application/locale/**"
      ),
    }),
  ],
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
      "@asset": path.resolve(__dirname, "./asset"),
    },
  },
  server: {
    // NOTE: Redirecionamento para backend quando em dev mode
    proxy: {
      "/v1": {
        target: "http://localhost:9089",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
