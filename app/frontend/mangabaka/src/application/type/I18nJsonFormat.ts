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
  component: {
    plugin_translation: {
      info_view: string;
    };
  };
  handler: {
    unknown: {
      unknown: string;
      unexpected_error: string;
      unidentified_error: string;
    };
    not_found: {
      resource: string;
      could_not_find: string;
    };
    bad_request: {
      invalid_fiel: string;
      malformed_request: string;
    };
    bad_gateway: {
      invalid_gateway: string;
      gateway_timeout: string;
      intermediary_server: string;
    };
    gateway_timeout: {
      try_again_later: string;
    };
    invalid_data: {
      obtained_invalid: string;
    };
  };
};
