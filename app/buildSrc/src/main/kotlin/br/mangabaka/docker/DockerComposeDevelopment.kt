/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.docker;

import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import org.gradle.kotlin.dsl.register

fun Project.configureDockerDevelopment(composeFile: String, projectName: String) {
    val dockerCommand = if (System.getProperty("os.name").toDefaultLowerCase().contains("windows")) {
        "docker-compose.exe"
    } else {
        "docker-compose"
    }

    val dockerSetupPostgresqlDev = tasks.register<Exec>("dockerPostgresqlDev") {
        commandLine(dockerCommand, "-f", composeFile, "-p", projectName, "up", "-d", "postgresql-dev")
        description = "Inicia o PostgreSQL para desenvolvimento"
    }

    val dockerSetupJettyServerDev = tasks.register<Exec>("dockerJettyDev") {
        dependsOn(dockerSetupPostgresqlDev)
        commandLine(dockerCommand, "-f", composeFile, "-p", projectName, "up", "-d", "jetty", "--force-recreate")
        description = "Inicia o Jetty para desenvolvimento"
    }

    tasks.register("setupDockerDev") {
        dependsOn(dockerSetupJettyServerDev)
        description = "Configura o ambiente Docker de desenvolvimento"
    }
}