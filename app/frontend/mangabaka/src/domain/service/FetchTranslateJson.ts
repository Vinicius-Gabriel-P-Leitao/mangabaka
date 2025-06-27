// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
import { Exceptions, Handlers, Types } from "@/export";
import { ArrowPathRoundedSquareIcon } from "@heroicons/vue/24/solid";

/**
 * Faz uma requisição para buscar a tradução JSON.
 *
 * @param url URL do JSON de tradução
 * @returns Os dados da tradução e status HTTP
 * @throws {ApiException} Se a requisição falhar ou a resposta for inválida
 */
export async function FetchTranslateJson<T>(
  url: string
): Promise<Types.ApiResponse<T>> {
  try {
    const response: Response = await fetch(url);

    if (!response.ok) {
      throw new Exceptions.ApiException(
        Handlers.ApiErrorMessageHandler.NOT_FOUND,
        {
          variant: "error",
          icon: ArrowPathRoundedSquareIcon,
        },
        { status: response.status }
      );
    }

    const json: T = await response.json();

    return {
      data: json,
      status: response.status,
      statusText: response.statusText,
    };
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Exceptions.ApiException(
        Handlers.ApiErrorMessageHandler.BAD_GATEWAY,
        {
          variant: "alert",
          icon: ArrowPathRoundedSquareIcon,
        },
        error
      );
    }

    throw new Exceptions.ApiException(
      Handlers.AppErrorMessageHandler.UNKNOWN,
      {
        variant: "error",
        icon: ArrowPathRoundedSquareIcon,
      },
      error
    );
  }
}
