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
    PG_DB_NAME=${props.getProperty("${prefix}_DB_NAME") ?: error("Propriedade ${prefix}_DB_NAME n?o encontrada em local.properties")}
    PG_PASSWORD=${props.getProperty("${prefix}_PASSWORD") ?: error("Propriedade ${prefix}_PASSWORD n?o encontrada em local.properties")}
    PG_USERNAME=${props.getProperty("${prefix}_USERNAME") ?: error("Propriedade ${prefix}_USERNAME n?o encontrada em local.properties")}
    PG_JDBC_URL=${props.getProperty("${prefix}_JDBC_URL") ?: error("Propriedade ${prefix}_JDBC_URL n?o encontrada em local.properties")}
    """.trimIndent()

val generateEnvProd =
    tasks.register("generateEnvProd") {
        doLast {
            val content = generateEnvContent("PG_PROD")
            val envFile = file("$rootDir/docker/prod/.env")
            envFile.writeText(content)
            println("? Arquivo .env de PRODU??O gerado em ${envFile.absolutePath}")
        }
    }

val generateEnvDev =
    tasks.register("generateEnvDev") {
        doLast {
            val content = generateEnvContent("PG_DEV")
            listOf(
                file("$rootDir/docker/dev/.env"),
                file("$rootDir/backend/.env"),
            ).forEach {
                it.writeText(content)
                println("? Arquivo .env de DESENVOLVIMENTO gerado em ${it.absolutePath}")
            }
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
    description = "Limpa os artefatos de build do frontend e backend"
}

tasks.register("recreate-dev") {
    dependsOn("clean")
    dependsOn(generateEnvDev)
    dependsOn(":backend:buildBackend")
    dependsOn(":docker:dockerRecreateDev")
    description = "Limpa os builds e recria o ambiente de desenvolvimento via Docker"
}

tasks.register("recreate-prod") {
    dependsOn("clean")
    dependsOn(generateEnvProd)
    dependsOn(":docker:dockerRecreateProd")
    description = "Limpa os builds e recria o ambiente de produ??o via Docker"
}

