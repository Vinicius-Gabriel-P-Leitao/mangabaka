<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { Composables, Layouts } from "@/export";
import sense from "@asset/sense.png";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
Composables.UseLocaleSync();
</script>

<template>
  <Layouts.ErrorFallbackLayout
    :title="t('page.unavailable.title')"
    :message="t('page.unavailable.message')"
    :cause="t('page.unavailable.cause')"
  >
    <span
      class="content-center text-end text-8xl sm:text-[190px]"
      aria-hidden="true"
    >
      5
    </span>
    <span class="place-self-center">
      <img
        :src="sense"
        :alt="t('page.unavailable.imageAlt')"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-stone-700"
      />
    </span>
    <span
      class="content-center text-start text-8xl sm:text-[190px]"
      aria-hidden="true"
    >
      3
    </span>
  </Layouts.ErrorFallbackLayout>
</template>
