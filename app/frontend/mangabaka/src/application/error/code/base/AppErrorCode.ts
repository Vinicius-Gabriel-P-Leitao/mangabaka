// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
export const AppErrorCode = {
  UNKNOWN: "UNKNOWN",
} as const;

export type AppErrorCode = keyof typeof AppErrorCode;
