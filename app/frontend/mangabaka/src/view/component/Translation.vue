<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { AvailableTranslation } from "@/domain/composable/AvailableTranslation";
import { Components, Exceptions, Handlers, Services } from "@/export";
import { ExclamationCircleIcon } from "@heroicons/vue/24/solid";
import { watch } from "vue";
import { useI18n } from "vue-i18n";

const { t, locale } = useI18n();
const channel = new BroadcastChannel("locale-change");
const languages = AvailableTranslation;

watch(locale, async (translate: string) => {
  await runToastSafe(() => Services.SwitchLanguageInterfaceService(translate));
  channel.postMessage(translate);
  localStorage.setItem("locale", translate);
});

async function runToastSafe(fn: () => Promise<void>) {
  fn().catch((error) => {
    if (!(error instanceof Exceptions.AppException)) {
      error = new Exceptions.ToastException(
        Handlers.ApiErrorMessageHandler.NOT_FOUND,
        {
          variant: "error",
          icon: ExclamationCircleIcon,
        },
        { status: 404 }
      );
    }

    throw error;
  });
}
</script>

<template>
  <section class="flex items-center gap-3">
    <Components.Select :label="t('component.select.label')" v-model="locale">
      <option
        v-for="lang in languages"
        :key="lang.code"
        :value="lang.code"
        class="text-[#1a1a1a] dark:text-white bg-[#f4f4f5] dark:bg-[#1a1a1a]"
      >
        {{ lang.label }}
      </option>
    </Components.Select>

    <Components.InfoView :infoText="t('component.translation.infoView')" />
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
