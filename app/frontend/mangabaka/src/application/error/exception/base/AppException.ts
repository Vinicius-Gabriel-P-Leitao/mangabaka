// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import type { ErrorCodeProvider } from "../../code/ErrorCodeProvider";

export class AppException extends Error {
  public readonly code: ErrorCodeProvider;
  public readonly cause?: unknown;
  public readonly param?: any;

  constructor(code: ErrorCodeProvider, param?: any, cause?: unknown) {
    super(code.handle(param));
    this.code = code;
    this.cause = cause;
  }
}
