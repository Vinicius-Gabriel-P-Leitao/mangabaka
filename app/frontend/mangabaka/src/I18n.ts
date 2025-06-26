// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { createI18n } from "vue-i18n";
import ptBR from "@/locales/pt-BR.json";
import enUS from "@/locales/en-US.json";

type MessageSchema = typeof ptBR;

const i18n = createI18n<[MessageSchema], "en-US" | "pt-BR">({
  legacy: false,
  locale: "pt-BR",
  fallbackLocale: "en-US",
  messages: {
    "pt-BR": ptBR,
    "en-US": enUS,
  },
});

export default i18n;
