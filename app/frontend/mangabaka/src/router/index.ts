// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.

import { createRouter, createWebHistory } from "vue-router";

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("@/view/Home.vue"),
  },
  {
    path: "/about",
    name: "About",
    component: () => import("@/view/About.vue"),
  },

  // NOTE: Rotas de erros HTTP
  {
    path: "/api-bad-request",
    name: "ApiBadRequest",
    component: () => import("@/view/fallback/BadRequest.vue"),
  },
  {
    path: "/api-bad-gateway",
    name: "ApiBadGateway",
    component: () => import("@/view/fallback/BadGateway.vue"),
  },
  {
    path: "/api-internal-error",
    name: "ApiInternalError",
    component: () => import("@/view/fallback/InternalServerError.vue"),
  },
  // NOTE: Necessário estar em ultimo
  {
    path: "/:pathMatch(.*)*",
    name: "NotFound",
    component: () => import("@/view/fallback/NotFound.vue"),
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
