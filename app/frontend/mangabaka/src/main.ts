// SPDX-License-Identifier: BSD-3-Clause
//
// Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
// Licensed under the BSD 3-Clause License.
// See LICENSE file in the project root for full license information.
import App from "@/App.vue";
import router from "@/domain/config/router";
import i18n from "@/domain/config/I18n";
import "@/style.css";
import { createApp } from "vue";

const app = createApp(App);

app.use(router);
app.use(i18n);
app.mount("#app");
