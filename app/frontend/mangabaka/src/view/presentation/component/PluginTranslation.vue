<script setup lang="ts">
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";
import i18n from "@/application/locale/I18n";
import { FetchTranslateJson } from "@/application/export/Service";
import type { I18nJsonFormat, ApiResponse } from "@/application/export/Type";

const loading = ref(false);
const { locale, availableLocales } = useI18n();

const languages = ref([
  { code: "pt-BR", label: "Português (Brasil)" },
  { code: "en-US", label: "English (US)" },
]);

watch(locale, (newTranslate: string) => {
  switchLang(newTranslate);
});

async function switchLang(newTranslate: string) {
  if (!availableLocales.includes(newTranslate)) {
    try {
      const result: ApiResponse<I18nJsonFormat> =
        await FetchTranslateJson<I18nJsonFormat>(
          `/v1/translate/${newTranslate}`,
        );

      if (result.data) {
        i18n.global.setLocaleMessage(newTranslate, result.data);
        locale.value = newTranslate;
      } else {
        loading.value = false;
        alert("Não veio dados para essa tradução, parceiro.");
      }
    } catch (exception) {
      loading.value = false;
      alert("Erro ao carregar tradução.");
    } finally {
      loading.value = false;
    }
  }
}
</script>

<template>
  <label for="idioma">Idioma:</label>
  <select id="idioma" v-model="locale" :disabled="loading">
    <option v-for="lang in languages" :key="lang.code" :value="lang.code">
      {{ lang.label }}
    </option>
  </select>
</template>
