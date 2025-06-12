import java.util.Properties

subprojects {
    repositories {
        mavenCentral()
    }
}

val props = Properties()
val localPropsFile = file("local.properties")

if (!localPropsFile.exists()) {
    error("Arquivo local.properties não encontrado na raiz do projeto!")
}

localPropsFile.inputStream().use { props.load(it) }

fun generateEnvContent(prefix: String): String {
    return """
        PG_DB_NAME=${props.getProperty("${prefix}_DB_NAME")}
        PG_PASSWORD=${props.getProperty("${prefix}_PASSWORD")}
        PG_USERNAME=${props.getProperty("${prefix}_USERNAME")}
        PG_JDBC_URL=${props.getProperty("${prefix}_JDBC_URL")}
            """.trimIndent()
}

val generateEnvProd = tasks.register("generateEnvProd") {
    doLast {
        val content = generateEnvContent("PG_PROD")
        file("$rootDir/docker/prod/.env").writeText(content)
        println("✅ Arquivo .env de PRODUÇÃO gerado!")
    }
}

val generateEnvDev = tasks.register("generateEnvDev") {
    doLast {
        val content = generateEnvContent("PG_DEV")
        listOf(
            file("$rootDir/docker/dev/.env"), file("$rootDir/backend/.env")
        ).forEach {
            it.writeText(content)
        }
        println("✅ Arquivos .env de DESENVOLVIMENTO gerados!")
    }
}

// Project tasks
project(":frontend") {
    tasks.register("buildFrontend") {
        dependsOn(generateEnvDev)
        dependsOn(":frontend:build")
    }
}

project(":backend") {
    tasks.register("buildBackend") {
        dependsOn(generateEnvDev)
        dependsOn(":frontend:buildFrontend")
        dependsOn(":backend:build")
    }
}

project(":docker") {
    // --- DEV: Build backend/frontend local + sobe docker dev ---
    tasks.register("dev") {
        dependsOn(generateEnvDev)
        dependsOn(":frontend:buildFrontend")
        dependsOn(":backend:buildBackend")
        dependsOn(":docker:setupDockerDev")
        description = "Build completo para desenvolvimento (build local + docker dev)"
    }

    // --- PROD: Sobe docker prod que já build dentro do container, sem build local ---
    tasks.register("prod") {
        dependsOn(generateEnvProd)
        dependsOn(":docker:setupDockerProd")
        description = "Sobe o ambiente de produção (build feito dentro do container)"
    }
}

// --- Aliás, para fazer um Build mais fácil

tasks.register("dev") {
    dependsOn(generateEnvDev)
    dependsOn(":docker:dev")
    description = "Alias para dev (devesenvolvimento via Docker)"
}

tasks.register("build") {
    dependsOn(generateEnvProd)
    dependsOn(":docker:prod")
    description = "Alias para prod (produção via Docker)"
}

tasks.register("clean") {
    dependsOn(":frontend:clean")
    dependsOn(":backend:clean")
    description = "Limpa o build do front e backend."
}

tasks.register("recreate-dev") {
    dependsOn("clean")
    dependsOn(":frontend:buildFrontend")
    dependsOn(":backend:buildBackend")
    dependsOn(":docker:dockerRecreateDev")
    description = "Limpa o backend e frontend e recria os containers docker."
}

tasks.register("recreate-prod") {
    dependsOn(generateEnvProd)
    dependsOn(":docker:dockerRecreateProd")
}

