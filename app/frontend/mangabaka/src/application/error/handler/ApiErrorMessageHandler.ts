// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import type { ApiErrorCode } from "../code/base/ApiErrorCode";
import type { ErrorCodeProvider } from "../code/ErrorCodeProvider";

export const ApiErrorMessageHandler: Record<ApiErrorCode, ErrorCodeProvider> = {
  NOT_FOUND: {
    code: "NOT_FOUND",
    handle: (param) => `Não foi possível encontrar: ${JSON.stringify(param)}.`,
  },
  BAD_REQUEST: {
    code: "BAD_REQUEST",
    handle: (param) => `Request mal formado: ${JSON.stringify(param)}.`,
  },
  BAD_GATEWAY: {
    code: "BAD_GATEWAY",
    handle: (param) =>
      `O gateway da requisição falhou: ${JSON.stringify(param)}.`,
  },
  GATEWAY_TIMEOUT: {
    code: "GATEWAY_TIMEOUT",
    handle: (param) => `Timeout do gateway: ${JSON.stringify(param)}.`,
  },
};
