// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { ExclamationCircleIcon } from "@heroicons/vue/24/solid";
import { Types, Exceptions } from "@/export";

// prettier-ignore
export function NormalizeGlobalError(error: unknown): {
  message: string;
  error: Types.GlobalErrorPayload;
  param?: any
} {
  if (error instanceof Exceptions.ApiException || error instanceof Exceptions.ToastException) {
    return {
      message: error.message,
      error: error.error, 
      param: error.param,
    };
  }

  if (error instanceof Exceptions.AppException) {
    return {
      message: error.message,
      error: {
        variant: "alert",
        icon: ExclamationCircleIcon,
      },
      param: error.param
    };
  }

  if (error !== null && typeof error === "object") {
    const errorObj = error as Record<string, unknown>;

    if ("message" in errorObj && isGlobalErrorPayload(errorObj.error)) {
      return {
        message: String(errorObj.message),
        error: errorObj.error as Types.GlobalErrorPayload,
      };
    }

    if ("message" in errorObj) {
      return {
        message: String(errorObj.message),
        error: {
          variant: "alert",
          icon: ExclamationCircleIcon,
        },
      };
    }
  }

  return {
    message: "Erro desconhecido.",
    error: {
      variant: "alert",
      icon: ExclamationCircleIcon,
    },
  };
}

function isGlobalErrorPayload(obj: unknown): obj is Types.GlobalErrorPayload {
  return (
    typeof obj === "object" && obj !== null && "variant" in obj && "icon" in obj
  );
}
