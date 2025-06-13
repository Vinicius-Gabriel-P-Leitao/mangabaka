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
  <main class="grid grid-flow-row grid-cols-1 gap-5 place-items-center">
    <section class="not-found text-center sm:text-left grid gap-5 grid-rows-2 sm:grid-rows-1 sm:grid-cols-2">
      <div class="grid grid-rows-1 grid-cols-3 gap-3">
        <span class="content-center text-end text-7xl sm:text-9xl" aria-hidden="true">4</span>
        <span class="place-self-center">
          <img :src="frieren" alt="Frieren triste por erro 404"
            class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-white" />
        </span>
        <span class="content-center text-start text-7xl sm:text-9xl" aria-hidden="true">4</span>
      </div>

      <section class="place-self-center">
        <p class="mb-2 sm:mb-6 text-2xl sm:text-5xl text-gray-600">Página não encontrada 😢</p>
        <p class="text-xs sm:text-2xl">
          Não foi possível encontrar: <strong>{{ originalPath }}</strong>
        </p>
      </section>
    </section>

    <nav>
      <button class="btn-primary">
        <router-link to="/">Voltar pra Home</router-link>
      </button>
    </nav>
  </main>
</template>
