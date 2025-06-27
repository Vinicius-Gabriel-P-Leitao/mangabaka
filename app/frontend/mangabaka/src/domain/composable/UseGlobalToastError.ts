// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { Types } from "@/export";
import { ref } from "vue";

const globalError = ref<Types.GlobalToastErrorPayload | null>(null);

export function useGlobalToastError() {
  return globalError;
}

export function throwToastError(error: Types.GlobalToastErrorPayload) {
  globalError.value = null;
  setTimeout(() => {
    globalError.value = error;
  }, 10);
}
