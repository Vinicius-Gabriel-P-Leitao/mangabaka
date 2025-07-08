// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
export const ApiErrorCode = {
  NOT_FOUND: "NOT_FOUND",
  BAD_REQUEST: "BAD_REQUEST",
  BAD_GATEWAY: "BAD_GATEWAY",
  GATEWAY_TIMEOUT: "GATEWAY_TIMEOUT",
  INVALID_DATA: "INVALID_DATA",
} as const;

export type ApiErrorCode = keyof typeof ApiErrorCode;
