<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->

<script setup lang="ts">
import ErrorFallbackLayout from "@/layout/ErrorFallbackLayout.vue";
import flame from "@asset/flame.png";
import serie from "@asset/serie.png";
</script>

<template>
  <ErrorFallbackLayout
    title="Erro interno 💀"
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
        :src="flame"
        alt="Flame from sou sou no Frieren"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-orange-500"
      />
    </span>
    <span class="place-self-center">
      <img
        :src="serie"
        alt="Serie from sou sou no Frieren"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-orange-200"
      />
    </span>
  </ErrorFallbackLayout>
</template>
