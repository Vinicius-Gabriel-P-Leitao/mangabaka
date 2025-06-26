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
  disabled: Boolean,
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
  <form class="max-w-sm mx-auto">
    <label
      :for="id"
      class="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
    >
      {{ label }}
    </label>
    <select
      :id="id"
      :value="modelValue"
      :disabled="disabled"
      @input="handleChange"
      class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-purple-900 focus:border-purple-900 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-purple-900 dark:focus:border-purple-900"
    >
      <slot />
    </select>
  </form>
</template>
