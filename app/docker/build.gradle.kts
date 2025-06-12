import org.gradle.internal.extensions.stdlib.toDefaultLowerCase

plugins {
    base
}
val isWindows = System.getProperty("os.name").toDefaultLowerCase().contains("windows")
val dockerCommand = if (isWindows) "docker-compose.exe" else "docker-compose"

val devComposeFile = "dev/mangabaka-docker-compose-dev.yml"
val prodComposeFile = "prod/mangabaka-docker-compose-prod.yml"
val devProjectName = "mangabaka-dev"
val prodProjectName = "mangabaka-prod"

// NOTE: Tasks para build de ambiente de desenvolvimento
val dockerSetupPostgresqlDev = tasks.register<Exec>("dockerPostgresqlDev") {
    dependsOn(":backend:buildBackend")
    commandLine(dockerCommand, "-f", devComposeFile, "-p", devProjectName, "up", "-d", "postgresql")
    description = "Inicia o PostgreSQL para desenvolvimento"
}

val dockerSetupJettyServerDev = tasks.register<Exec>("dockerJettyDev") {
    dependsOn(":backend:buildBackend", dockerSetupPostgresqlDev)
    commandLine(dockerCommand, "-f", devComposeFile, "-p", devProjectName, "up", "-d", "jetty")
    description = "Inicia o Jetty para desenvolvimento"
}

tasks.register("setupDockerDev") {
    dependsOn(dockerSetupJettyServerDev)
    description = "Configura o ambiente Docker de desenvolvimento"
}

tasks.register<Exec>("dockerRecreateDev") {
    commandLine(dockerCommand, "-f", devComposeFile, "-p", devProjectName, "up", "-d", "--force-recreate")
    description = "Recria o ambiente de produção"
}

// NOTE: Tasks para build de produção
tasks.register<Exec>("setupDockerProd") {
    commandLine(dockerCommand, "-f", prodComposeFile, "-p", prodProjectName, "up", "-d")
    description = "Inicia o ambiente de produção"
}

tasks.register<Exec>("dockerRecreateProd") {
    commandLine(dockerCommand, "-f", prodComposeFile, "-p", prodProjectName, "up", "-d", "--force-recreate")
    description = "Recria o ambiente de produção"
}