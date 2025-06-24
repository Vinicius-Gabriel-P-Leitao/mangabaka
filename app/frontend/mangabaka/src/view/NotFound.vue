<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRoute } from "vue-router";
import FallbackErro from "@/component/FallbackErro.vue";
import frieren from "@assets/frieren-404.png";

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
  <FallbackErro
    title="Página não encontrada 😢"
    message="Não foi possível encontrar:"
    cause="Motivo:"
  >
    <span
      class="content-center text-end text-7xl sm:text-[190px]"
      aria-hidden="true"
    >
      4
    </span>
    <span class="place-self-center">
      <img
        :src="frieren"
        alt="Frieren triste por erro 404"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-white"
      />
    </span>
    <span
      class="content-center text-start text-7xl sm:text-[190px]"
      aria-hidden="true"
    >
      4
    </span>
  </FallbackErro>
</template>
