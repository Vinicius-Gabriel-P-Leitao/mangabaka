// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { ExclamationCircleIcon } from "@heroicons/vue/24/solid";
import type { Types } from "@/export";

export function NormalizeGlobalError(error: unknown): {
  message: string;
  error: Types.GlobalErrorPayload;
} {
  if (error && typeof error === "object") {
    if (
      "message" in error &&
      "error" in error &&
      typeof error.error === "object"
    ) {
      return {
        message: String((error as any).message),
        error: (error as any).error as Types.GlobalErrorPayload,
      };
    }

    if ("message" in error) {
      return {
        message: String((error as any).message),
        error: {
          variant: "error",
          icon: ExclamationCircleIcon,
        },
      };
    }
  }

  return {
    message: "Erro desconhecido.",
    error: {
      variant: "error",
      icon: ExclamationCircleIcon,
    },
  };
}
