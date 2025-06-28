// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
export type I18nJsonFormat = {
  meta: {
    language: string;
  };
  page: {
    notFound: {
      title: string;
      message: string;
      cause: string;
      imageAlt: string;
    };
    internalServerError: {
      title: string;
      message: string;
      cause: string;
      imageAlt: string;
    };
    gatewayTimeout: {
      title: string;
      message: string;
      cause: string;
      imageAlt: string;
    };
    badRequest: {
      title: string;
      message: string;
      cause: string;
      imageAlt: string;
    };
    badGateway: {
      title: string;
      message: string;
      cause: string;
      imageAlt: string;
    };
    home: {
      title: string;
    };
  };
  component: {
    translation: {
      infoView: string;
    };
    select: {
      label: string;
    };
  };
  handler: {
    unknown: {
      unknown: string;
      unexpectedError: string;
      unidentifiedError: string;
    };
    notFound: {
      resource: string;
      couldNotFind: string;
    };
    badRequest: {
      invalidFiel: string;
      malformedRequest: string;
    };
    badGateway: {
      invalidGateway: string;
      gatewayTimeout: string;
      intermediaryServer: string;
    };
    gatewayTimeout: {
      tryAgainLater: string;
    };
    invalidData: {
      obtainedInvalid: string;
    };
  };
};
