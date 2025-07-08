// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import i18n from "@/domain/config/I18n";
import type { AppErrorCode } from "../code/base/AppErrorCode";
import type { ErrorCodeProvider } from "../code/ErrorCodeProvider";

const t = i18n.global.t;

export const AppErrorMessageHandler: Record<AppErrorCode, ErrorCodeProvider> = {
  UNKNOWN: {
    code: "UNKNOWN",
    handle: (param: any): string => {
      if (param instanceof Error) {
        return t("handler.unknown.unexpectedError", { param: param.message });
      }

      if (typeof param === "string") {
        return t("handler.unknown.unknown", { param: param });
      }

      return t("handler.unknown.unidentifiedError");
    },
  },
};
