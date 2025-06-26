<!-- SPDX-License-Identifier: BSD-3-Clause -->
<!---->
<!-- Copyright (c) 2025 Vinícius Gabriel Pereira Leitão -->
<!-- Licensed under the BSD 3-Clause License. -->
<!-- See LICENSE file in the project root for full license information. -->
<script setup lang="ts">
import { PluginTranslation } from "@/application/export/Component";
</script>

<template>
  <nav>
    <PluginTranslation />
  </nav>
  <div>
    <h1 class="text-teal-50">Home Page</h1>
    <router-link to="/about">About</router-link>
  </div>
</template>
