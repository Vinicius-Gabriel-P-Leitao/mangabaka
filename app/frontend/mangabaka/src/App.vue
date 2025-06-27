<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";
import { Layouts, Utils } from "@/export";

const toastContainer = ref<InstanceType<typeof Layouts.ToastErrorContainer>>();

function handleGlobalPromiseRejection(event: PromiseRejectionEvent) {
  const normalized = Utils.NormalizeGlobalError(event.reason);
  toastContainer.value?.addToast(normalized);
  event.preventDefault();
}

onMounted(() => {
  window.addEventListener("unhandledrejection", handleGlobalPromiseRejection);
});

// prettier-ignore
onBeforeUnmount(() => {
  window.removeEventListener( "unhandledrejection", handleGlobalPromiseRejection);
});
</script>

<template>
  <div>
    <Layouts.ToastErrorContainer ref="toastContainer" />
    <router-view />
  </div>
</template>
