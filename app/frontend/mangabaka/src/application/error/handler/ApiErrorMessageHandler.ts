// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import type { ApiErrorCode } from "../code/base/ApiErrorCode";
import type { ErrorCodeProvider } from "../code/ErrorCodeProvider";
import i18n from "@/domain/config/I18n";

const t = i18n.global.t;

// prettier-ignore
export const ApiErrorMessageHandler: Record<ApiErrorCode, ErrorCodeProvider> = {
  NOT_FOUND: {
    code: "NOT_FOUND",
    handle: (param: any): string => {
      const recurso = param?.resource ?? t("handler.not_found.resource");
      return `${t("handler.not_found.could_not_find")} ${recurso}.`;
    },
  },
  BAD_REQUEST: {
    code: "BAD_REQUEST",
    handle: (param: any): string => {
      if (param?.field) return `Campo inválido: ${param.field}.`;
      return "A requisição está mal formada. Verifique os dados enviados.";
    },
  },
  BAD_GATEWAY: {
    code: "BAD_GATEWAY",
    handle: (param: any): string => {
      if (param?.status === 502)
        return "Gateway inválido: servidor intermediário falhou.";

      if (param?.status === 504) return "Timeout do servidor de origem.";

      return `Erro ao acessar o servidor intermediário. Código: 
      ${param?.status ?? "desconhecido"}`;
    },
  },
  GATEWAY_TIMEOUT: {
    code: "GATEWAY_TIMEOUT",
    handle: (_param: any): string =>
      "O servidor demorou demais para responder. Tente novamente mais tarde.",
  },
};
