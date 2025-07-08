<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { Types, Components } from "@/export";
import { ref } from "vue";

const toasts = ref<
  Array<{
    id: number;
    error: { message: string; error: Types.GlobalToastErrorPayload };
  }>
>([]);
let toastId = 0;

function addToast(payload: {
  message: string;
  error: Types.GlobalToastErrorPayload;
}) {
  const id = ++toastId;
  toasts.value.unshift({ id, error: payload });

  setTimeout(() => removeToast(id), 3000);
}

function removeToast(id: number) {
  toasts.value = toasts.value.filter((toast) => toast.id !== id);
}

defineExpose({ addToast });
</script>

<template>
  <TransitionGroup
    name="toast"
    tag="div"
    class="fixed bottom-4 right-4 z-50 flex flex-col-reverse gap-2"
  >
    <Components.Toast
      v-for="toast in toasts"
      :key="toast.id"
      :error="toast.error"
      :description-icon="'Erro'"
      @close="removeToast(toast.id)"
    >
      <component :is="toast.error.error.icon" class="h-6 w-6" />
    </Components.Toast>
  </TransitionGroup>
</template>
