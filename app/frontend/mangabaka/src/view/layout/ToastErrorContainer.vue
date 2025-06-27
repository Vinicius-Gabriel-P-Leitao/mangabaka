<script setup lang="ts">
import { ref } from "vue";
import { ToastError } from "@/export/Component";
import type { GlobalErrorPayload } from "@/export/Type";

const toasts = ref<Array<{ id: number; error: GlobalErrorPayload }>>([]);
let toastId = 0;

function addToast(error: GlobalErrorPayload) {
  const id = ++toastId;
  toasts.value.unshift({ id, error });

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
    <ToastError
      v-for="toast in toasts"
      :key="toast.id"
      :error="toast.error"
      :description-icon="'Erro'"
      @close="removeToast(toast.id)"
    >
      <component :is="toast.error.icon" class="" />
    </ToastError>
  </TransitionGroup>
</template>
