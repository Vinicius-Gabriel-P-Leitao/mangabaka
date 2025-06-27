// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
export type ApiResponse<T> = {
  data: T;
  status: number;
  statusText: string;
};
