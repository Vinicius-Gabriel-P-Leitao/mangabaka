<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { QuestionMarkCircleIcon } from "@heroicons/vue/24/solid";
import { onMounted, onUnmounted, ref } from "vue";

defineProps<{ infoText: String }>();

const show = ref(false);
const iconRef = ref<HTMLElement | null>(null);
let timer: ReturnType<typeof setTimeout> | null = null;

const positionAbscissa = ref<"top" | "bottom">("top");

function checkPosition() {
  if (!iconRef.value) return;
  const rect = iconRef.value.getBoundingClientRect();

  const spaceAbove = rect.top;
  const spaceBelow = window.innerHeight - rect.bottom;

  positionAbscissa.value = spaceBelow > spaceAbove ? "bottom" : "top";
}

function onMouseEnter() {
  timer = setTimeout(() => (show.value = true), 300);
}

function onMouseLeave() {
  if (timer) {
    clearTimeout(timer);
    timer = null;
  }

  show.value = false;
}

onMounted(() => {
  checkPosition();
});

onUnmounted(() => {
  if (timer) clearTimeout(timer);
});
</script>

<template>
  <div
    @mouseenter="onMouseEnter"
    @mouseleave="onMouseLeave"
    class="relative inline-block text-[#1a1a1a] dark:text-white"
    aria-describedby="tooltip"
    ref="iconRef"
  >
    <QuestionMarkCircleIcon class="w-6 h-6 cursor-pointer" />
    <transition name="fade">
      <div
        v-if="show"
        :class="[
          'absolute -translate-x-1/2 left-1/2 z-50 inline-block text-xs rounded w-fit h-fit p-1 select-none whitespace-nowrap shadow-lg text-[#1a1a1a] dark:text-white bg-[#f4f4f5] dark:bg-[#1a1a1a] transform',
          positionAbscissa === 'top' ? 'bottom-full mb-2' : 'top-full mt-2',
        ]"
      >
        {{ infoText }}
      </div>
    </transition>
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
