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

project(":frontend") {
    val npmCommand = if (isWindows) "npm.cmd" else "npm"

    val npmInstall = tasks.register<Exec>("npmInstall") {
        workingDir = file("mangabaka")
        commandLine(npmCommand, "install")
    }

    val buildVue = tasks.register<Exec>("buildVue") {
        dependsOn(npmInstall)
        workingDir = file("mangabaka")
        commandLine(npmCommand, "run", "build")
    }

    val copyFrontendDist = tasks.register<Copy>("copyFrontendDist") {
        dependsOn(buildVue)
        from("$rootDir/frontend/mangabaka/dist") { include("**/*") }
        into("$rootDir/backend/src/main/webapp")
    }

    tasks.register("buildFrontend") {
        dependsOn(buildVue)
        dependsOn(copyFrontendDist)
    }
}

project(":backend") {
    val generateEnv = tasks.register("generateEnv") {
        doLast {
            val envVars = rootProject.extra["envVars"] as Map<*, *>

            val envContent = """
                PG_DB_NAME_PROD=${envVars["PG_DB_NAME_PROD"]}
                PG_PASSWORD_PROD=${envVars["PG_PASSWORD_PROD"]}
                PG_USERNAME_PROD=${envVars["PG_USERNAME_PROD"]}
                PG_JDBC_URL_PROD=${envVars["PG_JDBC_URL_PROD"]}
                """.trimIndent()

            val dockerEnvFile = file("$rootDir/docker/.env")
            val backendEnvFile = file("$rootDir/backend/.env")

            listOf(dockerEnvFile, backendEnvFile).forEach { file ->
                file.writeText(envContent)
            }

            backendEnvFile.writeText(envContent)
            println("Gerado: ${backendEnvFile.path}")
        }
    }

    tasks.register("buildBackend") {
        dependsOn(":frontend:buildFrontend")
        dependsOn(":backend:build")
        finalizedBy(generateEnv)
    }
}

project(":docker") {
    val dockerCommand = if (isWindows) "docker-compose.exe" else "docker-compose"

    val dockerSetupPostgresql = tasks.register<Exec>("dockerPostgresql") {
        dependsOn(":backend:buildBackend")
        commandLine(
            dockerCommand, "-f", "mangabaka-docker-compose.yml", "-p", "mangabaka", "up", "-d", "postgresql"
        )
    }

    val dockerSetupJettyServer = tasks.register<Exec>("dockerJetty") {
        dependsOn(":backend:buildBackend")
        dependsOn(dockerSetupPostgresql)
        commandLine(
            dockerCommand, "-f", "mangabaka-docker-compose.yml", "-p", "mangabaka", "up", "-d", "jetty"
        )
    }

    tasks.register<Exec>("dockerRecreate") {
        commandLine(
            dockerCommand, "-f", "mangabaka-docker-compose.yml", "-p", "mangabaka", "up", "-d", "--force-recreate"
        )
    }

    tasks.register<Exec>("dockerJettyModules") {
        commandLine("docker", "run", "--rm", "jetty:12.0.22-jdk17", "--list-modules")
    }

    tasks.register("setupDocker") {
        dependsOn(dockerSetupJettyServer)
    }
}

tasks.register("build") {
    dependsOn(":frontend:buildFrontend")
    dependsOn(":backend:buildBackend")
    finalizedBy(":docker:setupDocker")
}

tasks.register("recreate") {
    dependsOn(":frontend:buildFrontend")
    dependsOn(":backend:buildBackend")
    dependsOn(":docker:dockerRecreate")
}

tasks.register("info") {
    dependsOn(":docker:dockerJettyModules")
}
