<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->

<script setup lang="ts">
import ErrorFallbackLayout from "@/layout/ErrorFallbackLayout.vue";
import frieren from "@asset/frieren.png";
import fern from "@asset/fern.png";
</script>

<template>
  <ErrorFallbackLayout
    title="Request mal formado 🤨"
    message="Não foi possível em:"
    cause="Motivo:"
  >
    <span
      class="content-center text-end text-8xl sm:text-[190px]"
      aria-hidden="true"
    >
      4
    </span>
    <span class="place-self-center">
      <img
        :src="frieren"
        alt="Frieren from sou sou no Frieren"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-white"
      />
    </span>
    <span class="place-self-center">
      <img
        :src="fern"
        alt="Fern from sou sou no Frieren"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-purple-900"
      />
    </span>
  </ErrorFallbackLayout>
</template>
