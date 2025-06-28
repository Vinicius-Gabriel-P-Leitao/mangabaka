// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import type { AvailableLocales } from "@/application/type/AvailableLocales";
import i18n from "@/domain/config/I18n";
import { Exceptions, Handlers, Services, Types } from "@/export";
import { ExclamationCircleIcon } from "@heroicons/vue/24/solid";

export async function SwitchLanguageInterfaceService(translate: string) {
  const availableLocales = i18n.global.availableLocales;

  if (!availableLocales.includes(translate as AvailableLocales)) {
    try {
      const result: Types.ApiResponse<Types.I18nJsonFormat> =
        await Services.FetchDataService<Types.I18nJsonFormat>(
          `/v1/translate/${translate}` // TODO: Criar rota no backend para essa busca
        );

      if (result.data || result.status === 200) {
        i18n.global.setLocaleMessage(translate, result.data);

        Services.AddLanguageInterfaceService(
          translate,
          result.data?.meta?.language ?? translate,
          result.data
        );
      } else {
        throw new Exceptions.ToastException(
          Handlers.ApiErrorMessageHandler.NOT_FOUND,
          {
            variant: "error",
            icon: ExclamationCircleIcon,
          },
          { status: result.status }
        );
      }
    } catch (exception) {
      if (exception instanceof Exceptions.AppException) {
        throw new Exceptions.ToastException(
          exception.code,
          {
            variant: "error",
            icon: ExclamationCircleIcon,
          },
          { status: exception.param ?? 500 }
        );
      }

      throw new Exceptions.ToastException(
        Handlers.AppErrorMessageHandler.UNKNOWN,
        {
          variant: "error",
          icon: ExclamationCircleIcon,
        },
        { status: 500 }
      );
    }
  }
}
