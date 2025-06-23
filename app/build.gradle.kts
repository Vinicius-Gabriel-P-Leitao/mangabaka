/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

import java.util.Properties

subprojects {
    repositories {
        mavenCentral()
    }
}

val props = Properties()
val localPropsFile = file("local.properties")

if (!localPropsFile.exists()) {
    error("Arquivo local.properties n?o encontrado na raiz do projeto!")
}

localPropsFile.inputStream().use { props.load(it) }

fun generateEnvContent(prefix: String): String =
    """
    BACKEND_MODE=${props.getProperty("${prefix}_BACKEND_MODE") ?: error("Propriedade ${prefix}BACKEND_MODE não encontrada em local.properties") }
    PG_DB_NAME=${props.getProperty("${prefix}_DB_NAME") ?: error("Propriedade ${prefix}_DB_NAME não encontrada em local.properties")}
    PG_PASSWORD=${props.getProperty("${prefix}_PASSWORD") ?: error("Propriedade ${prefix}_PASSWORD não encontrada em local.properties")}
    PG_USERNAME=${props.getProperty("${prefix}_USERNAME") ?: error("Propriedade ${prefix}_USERNAME não encontrada em local.properties")}
    PG_JDBC_URL=${props.getProperty("${prefix}_JDBC_URL") ?: error("Propriedade ${prefix}_JDBC_URL não encontrada em local.properties")}
    """.trimIndent()

val generateEnvProd =
    tasks.register("generateEnvProd") {
        doLast {
            val content = generateEnvContent("PROD")
            val envFile = file("$rootDir/docker/prod/.env")
            envFile.writeText(content)
            println("? Arquivo .env de PRODUÇÃO gerado em ${envFile.absolutePath}")
        }
    }

val generateEnvDev =
    tasks.register("generateEnvDev") {
        doLast {
            val content = generateEnvContent("DEV")
            val envFile = file("$rootDir/docker/dev/.env")
            envFile.writeText(content)
            println("? Arquivo .env de DESENVOLVIMENTO gerado em ${envFile.absolutePath}")
        }
    }

// Project tasks
project(":frontend") {
    tasks.register("buildFrontend") {
        dependsOn(":frontend:build")
        description = "Build do frontend com Vue.js"
    }
}

project(":backend") {
    tasks.register("buildBackend") {
        dependsOn(":frontend:buildFrontend")
        dependsOn(":frontend:copyFrontendDist")
        dependsOn(":backend:build")
        description = "Build do backend com c?pia do frontend"
    }
}

project(":docker") {
    tasks.register("prod") {
        dependsOn(generateEnvProd)
        dependsOn(":docker:setupDockerProd")
    }

    tasks.register("dev") {
        dependsOn(generateEnvDev)
        dependsOn(":frontend:buildFrontend")
        dependsOn(":frontend:copyFrontendDist")
        dependsOn(":backend:buildBackend")
        dependsOn(":docker:setupDockerDev")
    }
}

tasks.register("build") {
    dependsOn(":docker:prod")
    description = "Sobe o ambiente de produ??o (build feito dentro do container)"
}

tasks.register("dev") {
    dependsOn(":docker:dev")
    description = "Build completo para desenvolvimento (build local + docker dev)"
}

tasks.register("clean") {
    dependsOn(":frontend:clean")
    dependsOn(":backend:clean")
    delete("${rootProject.projectDir}/docker/dev/.env")
    delete("${rootProject.projectDir}/docker/prod/.env")
    description = "Limpa os artefatos de build do frontend e backend"
}

