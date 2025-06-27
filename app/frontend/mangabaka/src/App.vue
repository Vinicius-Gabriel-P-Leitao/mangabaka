<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from "vue";
import { ToastErrorContainer } from "./export/Layout";

const toastContainer = ref<InstanceType<typeof ToastErrorContainer>>();

function handleGlobalPromiseRejection(event: PromiseRejectionEvent) {
  const error = event.reason;
  toastContainer.value?.addToast(error.error);

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
    <ToastErrorContainer ref="toastContainer" />
    <router-view />
  </div>
</template>
