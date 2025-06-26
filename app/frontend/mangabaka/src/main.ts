// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import { createApp } from "vue";
import "@/style.css";
import App from "@/App.vue";
import i18n from "@/I18n.ts";
import router from "@/router/index.ts";

const app = createApp(App);
app.use(router);
app.use(i18n);
app.mount("#app");
