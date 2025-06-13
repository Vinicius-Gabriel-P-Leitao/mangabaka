<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import frieren from "@assets/frieren-404.png";

const originalPath = ref("");

onMounted(() => {
  const url = new URL(window.location.href);
  const route = useRoute();

  originalPath.value = url.searchParams.get("original") || route.fullPath;
});
</script>

<template>
  <div class="not-found text-center mt-16">
    <img :src="frieren" alt="frieren sleppy 404 error" />

    <h1>404 - Página não encontrada 😢</h1>
    <p>Não foi posivel encontrar: <strong>{{ originalPath }}</strong></p>
    <router-link to="/">Voltar pra Home</router-link>
  </div>
</template>
