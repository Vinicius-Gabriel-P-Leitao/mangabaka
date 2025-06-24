<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";

defineProps({
  title: String,
  message: String,
  cause: String,
});

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
    reason: params.get("reason") || "",
  };
});
</script>

<template>
  <main class="grid grid-flow-row grid-cols-1 gap-y-10 place-items-center">
    <section
      class="not-found text-center sm:text-left grid gap-5 grid-rows-2 sm:grid-rows-2 sm:grid-cols-1"
    >
      <div class="grid grid-rows-1 grid-cols-3 gap-3">
        <slot />
      </div>

      <section class="place-self-center w-full max-w-full px-4">
        <p class="mb-2 sm:mb-6 text-2xl sm:text-5xl text-gray-600 text-center">
          {{ title }}
        </p>

        <p
          class="text-xs sm:text-2xl break-words w-full max-w-full text-center"
        >
          {{ message }}
          <strong class="break-all w-full inline-block">{{
            originalPath.original
          }}</strong>
        </p>

        <p
          v-if="originalPath.reason"
          class="mt-3 text-xs sm:text-xl bg-gray-700 rounded-md p-2 break-words w-full max-w-full text-center"
        >
          {{ cause }}
          <code
            class="break-all whitespace-pre-wrap w-full inline-block text-white"
          >
            {{ originalPath.reason }}
          </code>
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
