// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import type { ErrorCodeProvider } from "@/application/error/code/ErrorCodeProvider";
import type { Types } from "@/export";
import { AppException } from "../base/AppException";

export class ToastException extends AppException {
  public readonly error: Types.GlobalToastErrorPayload;

  constructor(
    code: ErrorCodeProvider,
    error: Types.GlobalToastErrorPayload,
    param?: any,
    cause?: unknown
  ) {
    super(code, param, cause);
    this.error = error;
    Object.setPrototypeOf(this, ToastException.prototype);
  }
}
