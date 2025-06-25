/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
package br.mangabaka.docker;

import org.gradle.api.Project
import org.gradle.api.tasks.Exec
import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import org.gradle.kotlin.dsl.register

fun Project.configureDockerProduction(composeFile: String, projectName: String) {
    val dockerCommand = if (System.getProperty("os.name").toDefaultLowerCase().contains("windows")) {
        "docker-compose.exe"
    } else {
        "docker-compose"
    }

    val dockerSetupPostgresqlProd = tasks.register<Exec>("dockerSetupPostgresqlProd") {
        commandLine(dockerCommand, "-f", composeFile, "-p", projectName, "up", "-d", "postgresql-prod")
        description = "Inicia o PostgresSQL para desenvolvimento"
    }

    tasks.register<Exec>("setupDockerAllProd") {
        dependsOn(dockerSetupPostgresqlProd)
        commandLine(dockerCommand, "-f", composeFile, "-p", projectName, "up", "-d", "mangabaka-all")
        description = "Inicia o ambiente de produção para somente todo o projeto"
    }

    tasks.register<Exec>("setupDockerApiProd") {
        dependsOn(dockerSetupPostgresqlProd)
        commandLine(dockerCommand, "-f", composeFile, "-p", projectName, "up", "-d", "mangabaka-api")
        description = "Inicia o ambiente de produção para somente api"
    }
}