// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
import type { ApiResponse } from "@/application/type/ApiResponse";

export async function FetchTranslateJson<T>(
  url: string
): Promise<ApiResponse<T>> {
  const response: Response = await fetch(url);
  if (!response.ok) {
    throw new Error(`Erro ao carregar tradução ${response.status}`);
  }

  const json: T = await response.json();

  return {
    data: json,
    status: response.status,
    statusText: response.statusText,
  };
}
