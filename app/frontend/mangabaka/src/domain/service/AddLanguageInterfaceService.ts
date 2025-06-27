// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
import { AvailableTranslation } from "@/domain/composable/AvailableTranslation";
import i18n from "@/domain/config/I18n";
import type { Types } from "@/export";

export async function AddLanguageInterfaceService(
  code: string,
  label: string,
  translations: Types.I18nJsonFormat
) {
  i18n.global.setLocaleMessage(code, translations);

  if (!AvailableTranslation.value.find((language) => language.code === code)) {
    AvailableTranslation.value.push({ code, label });
  }
}
