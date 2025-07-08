<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { Types } from "@/export";
import { XMarkIcon } from "@heroicons/vue/24/solid";
import { computed, ref, watch } from "vue";

const props = defineProps<{
    error: { message: string; error: Types.GlobalToastErrorPayload } | null;
    variant?: Types.ToastVariant | "error";
    descriptionIcon: string;
    duration?: number;
}>();

const localMessage = ref("");
const localVariant = ref<Types.ToastVariant>("error");
const emit = defineEmits(["close"]);

watch(
    () => props.error,
    (error) => {
        if (error) {
            localMessage.value = error.message;
            localVariant.value = error.error.variant;

            if (props.duration) {
                setTimeout(() => {
                    emit("close");
                }, props.duration);
            }
        } else {
            localMessage.value = "";
            localVariant.value = "error";
        }
    },
    { immediate: true },
);

// prettier-ignore
const classes = {
  base: "notification bottom-4 right-4 z-50 w-full rounded-[8px] max-w-xs p-4 mb-4 gap-4 text-base font-medium transition-colors border flex inline-flex items-center justify-center focus:outline-none focus:ring focus:ring-offset-0 text-[#1a1a1a] dark:text-white",
  variants: {
    alert:
      "bg-[#f4f4f5] dark:bg-[#1a1a1a] border-[#1a1a1a] dark:border-white hover:bg-[#1a1a1a]/20 dark:hover:bg-white/10 focus:ring-white",
    success:
      "bg-green-400/10 border-green-400 hover:bg-green-600/40 focus:ring-green-600",
    error:
      "bg-red-400/10 border-red-400 hover:bg-red-600/40 focus:ring-red-600",
  },
  disabled: "opacity-50 cursor-not-allowed",
};

const computedClasses = computed(() => {
    return [
        classes.base,
        classes.variants[localVariant.value] ?? classes.variants.error,
    ].join(" ");
});
</script>

<template>
    <article :class="computedClasses" role="alert">
        <header
            class="inline-flex items-center justify-center shrink-0 w-8 h-8"
        >
            <slot />
            <span class="sr-only">{{ descriptionIcon }}</span>
        </header>

        <p class="flex-1 min-w-0">
            <span
                class="block text-sm font-normal overflow-hidden text-ellipsis"
            >
                {{ localMessage }}
            </span>
        </p>

        <button
            type="button"
            @click="$emit('close')"i
            class="cursor-pointer ms-auto -mx-1.5 -my-1.5 p-1.5 rounded-lg focus:ring-1 inline-flex items-center justify-center h-8 w-8 bg-white dark:bg-[#1a1a1a] text-[#1a1a1a] dark:text-white hover:text-white hover:bg-[#1a1a1a]/20 dark:hover:bg-white/40 focus:ring-gray-300"
            data-dismiss-target="#toast-success"
            aria-label="Close"
        >
            <span class="sr-only">Close</span>
            <XMarkIcon aria-hidden="true" />
        </button>
    </article>
</template>
