<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { ToastException } from "@/application/error/ToastException";
import i18n from "@/domain/config/I18n";
import { DropdownTranslation, InfoView } from "@/export/Component";
import { FetchTranslateJson } from "@/export/Service";
import type { ApiResponse, I18nJsonFormat } from "@/export/Type";
import { ExclamationCircleIcon } from "@heroicons/vue/24/solid";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { locale, availableLocales } = useI18n();
const channel = new BroadcastChannel("locale-change");

const loading = ref(false);
const languages = ref([
  { code: "pt-BR", label: "Português (Brasil)" },
  { code: "en-US", label: "English (US)" },
  { code: "en-RS", label: "English (RS)" },
]);

watch(locale, (newTranslate: string) => {
  runToastSafe(() => switchLang(newTranslate));
  channel.postMessage(newTranslate);
  localStorage.setItem("locale", newTranslate);
});

function runToastSafe(fn: () => Promise<void>) {
  fn().catch((error) => {
    if (!(error instanceof ToastException)) {
      error = new ToastException(error.message || "Erro inesperado", {
        message: error.message || "Erro inesperado",
        variant: "error",
        icon: ExclamationCircleIcon,
      });
    }

    throw error;
  });
}

async function switchLang(newTranslate: string) {
  if (!availableLocales.includes(newTranslate)) {
    loading.value = true;

    try {
      const result: ApiResponse<I18nJsonFormat> =
        await FetchTranslateJson<I18nJsonFormat>(
          `/v1/translate/${newTranslate}` // TODO: Criar rota no backend para essa busca
        );
      console.log("asdkokas");

      if (result.data || result.status != 200) {
        i18n.global.setLocaleMessage(newTranslate, result.data);
      } else {
        throw new ToastException("Tradução não encontrada", {
          message: "Tradução não encontrada",
          variant: "alert",
          icon: ExclamationCircleIcon,
        });
      }
    } catch (exception) {
      if (exception instanceof Error) {
        throw new ToastException("Falha ao salvar", {
          message: exception.message,
          variant: "error",
          icon: ExclamationCircleIcon,
        });
      } else {
        throw new ToastException("Falha ao salvar", {
          message: "Erro desconhecido.",
          variant: "error",
          icon: ExclamationCircleIcon,
        });
      }
    } finally {
      loading.value = false;
    }
  }
}
</script>

<template>
  <section class="flex items-center gap-3">
    <DropdownTranslation label="Idioma:" v-model="locale" :disabled="loading">
      <option
        v-for="lang in languages"
        :key="lang.code"
        :value="lang.code"
        class="text-[#1a1a1a] dark:text-white bg-[#f4f4f5] dark:bg-[#1a1a1a]"
      >
        {{ lang.label }}
      </option>
    </DropdownTranslation>

    <InfoView infoText="Campo para trocar idioma da interface." />
  </section>
</template>

<style>
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(20px);
}
.toast-enter-to,
.toast-leave-from {
  opacity: 1;
  transform: translateY(0);
}
.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.3s ease, transform 0.3s ease;
}
</style>
