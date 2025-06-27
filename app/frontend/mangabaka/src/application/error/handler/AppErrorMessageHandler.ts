// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import type { AppErrorCode } from "../code/base/AppErrorCode";
import type { ErrorCodeProvider } from "../code/ErrorCodeProvider";

export const AppErrorMessageHandler: Record<AppErrorCode, ErrorCodeProvider> = {
  UNKNOWN: {
    code: "UNKNOWN",
    handle: (param: any): string => {
      if (param instanceof Error) {
        return `Ocorreu um erro inesperado: ${param.message}`;
      }

      if (typeof param === "string") {
        return `Erro desconhecido: ${param}`;
      }

      if (typeof param === "object" && param?.status) {
        return `Erro inesperado (código ${param.status}).`;
      }

      return "Algo deu errado, mas não conseguimos identificar o erro.";
    },
  },
};
