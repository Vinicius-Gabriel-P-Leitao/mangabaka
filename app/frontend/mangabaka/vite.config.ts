import { defineConfig } from "vite";
import path from "path";
import vue from "@vitejs/plugin-vue";
import tailwindcss from "@tailwindcss/vite";

export default defineConfig({
  plugins: [vue(), tailwindcss()],
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
