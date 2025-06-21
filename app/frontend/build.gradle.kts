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
    group = "frontend"
    workingDir = frontendDir
    commandLine(npmCommand, "install")
}

val buildVue = tasks.register<Exec>("buildVue") {
    group = "frontend"
    workingDir = frontendDir
    dependsOn(npmInstall)
    commandLine(npmCommand, "run", "build")
}

val copyFrontendDist = tasks.register<Copy>("copyFrontendDist") {
    group = "frontend"
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
    description = "Copia os arquivos de build do Vue.js para o backend."
}


tasks.named("build") {
    group = "frontend"
    dependsOn(copyFrontendDist)
    description = "Build completo do frontend e movido para dentro do backend."
}

tasks.named("clean") {
    group = "frontend"
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
