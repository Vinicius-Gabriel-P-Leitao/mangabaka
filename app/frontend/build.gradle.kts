import org.gradle.internal.extensions.stdlib.toDefaultLowerCase

plugins {
    base
}

val isWindows = System.getProperty("os.name").toDefaultLowerCase().contains("windows")
val npmCommand = if (isWindows) "npm.cmd" else "npm"

val frontendDir = file("mangabaka")

tasks.register<Exec>("npmInstall") {
    workingDir = frontendDir
    commandLine(npmCommand, "install")
    description = "Instala as dependências do Vue.js"
}

tasks.register<Exec>("buildVue") {
    dependsOn("npmInstall")
    workingDir = frontendDir
    commandLine(npmCommand, "run", "build")
    description = "Executa o build do Vue.js"
}

tasks.register<Copy>("copyFrontendDist") {
    dependsOn("buildVue")
    from("$frontendDir/dist") {
        include("**/*")
    }
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
    dependsOn("copyFrontendDist")
    description = "Build completo do frontend"
}

tasks.named("clean") {
    delete("$frontendDir/dist")
    delete(fileTree("${rootProject.projectDir}/backend/src/main/webapp") {
        include("**/*")
    })
    description = "Limpa os artefatos de build do frontend"
}