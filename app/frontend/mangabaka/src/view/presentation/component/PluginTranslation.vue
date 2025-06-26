<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { DropdownTranslation } from "@/application/export/Component";
import { FetchTranslateJson } from "@/application/export/Service";
import type { ApiResponse, I18nJsonFormat } from "@/application/export/Type";
import i18n from "@/domain/config/I18n";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const loading = ref(false);
const { locale, availableLocales } = useI18n();
const channel = new BroadcastChannel("locale-change");

const languages = ref([
  { code: "pt-BR", label: "Português (Brasil)" },
  { code: "en-US", label: "English (US)" },
]);

watch(locale, (newTranslate: string) => {
  switchLang(newTranslate);
  channel.postMessage(newTranslate);
  localStorage.setItem("locale", newTranslate);
});

async function switchLang(newTranslate: string) {
  if (!availableLocales.includes(newTranslate)) {
    loading.value = true;

    try {
      const result: ApiResponse<I18nJsonFormat> =
        await FetchTranslateJson<I18nJsonFormat>(
          `/v1/translate/${newTranslate}`
        );

      if (result.data) {
        i18n.global.setLocaleMessage(newTranslate, result.data);
      } else {
        alert("Não veio dados para essa tradução, parceiro.");
      }
    } catch (exception) {
      alert("Erro ao carregar tradução.");
    } finally {
      loading.value = false;
    }
  }
}
</script>

<template>
  <DropdownTranslation label="Idioma:" v-model="locale" :disabled="loading">
    <option v-for="lang in languages" :key="lang.code" :value="lang.code">
      {{ lang.label }}
    </option>
  </DropdownTranslation>
</template>
