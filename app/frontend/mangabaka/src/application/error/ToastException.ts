// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
import type { GlobalErrorPayload } from "@/export/Type";

export class ToastException extends Error {
  error: GlobalErrorPayload;

  constructor(message: string, error: GlobalErrorPayload) {
    super(message);
    this.error = error;
    Object.setPrototypeOf(this, ToastException.prototype);
  }
}
