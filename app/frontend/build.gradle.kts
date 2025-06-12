import org.gradle.internal.extensions.stdlib.toDefaultLowerCase

plugins {
    base
}

val npmCommand = if (System.getProperty("os.name").toDefaultLowerCase().contains("windows")) "npm.cmd" else "npm"
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
    from("$frontendDir/dist") { include("**/*") }
    into("${rootProject.projectDir}/backend/src/main/webapp")
    description = "Copia os arquivos de build do Vue.js para o backend"
}

tasks.named("build") {
    dependsOn("buildVue", "copyFrontendDist")
    description = "Build completo do frontend"
}

tasks.named("clean") {
    doFirst {
        delete("$frontendDir/dist")
        delete("${rootProject.projectDir}/backend/src/main/webapp")
    }
    description = "Limpa os artefatos de build do frontend"
}