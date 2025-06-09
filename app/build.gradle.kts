import java.util.Properties
import org.gradle.internal.extensions.stdlib.toDefaultLowerCase

subprojects {
    repositories {
        mavenCentral()
    }
}
val isWindows = System.getProperty("os.name").toDefaultLowerCase().contains("windows")
val props = Properties()
val localPropsFile = file("local.properties")

if (localPropsFile.exists()) {
    localPropsFile.inputStream().use { props.load(it) }
} else {
    error("Arquivo local.properties não encontrado na raiz do projeto!")
}

extra["envVars"] = mapOf(
    "PG_DB_NAME_PROD" to props.getProperty("PG_DB_NAME_PROD"),
    "PG_PASSWORD_PROD" to props.getProperty("PG_PASSWORD_PROD"),
    "PG_USERNAME_PROD" to props.getProperty("PG_USERNAME_PROD"),
    "PG_JDBC_URL_PROD" to props.getProperty("PG_JDBC_URL_PROD")
)

tasks.register("generateEnv") {
    doLast {
        val envVars = rootProject.extra["envVars"] as Map<*, *>

        val envContent =
            """
            PG_DB_NAME_PROD=${envVars["PG_DB_NAME_PROD"]}
            PG_PASSWORD_PROD=${envVars["PG_PASSWORD_PROD"]}
            PG_USERNAME_PROD=${envVars["PG_USERNAME_PROD"]}
            PG_JDBC_URL_PROD=${envVars["PG_JDBC_URL_PROD"]}
            """.trimIndent()

        val dockerEnvFile = file("$rootDir/docker/.env")
        val backendEnvFile = file("$rootDir/backend/.env")

        listOf(dockerEnvFile, backendEnvFile).forEach { file ->
            file.writeText(envContent)
            println("Gerado: ${file.path}")
        }
    }
}

project(":docker") {
    // NOTE: docker precisa tá instalado e configurado no path
    val dockerCommand = if (isWindows) "docker-compose.exe" else "docker-compose"

    val dockerSetupPostgresql = tasks.register<Exec>("dockerPostgresql") {
        dependsOn(":generateEnv")
        commandLine(
            dockerCommand, "-f", "mangabaka-docker-compose.yml", "-p", "mangabaka", "up", "-d", "postgresql"
        )
    }

    val dockerSetupJettyServer = tasks.register<Exec>("dockerJetty") {
        dependsOn(dockerSetupPostgresql)
        commandLine(
            dockerCommand, "-f", "mangabaka-docker-compose.yml", "-p", "mangabaka", "up", "-d", "jetty"
        )
    }

    tasks.register<Exec>("dockerJettyModules") {
        commandLine("docker", "run", "--rm", "jetty:12.0.22-jdk17", "--list-modules")
    }

    tasks.register("setupDocker") {
        dependsOn(dockerSetupJettyServer)
    }
}

project(":frontend") {
    // NOTE: npm tem que estar no path para poder dar certo.
    val npmCommand = if (isWindows) "npm.cmd" else "npm"

    val npmInstall = tasks.register<Exec>("npmInstall") {
        workingDir = file("mangabaka")
        commandLine(npmCommand, "-v")
    }

    val buildFrontend = tasks.register<Exec>("buildVue") {
        dependsOn(npmInstall)
        workingDir = file("mangabaka")
        commandLine(npmCommand, "run", "build")
    }

    tasks.register("buildFrontend") {
        dependsOn(buildFrontend)
    }
}

project(":backend") {
    tasks.register("buildBackend") {
        dependsOn(rootProject.tasks.named("generateEnv"))
        dependsOn(tasks.named("build"))
    }
}

tasks.register("build") {
    dependsOn(":backend:buildBackend")
    dependsOn(":frontend:buildFrontend")
    finalizedBy(":docker:setupDocker")
}

tasks.register("info") {
    dependsOn(":docker:dockerJettyModules")
}
