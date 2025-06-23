<script setup lang="ts">
import { useRoute } from "vue-router";
const route = useRoute();
</script>

<template>
  <div class="p-6">
    <h1 class="text-yellow-600 text-2xl font-bold">⚠️ Requisição Inválida</h1>
    <p>
      Rota original: <code>{{ route.query.original }}</code>
    </p>
    <p v-if="route.query.reason">Motivo: {{ route.query.reason }}</p>
  </div>
</template>
