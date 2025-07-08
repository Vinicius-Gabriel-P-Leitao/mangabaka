// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import i18n from "@/domain/config/I18n";
import { ErrorCodes } from "@/export";
import type { ErrorCodeProvider } from "../code/ErrorCodeProvider";

const t = i18n.global.t;

export const ApiErrorMessageHandler: Record<
  ErrorCodes.ApiErrorCode,
  ErrorCodeProvider
> = {
  NOT_FOUND: {
    code: "NOT_FOUND",
    handle: (param: any): string => {
      const resource = param?.resource ?? t("handler.notFound.resource");
      return t("handler.notFound.couldNotFind", { param: resource });
    },
  },
  BAD_REQUEST: {
    code: "BAD_REQUEST",
    handle: (param: any): string => {
      if (param?.field)
        return t("handler.badRequest.invalidFiel", { param: param.field });

      return t("handler.badRequest.malformedRequest");
    },
  },
  BAD_GATEWAY: {
    code: "BAD_GATEWAY",
    handle: (param: any): string => {
      if (param?.status === 502) return t("handler.badGateway.invalidGateway");

      if (param?.status === 504) return t("handler.badGateway.gatewayTimeout");

      const status = param?.status ?? t("handler.unknown.unknown");
      return t("handler.badGateway.intermediaryServer", { param: status });
    },
  },
  GATEWAY_TIMEOUT: {
    code: "GATEWAY_TIMEOUT",
    handle: (_param: any): string => t("handler.gatewayTimeout.tryAgainLater"),
  },
  INVALID_DATA: {
    code: "INVALID_DATA",
    handle: (param: any): string => {
      const status = param?.status ?? t("handler.unknown.unknown");
      const url = param?.message ?? t("handler.unknown.unknown");

      return t("handler.invalidData.obtainedInvalid", {
        url: url,
        param: status,
      });
    },
  },
};
