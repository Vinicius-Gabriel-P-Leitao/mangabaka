// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
import type { Component } from "vue";
import type { ToastVariant } from "./ToastVariant";

export type GlobalToastErrorPayload = {
  message: string;
  variant: ToastVariant;
  icon?: Component;
};
