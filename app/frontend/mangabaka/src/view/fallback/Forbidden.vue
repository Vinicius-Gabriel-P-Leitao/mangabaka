<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { Composables, Layouts } from "@/export";
import heiter from "@asset/heiter.png";
import { useI18n } from "vue-i18n";

const { t } = useI18n();
Composables.UseLocaleSync();
</script>

<template>
  <Layouts.ErrorFallbackLayout
    :title="t('page.forbidden.title')"
    :message="t('page.forbidden.message')"
    :cause="t('page.forbidden.cause')"
  >
    <span
      class="content-center text-end text-8xl sm:text-[190px]"
      aria-hidden="true"
    >
      4
    </span>
    <span class="place-self-center">
      <img
        :src="heiter"
        :alt="t('page.forbidden.imageAlt')"
        class="w-24 sm:w-40 h-24 sm:h-40 object-cover rounded-full shadow-lg border-4 border-emerald-600"
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
