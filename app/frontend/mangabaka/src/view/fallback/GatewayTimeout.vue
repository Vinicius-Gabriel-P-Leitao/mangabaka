<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->

<script setup lang="ts">
import ErrorFallbackLayout from "@/layout/ErrorFallbackLayout.vue";
import himmel from "@asset/himmel.png";
</script>

<template>
  <ErrorFallbackLayout
    title="Timeout do Gateway 🧐"
    message="Não foi possível em:"
    cause="Motivo:"
  >
    <span
      class="content-center text-end text-8xl sm:text-[190px]"
      aria-hidden="true"
    >
      5
    </span>
    <span class="place-self-center">
      <img
        :src="himmel"
        alt="Flame from sou sou no Frieren"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-blue-200"
      />
    </span>
    <span
      class="content-center text-start text-8xl sm:text-[190px]"
      aria-hidden="true"
    >
      4
    </span>
  </ErrorFallbackLayout>
</template>
