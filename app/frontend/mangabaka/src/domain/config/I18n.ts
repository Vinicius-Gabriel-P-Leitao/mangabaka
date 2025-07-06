// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { Types } from "@/export";
import { createI18n } from "vue-i18n";

const imports: Record<
  Types.AvailableLocales,
  Record<string, Types.I18nJsonFormat>
> = {
  en_US: import.meta.glob(`@/application/locale/en-US/*.json`, {
    eager: true,
    import: "default",
  }),
  pt_BR: import.meta.glob(`@/application/locale/pt-BR/*.json`, {
    eager: true,
    import: "default",
  }),
};

function getLocaleMessages(): Record<
  Types.AvailableLocales,
  Types.I18nJsonFormat
> {
  const locales = Object.keys(imports) as (keyof typeof imports)[];

  return locales.reduce((messages, locale) => {
    const merged = Object.values(imports[locale]).reduce(
      (acc, current) => Object.assign(acc, current as Types.I18nJsonFormat),
      {} as Types.I18nJsonFormat
    );

    return { ...messages, [locale]: merged };
  }, {} as Record<Types.AvailableLocales, Types.I18nJsonFormat>);
}

const i18n = createI18n<[Types.I18nJsonFormat], Types.AvailableLocales>({
  legacy: false,
  locale: "pt_BR",
  fallbackLocale: "en_US",
  messages: getLocaleMessages() || [],
});

export default i18n;
