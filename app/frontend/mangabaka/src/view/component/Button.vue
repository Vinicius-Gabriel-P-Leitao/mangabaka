<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import type { Types } from "@/export";
import { computed } from "vue";

const props = defineProps<{
  type?: "button" | "submit" | "reset";
  variant?: Types.ButtonVariant;
  disabled?: boolean;
}>();

const type = props.type ?? "button";
const variant = props.variant ?? "primary";
const disabled = props.disabled ?? false;

// prettier-ignore
const classes = {
  base: "rounded-[8px] text-base font-medium transition-colors border cursor-pointer inline-flex items-center justify-center px-5 py-[0.6em] focus:outline-none focus:ring focus:ring-offset-0 text-[#1a1a1a] dark:text-white",
  variants: {
    primary:
      "bg-[#f4f4f5] dark:bg-[#1a1a1a] border-[#1a1a1a] dark:border-white hover:bg-[#1a1a1a]/20 dark:hover:bg-white/10 focus:ring-white",
    secondary:
      "bg-blue-400/10 border-blue-400 hover:bg-blue-600/40 focus:ring-blue-600",
    danger:
      "bg-red-400/10 border-red-400 hover:bg-red-600/40 focus:ring-red-600",
  },
  disabled: "opacity-50 cursor-not-allowed",
};

const computedClasses = computed(() => {
  return [
    classes.base,
    classes.variants[variant] ?? classes.variants.primary,
    disabled ? classes.disabled : "",
  ].join(" ");
});
</script>

<template>
  <button :type="type" :class="computedClasses" :disabled="disabled">
    <slot />
  </button>
</template>
