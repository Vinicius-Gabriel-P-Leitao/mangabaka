/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.frontend;

import org.gradle.api.Project

fun Project.configureFrontendClean(frontendDirName: String = "frontend") {
    val frontendDir = projectDir.resolve(frontendDirName)

    val cleanFrontend = tasks.register("cleanFrontend") {
        group = "frontend"
        description = "Limpa os arquivos gerados pelo build do frontend"
        doFirst {
            val distDir = frontendDir.resolve("dist")
            if (distDir.exists()) {
                project.delete(distDir)
                logger.lifecycle("🗑️ Diretório $distDir deletado")
            }

            val webappDir = rootProject.projectDir.resolve("backend/src/main/webapp")
            if (webappDir.exists()) {
                project.delete {
                    delete(fileTree(webappDir) { include("**/*") })
                }
                logger.lifecycle("🗑️ Diretório $webappDir limpo")
            }
        }
    }

    tasks.register("clean") {
        dependsOn(cleanFrontend)
    }
}