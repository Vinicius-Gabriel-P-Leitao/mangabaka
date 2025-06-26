<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { ErrorFallbackLayout } from "@/application/export/Layout";
import frieren from "@asset/frieren.png";
import { onMounted, onUnmounted } from "vue";
import { useI18n } from "vue-i18n";

const { t, locale } = useI18n();
const channel = new BroadcastChannel("locale-change");

channel.onmessage = (event) => {
  if (locale.value !== event.data) {
    locale.value = event.data;
  }
};

onMounted(() => {
  const saved = localStorage.getItem("locale");
  if (saved && saved !== locale.value) {
    locale.value = saved;
  }
});

onUnmounted(() => {
  channel.close();
});
</script>

<template>
  <ErrorFallbackLayout
    :title="t('not_found.title')"
    message="Não foi possível encontrar:"
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
    <span
      class="content-center text-start text-8xl sm:text-[190px]"
      aria-hidden="true"
    >
      4
    </span>
  </ErrorFallbackLayout>
</template>
