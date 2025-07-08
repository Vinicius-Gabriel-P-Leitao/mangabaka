// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { onMounted, onUnmounted } from "vue";
import { useI18n } from "vue-i18n";

export function UseLocaleSync() {
  const { locale } = useI18n();
  const channel = new BroadcastChannel("locale-change");

  channel.onmessage = (event) => {
    if (locale.value !== event.data) {
      locale.value = event.data;
    }
  };

  onMounted(() => {
    const saved = localStorage.getItem("locale");
    if (saved && saved !== locale.value) {
      locale.value = saved;
    }
  });

  onUnmounted(() => {
    channel.close();
  });
}
