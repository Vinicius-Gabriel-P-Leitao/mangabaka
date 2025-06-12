<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

const originalPath = ref("");

onMounted(() => {
  const url = new URL(window.location.href);
  const route = useRoute();

  originalPath.value = url.searchParams.get("original") || route.fullPath;
});
</script>

<template>
  <div class="not-found text-center mt-16">
    <h1>404 - Página não encontrada 😢</h1>
    <p>
      Não conseguimos encontrar: <strong>{{ originalPath }}</strong>
    </p>

    <router-link to="/">Voltar pra Home</router-link>
  </div>
</template>
