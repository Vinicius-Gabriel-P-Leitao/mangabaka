<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { Components } from "@/export";
import { onMounted } from "vue";
import { useI18n } from "vue-i18n";

const { t, locale } = useI18n();

onMounted(() => {
  const saved = localStorage.getItem("locale");
  if (saved && saved !== locale.value) {
    locale.value = saved;
  }
});
</script>

<template>
  <nav>
    <Components.PluginTranslation />
    <h1 class="text-5xl text-zinc-800 dark:text-teal-50">
      {{ t("home.title") }}
    </h1>
    
    <router-link to="/about" custom v-slot="{ navigate }">
      <Components.BaseButton
        variant="primary"
        :disabled="false"
        @click="navigate"
        class="back-home-button"
      >
        About
      </Components.BaseButton>
    </router-link>
  </nav>
</template>
