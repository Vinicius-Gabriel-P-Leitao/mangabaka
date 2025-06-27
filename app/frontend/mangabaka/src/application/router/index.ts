// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { createRouter, createWebHistory } from "vue-router";
import { About, Home } from "@/export/Interface";
import {
  BadGateway,
  BadRequest,
  GatewayTimeout,
  InternalServerError,
  NotFound,
} from "@/export/Fallback";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Home,
  },
  {
    path: "/about",
    name: "About",
    component: About,
  },
  // NOTE: Rotas de erros HTTP
  {
    path: "/api-bad-request",
    name: "ApiBadRequest",
    component: BadRequest,
  },
  {
    path: "/api-bad-gateway",
    name: "ApiBadGateway",
    component: BadGateway,
  },
  {
    path: "/api-gateway-timeout",
    name: "ApiGatewayTimeout",
    component: GatewayTimeout,
  },
  {
    path: "/api-internal-error",
    name: "ApiInternalError",
    component: InternalServerError,
  },
  // NOTE: Necessário estar em ultimo
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: NotFound,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
