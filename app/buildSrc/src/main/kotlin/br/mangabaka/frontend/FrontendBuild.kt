/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.frontend

import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.kotlin.dsl.register

fun Project.configureFrontendBuild(frontendDirName: String = "frontend") {
    val frontendDir = projectDir.resolve(frontendDirName)

    val npmCommand = if (System.getProperty("os.name").lowercase().contains("windows")) {
        "npm.cmd"
    } else {
        "npm"
    }

    val npmInstall = tasks.register<Exec>("npmInstall") {
        group = "frontend"
        description = "Instala as dependências do npm para o frontend"
        workingDir = frontendDir
        commandLine = listOf(npmCommand, "install")
        doLast {
            logger.lifecycle("✅ Dependências do npm instaladas em $frontendDir")
        }
    }

    val buildVue = tasks.register<Exec>("buildVue") {
        group = "frontend"
        description = "Faz build do projeto Vue no diretório frontend/$frontendDirName"
        workingDir = frontendDir
        dependsOn(npmInstall)
        commandLine = listOf(npmCommand, "run", "build")
        doLast {
            logger.lifecycle("✅ Build do Vue finalizado em $frontendDir/dist")
        }
    }

    val copyFrontendDist = tasks.register<Copy>("copyFrontendDist") {
        group = "frontend"
        description = "Copia os arquivos do frontend (dist) para o backend/webapp"
        dependsOn(buildVue)
        from(frontendDir.resolve("dist")) {
            include("**/*")
        }
        into(rootProject.projectDir.resolve("backend/src/main/webapp"))
        onlyIf {
            val distDir = frontendDir.resolve("dist")
            if (!distDir.exists()) {
                logger.warn("❌ Diretório $distDir não existe. Build do Vue falhou?")
                false
            } else {
                true
            }
        }
        doLast {
            logger.lifecycle("📦 Arquivos copiados para backend/src/main/webapp:")
            rootProject.projectDir.resolve("backend/src/main/webapp").listFiles()
                ?.forEach { logger.lifecycle("  - ${it.name}") }
        }
    }

    tasks.register("buildFrontendFull") {
        group = "frontend"
        description = "Builda e copia o frontend para o backend"
        dependsOn(buildVue, copyFrontendDist)
    }
}