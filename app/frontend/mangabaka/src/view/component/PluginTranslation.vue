<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { AvailableTranslation } from "@/domain/composable/AvailableTranslation";
import i18n from "@/domain/config/I18n";
import { Components, Exceptions, Handlers, Services, Types } from "@/export";
import { ExclamationCircleIcon } from "@heroicons/vue/24/solid";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { locale, availableLocales } = useI18n();
const channel = new BroadcastChannel("locale-change");

const loading = ref(false);
const languages = AvailableTranslation;

watch(locale, (translate: string) => {
  runToastSafe(() => switchLang(translate));
  channel.postMessage(translate);
  localStorage.setItem("locale", translate);
});

function runToastSafe(fn: () => Promise<void>) {
  fn().catch((error) => {
    if (!(error instanceof Exceptions.AppException)) {
      error = new Exceptions.ToastException(
        Handlers.ApiErrorMessageHandler.NOT_FOUND,
        {
          variant: "alert",
          icon: ExclamationCircleIcon,
        },
        { status: 404 }
      );
    }

    throw error;
  });
}

async function switchLang(translate: string) {
  if (!availableLocales.includes(translate)) {
    loading.value = true;

    try {
      const result: Types.ApiResponse<Types.I18nJsonFormat> =
        await Services.FetchDataService<Types.I18nJsonFormat>(
          `/v1/translate/${translate}` // TODO: Criar rota no backend para essa busca
        );

      if (result.data || result.status === 200) {
        i18n.global.setLocaleMessage(translate, result.data);

        Services.AddLanguageInterfaceService(
          translate,
          result.data?.meta?.language ?? translate,
          result.data
        );
      } else {
        throw new Exceptions.ToastException(
          Handlers.ApiErrorMessageHandler.NOT_FOUND,
          {
            variant: "alert",
            icon: ExclamationCircleIcon,
          },
          { status: result.status }
        );
      }
    } catch (exception) {
      if (exception instanceof Exceptions.AppException) {
        throw new Exceptions.ToastException(
          exception.code,
          {
            variant: "alert",
            icon: ExclamationCircleIcon,
          },
          { status: exception.param ?? 500 }
        );
      }

      throw new Exceptions.ToastException(
        Handlers.AppErrorMessageHandler.UNKNOWN,
        {
          variant: "alert",
          icon: ExclamationCircleIcon,
        },
        { status: 500 }
      );
    } finally {
      loading.value = false;
    }
  }
}
</script>

<template>
  <section class="flex items-center gap-3">
    <Components.DropdownTranslation
      label="Idioma:"
      v-model="locale"
      :disabled="loading"
    >
      <option
        v-for="lang in languages"
        :key="lang.code"
        :value="lang.code"
        class="text-[#1a1a1a] dark:text-white bg-[#f4f4f5] dark:bg-[#1a1a1a]"
      >
        {{ lang.label }}
      </option>
    </Components.DropdownTranslation>

    <Components.InfoView infoText="Campo para trocar idioma da interface." />
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
