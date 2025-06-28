// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { ExclamationCircleIcon } from "@heroicons/vue/24/solid";
import { AvailableTranslation } from "../../composable/AvailableTranslation";
import { Services, Types, Handlers, Exceptions } from "@/export";

export async function FetchCustomTranslations() {
  try {
    const response = await Services.FetchDataService<
      Types.AvailableTranslation[]
    >("/v1/translate/list");

    if (!response.data || !Array.isArray(response.data)) {
      throw new Exceptions.ToastException(
        Handlers.ApiErrorMessageHandler.INVALID_DATA,
        {
          variant: "alert",
          icon: ExclamationCircleIcon,
        },
        {
          message: "/v1/translate/list",
          status: response.status,
        }
      );
    }

    response.data.forEach((lang) => {
      if (
        !AvailableTranslation.value.find((value) => value.code === lang.code)
      ) {
        AvailableTranslation.value.push(lang);
      }
    });
  } catch (error) {
    throw error;
  }
}
