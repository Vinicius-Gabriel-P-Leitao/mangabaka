/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) {year} Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */

import br.mangabaka.docker.configureDockerDevelopment
import br.mangabaka.docker.configureDockerProduction
import br.mangabaka.frontend.configureFrontendBuild
import br.mangabaka.frontend.configureFrontendClean
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

fun generateEnvContent(prefix: String): String = """
    APP_LOCALE=${props.getProperty("${prefix}_APP_LOCALE") ?: error("Propriedade ${prefix}_APP_LOCALE não encontrada em local.properties")}
    BACKEND_MODE=${props.getProperty("${prefix}_BACKEND_MODE") ?: error("Propriedade ${prefix}_BACKEND_MODE não encontrada em local.properties")}
    PG_DB_NAME=${props.getProperty("${prefix}_DB_NAME") ?: error("Propriedade ${prefix}_DB_NAME não encontrada em local.properties")}
    PG_PASSWORD=${props.getProperty("${prefix}_PASSWORD") ?: error("Propriedade ${prefix}_PASSWORD não encontrada em local.properties")}
    PG_USERNAME=${props.getProperty("${prefix}_USERNAME") ?: error("Propriedade ${prefix}_USERNAME não encontrada em local.properties")}
    PG_JDBC_URL=${props.getProperty("${prefix}_JDBC_URL") ?: error("Propriedade ${prefix}_JDBC_URL não encontrada em local.properties")}
""".trimIndent()

val generateEnvProd = tasks.register("generateEnvProd") {
    doLast {
        val content = generateEnvContent("PROD")
        val envFile = file("$rootDir/docker/production/.env")

        envFile.writeText(content)
        println("? Arquivo .env de PRODUÇÃO gerado em ${envFile.absolutePath}")
    }
}

val generateEnvDev = tasks.register("generateEnvDev") {
    doLast {
        val content = generateEnvContent("DEV")
        val envFile = file("$rootDir/docker/development/.env")

        envFile.writeText(content)
        println("? Arquivo .env de DESENVOLVIMENTO gerado em ${envFile.absolutePath}")
    }
}

project(":frontend") {
    configureFrontendBuild("mangabaka")
    configureFrontendClean("mangabaka")

    tasks.register("build") {
        dependsOn(":frontend:buildFrontendFull")
        description = "Build do frontend com Vue.js"
    }
}

project(":backend") {
    tasks.register("buildBackend") {
        dependsOn(":frontend:build")
        dependsOn(tasks.named("build"), tasks.named("war"))
        description = "Build do backend com c?pia do frontend"
    }
}

project(":docker") {
    configureDockerProduction("production/mangabaka-docker-compose-prod.yml", "mangabaka-prod")

    tasks.register("prod") {
        val backendMode = props.getProperty("PROD_BACKEND_MODE") ?: "ALL"
        dependsOn(generateEnvProd)

        if (backendMode == "ALL") {
            dependsOn(":docker:setupDockerAllProd")
        }

        if (backendMode == "API") {
            dependsOn(":docker:setupDockerApiProd")
        }
    }

    configureDockerDevelopment("development/mangabaka-docker-compose-dev.yml", "mangabaka-dev")

    tasks.register("dev") {
        dependsOn(generateEnvDev)
        dependsOn(":backend:buildBackend")
        dependsOn(":docker:setupDockerDev")
    }
}

tasks.register("prod") {
    dependsOn(":docker:prod")
    description = "Sobe o ambiente de produção (build feito dentro do container)"
}

tasks.register("dev") {
    dependsOn(":docker:dev")
    description = "Build completo para desenvolvimento (build local + docker dev)"
}

tasks.register("clean") {
    dependsOn(":backend:clean")
    dependsOn(":frontend:clean")

    delete("${rootProject.projectDir}/docker/dev/.env")
    delete("${rootProject.projectDir}/docker/prod/.env")
    description = "Limpa os artefatos de build do frontend e backend"
}

