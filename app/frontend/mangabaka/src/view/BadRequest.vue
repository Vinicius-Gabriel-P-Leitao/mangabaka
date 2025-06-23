<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import frieren from "@assets/frieren-404.png";
import fern from "@assets/fern-400.png";

const originalPath = ref({
  original: "",
  reason: "",
});

onMounted(() => {
  const url = new URL(window.location.href);
  const route = useRoute();
  const params = url.searchParams;

  originalPath.value = {
    original: params.get("original") || route.fullPath,
    reason: params.get("reason") || "Sem causa do erro.",
  };
});
</script>

<template>
  <main class="grid grid-flow-row grid-cols-1 gap-y-10 place-items-center">
    <section
      class="not-found text-center sm:text-left grid gap-5 grid-rows-2 sm:grid-rows-1 sm:grid-cols-2"
    >
      <div class="grid grid-rows-1 grid-cols-3 gap-3">
        <span
          class="content-center text-end text-8xl sm:text-[190px]"
          aria-hidden="true"
          >4</span
        >
        <span class="place-self-center">
          <img
            :src="frieren"
            alt="Frieren triste por erro 404"
            class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-white"
          />
        </span>
        <span class="place-self-center">
          <img
            :src="fern"
            alt="Frieren triste por erro 404"
            class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-white"
          />
        </span>
      </div>

      <section class="place-self-center">
        <p class="mb-2 sm:mb-6 text-2xl sm:text-5xl text-gray-600">
          Request mal formado 🤨
        </p>
        <p class="text-xs sm:text-2xl">
          Rota original: <strong>{{ originalPath.original }}</strong>
        </p>
        <p v-if="originalPath.reason" class="mt-5 text-xs sm:text-xl">
          <code>{{ originalPath.reason }}</code>
        </p>
      </section>
    </section>

    <nav>
      <router-link to="/" custom v-slot="{ navigate }">
        <button @click="navigate" class="meu-botao">Voltar pra Home</button>
      </router-link>
    </nav>
  </main>
</template>
