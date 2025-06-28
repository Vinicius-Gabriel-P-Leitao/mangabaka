<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">

const emit = defineEmits(["update:modelValue"]);

defineProps({
  label: String,
  modelValue: String,
  id: {
    type: String,
    default: () => `dropdown-${Math.random().toString(36).substring(2, 9)}`,
  },
});

function handleChange(event: Event) {
  const target = event.target;
  if (target instanceof HTMLSelectElement) {
    emit("update:modelValue", target.value);
  }
}
</script>

<template>
  <form class="inline-block w-fit" >
    <div class="rounded-[8px] border border-[#1a1a1a] dark:border-white">
      <label
        :for="id"
        class="text-base font-medium rounded-l-[8px] px-5 py-[0.6em] mb-2 bg-[#f4f4f5] dark:bg-[#1a1a1a] text-[#1a1a1a] dark:text-white"
      >
        {{ label }}
      </label>
      <select
        :id="id"
        :value="modelValue"
        @input="handleChange"
        class="text-base font-medium rounded-r-lg border border-transparent px-5 py-2 cursor-pointer transition-colors duration-200 bg-no-repeat bg-[right_1rem_center] bg-[length:1em] bg-[#f4f4f5] dark:bg-[#1a1a1a] text-[#1a1a1a] dark:text-white hover:bg-[#1a1a1a]/20 dark:hover:bg-white/10 focus:outline-none focus:ring-0 focus:border-transparent"
      >
        <slot />
      </select>
    </div>
  </form>
</template>
