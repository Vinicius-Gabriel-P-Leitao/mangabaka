// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { createRouter, createWebHistory } from "vue-router";
import { Fallbacks, Interfaces } from "@/export";

const routes = [
  {
    path: "/",
    name: "Home",
    component: Interfaces.Home,
  },
  {
    path: "/about",
    name: "About",
    component: Interfaces.About,
  },
  // NOTE: Rotas de erros HTTP
  {
    path: "/api-bad-request",
    name: "ApiBadRequest",
    component: Fallbacks.BadRequest,
  },
  {
    path: "/api-bad-gateway",
    name: "ApiBadGateway",
    component: Fallbacks.BadGateway,
  },
  {
    path: "/api-gateway-timeout",
    name: "ApiGatewayTimeout",
    component: Fallbacks.GatewayTimeout,
  },
  {
    path: "/api-internal-error",
    name: "ApiInternalError",
    component: Fallbacks.InternalServerError,
  },
  {
    path: "/api-conflict",
    name: "ApiConflict",
    component: Fallbacks.Conflict,
  },
  // NOTE: Necessário estar em ultimo
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: Fallbacks.NotFound,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
