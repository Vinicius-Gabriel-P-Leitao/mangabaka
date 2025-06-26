<script setup lang="ts">
import { QuestionMarkCircleIcon } from "@heroicons/vue/24/solid";
import { onUnmounted, ref } from "vue";

defineProps<{
  infoText: string;
}>();

const show = ref(false);
const iconRef = ref<HTMLElement | null>(null);
let timer: ReturnType<typeof setTimeout> | null = null;

const positionAbscissa = ref<"top" | "bottom">("top");
const positionOrdinate = ref<"right" | "left">("right");
const MIN_MARGIN = 80;

function checkPosition() {
  if (!iconRef.value) return;
  const rect = iconRef.value.getBoundingClientRect();

  const spaceAbove = rect.top;
  const spaceBelow = window.innerHeight - rect.bottom;

  positionAbscissa.value =
    spaceAbove < MIN_MARGIN && spaceBelow > MIN_MARGIN ? "bottom" : "top";

  const spaceLeft = rect.left;
  const spaceRight = window.innerWidth - rect.right;

  positionOrdinate.value =
    spaceLeft < MIN_MARGIN && spaceRight > MIN_MARGIN ? "left" : "right";
}

function onMouseEnter() {
  checkPosition();
  timer = setTimeout(() => (show.value = true), 300);
}

function onMouseLeave() {
  if (timer) {
    clearTimeout(timer);
    timer = null;
  }

  show.value = false;
}

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
          'absolute text-xs rounded px-3 py-1 select-none whitespace-nowrap shadow-lg z-50 text-[#1a1a1a] dark:text-white bg-[#f4f4f5] dark:bg-[#1a1a1a]',
          positionAbscissa === 'top' ? 'bottom-full mb-2' : 'top-full mt-2',
          positionOrdinate == 'right'
            ? 'left-full top-1/2 -translate-y-1/2'
            : 'right-full top-1/2 -translate-y-1/2',
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
