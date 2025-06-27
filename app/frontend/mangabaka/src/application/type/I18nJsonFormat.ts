// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
export type I18nJsonFormat = {
  meta: {
    language: string;
  };
  page: {
    home: {
      title: string;
    };
    not_found: {
      title: string;
    };
  };
  handler: {
    not_found: {
      resource: string;
      could_not_find: string;
    };
  };
};
