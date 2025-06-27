// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import i18n from "@/domain/config/I18n";
import { ErrorCodes } from "@/export";
import type { ErrorCodeProvider } from "../code/ErrorCodeProvider";

const t = i18n.global.t;

// prettier-ignore
export const ApiErrorMessageHandler: Record<
  ErrorCodes.ApiErrorCode,
  ErrorCodeProvider
> = {
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
      if (param?.field)
        return `${t("handler.bad_request.invalid_fiel")} ${param.field}.`;
      return t("handler.bad_request.malformed_request");
    },
  },
  BAD_GATEWAY: {
    code: "BAD_GATEWAY",
    handle: (param: any): string => {
      if (param?.status === 502)
        return t("handler.bad_gateway.invalid_gateway");

      if (param?.status === 504)
        return t("handler.bad_gateway.gateway_timeout");

      return `${t("handler.bad_gateway.intermediary_server")} ${
        param?.status ?? t("handler.unknown.unknown")
      }`;
    },
  },
  GATEWAY_TIMEOUT: {
    code: "GATEWAY_TIMEOUT",
    handle: (_param: any): string =>
      t("handler.gateway_timeout.try_again_later"),
  },
  INVALID_DATA: {
    code: "INVALID_DATA",
    handle: (param: any): string => {
      return `${t("handler.invalid_data.obtained_invalid", {
        param: param?.message,
      })} ${param?.status ?? t("handler.unknown.unknown")}`;
    },
  },
};
