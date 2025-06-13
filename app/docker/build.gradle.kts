import org.gradle.internal.extensions.stdlib.toDefaultLowerCase

plugins {
    base
}

val dockerCommand = if (System.getProperty("os.name").toDefaultLowerCase().contains("windows")
) "docker-compose.exe" else "docker-compose"

val devComposeFile = "dev/mangabaka-docker-compose-dev.yml"
val devProjectName = "mangabaka-dev"

val prodComposeFile = "prod/mangabaka-docker-compose-prod.yml"
val prodProjectName = "mangabaka-prod"

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

// NOTE: Setup dev compila o código do backend + frontend e joga dentro do jetty via volume
tasks.register("setupDockerDev") {
    dependsOn(dockerSetupJettyServerDev)
    description = "Configura o ambiente Docker de desenvolvimento"
}

tasks.register<Exec>("dockerRecreateDev") {
    commandLine(dockerCommand, "-f", devComposeFile, "-p", devProjectName, "up", "-d", "--force-recreate")
    description = "Recria o ambiente de desenvolvimento"
    doFirst {
        println("Recriando containers Docker para desenvolvimento...")
    }
}

// NOTE: Ambiente de produção leva todo o código para o container e compila lá dentro
tasks.register<Exec>("setupDockerProd") {
    commandLine(dockerCommand, "-f", prodComposeFile, "-p", prodProjectName, "up", "-d")
    description = "Inicia o ambiente de produção"
}

tasks.register<Exec>("dockerRecreateProd") {
    commandLine(dockerCommand, "-f", prodComposeFile, "-p", prodProjectName, "up", "-d", "--force-recreate")
    description = "Recria o ambiente de produção"
    doFirst {
        println("Recriando containers Docker para produção...")
    }
}