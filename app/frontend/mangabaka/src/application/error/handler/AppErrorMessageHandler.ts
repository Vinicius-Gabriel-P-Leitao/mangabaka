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
    handle: (param) => `Erro desconhecido: ${JSON.stringify(param)}.`,
  },
};
