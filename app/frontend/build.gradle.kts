import org.gradle.internal.extensions.stdlib.toDefaultLowerCase
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Copy

plugins {
    base
}

val npmCommand = if (System.getProperty("os.name").toDefaultLowerCase().contains("windows")
) "npm.cmd" else "npm"
val frontendDir = file("mangabaka")

val npmInstall = tasks.register<Exec>("npmInstall") {
    workingDir = frontendDir
    commandLine(npmCommand, "install")
}

val buildVue = tasks.register<Exec>("buildVue") {
    workingDir = frontendDir
    dependsOn(npmInstall)
    commandLine(npmCommand, "run", "build")
}

val devVue = tasks.register<Exec>("devVue") {
    workingDir = frontendDir
    dependsOn(npmInstall)
    commandLine(npmCommand, "run", "dev")
}

val copyFrontendDist = tasks.register<Copy>("copyFrontendDist") {
    dependsOn(buildVue)
    from("$frontendDir/dist") { include("**/*") }
    into("${rootProject.projectDir}/backend/src/main/webapp")
    onlyIf {
        val distDir = file("$frontendDir/dist")
        if (!distDir.exists()) {
            logger.warn("Diretório $frontendDir/dist não existe. Build do Vue.js falhou?")
            false
        } else {
            true
        }
    }
    doLast {
        val targetDir = file("${rootProject.projectDir}/backend/src/main/webapp")
        println("Arquivos copiados para ${targetDir.absolutePath}:")
        targetDir.listFiles()?.forEach { println("  - ${it.name}") }
    }
    description = "Copia os arquivos de build do Vue.js para o backend"
}


tasks.named("build") {
    dependsOn(copyFrontendDist)
    description = "Build completo do frontend e movido para dentro do backend."
}

tasks.register("dev") {
    dependsOn(devVue)
    description = "Ambiente de desenvolvimento Vue."
}

tasks.named("clean") {
    doFirst {
        val distDir = file("$frontendDir/dist")
        if (distDir.exists()) {
            delete(distDir)
        }

        val webappDir = file("${rootProject.projectDir}/backend/src/main/webapp")
        if (webappDir.exists()) {
            delete(fileTree(webappDir) {
                include("**/*")
            })
        }
    }
    description = "Limpa os artefatos de build do frontend"
}
