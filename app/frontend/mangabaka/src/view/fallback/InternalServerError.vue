<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { Composables, Layouts } from "@/export";
import flame from "@asset/flame.png";
import serie from "@asset/serie.png";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
Composables.UseLocaleSync();
</script>

<template>
  <Layouts.ErrorFallbackLayout
    :title="t('page.internalServerError.title')"
    :message="t('page.internalServerError.message')"
    :cause="t('page.internalServerError.cause')"
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
        :alt="t('page.internalServerError.imageAlt', { param: 'Flame' })"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-orange-500"
      />
    </span>
    <span class="flex items-center justify-start">
      <img
        :src="serie"
        :alt="t('page.internalServerError.imageAlt', { param: 'Serie' })"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-orange-200"
      />
    </span>
  </Layouts.ErrorFallbackLayout>
</template>
