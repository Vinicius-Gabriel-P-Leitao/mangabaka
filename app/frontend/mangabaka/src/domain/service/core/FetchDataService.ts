// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { Exceptions, Handlers, Types } from "@/export";
import { ArrowPathRoundedSquareIcon } from "@heroicons/vue/24/solid";
import axios from "axios";

/**
 * Faz uma requisição para buscar a tradução JSON.
 *
 * @param url URL do JSON de tradução
 * @returns Os dados da tradução e status HTTP
 * @throws {ApiException} Se a requisição falhar ou a resposta for inválida
 */
export async function FetchDataService<T>(
  url: string
): Promise<Types.ApiResponse<T>> {
  try {
    const response: Types.ApiResponse<T> = await axios.get<T>(url);

    if (!response.status) {
      throw new Exceptions.ApiException(
        Handlers.ApiErrorMessageHandler.NOT_FOUND,
        {
          variant: "error",
          icon: ArrowPathRoundedSquareIcon,
        },
        { status: response.status }
      );
    }

    return {
      data: response.data,
      status: response.status,
      statusText: response.statusText,
    };
  } catch (error) {
    if (axios.isAxiosError(error)) {
      throw new Exceptions.ApiException(
        Handlers.ApiErrorMessageHandler.BAD_GATEWAY,
        {
          variant: "error",
          icon: ArrowPathRoundedSquareIcon,
        },
        error.status
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
