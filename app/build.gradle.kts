import org.gradle.internal.extensions.stdlib.toDefaultLowerCase

subprojects {
    repositories {
        mavenCentral()
    }
}
val isWindows = System.getProperty("os.name").toDefaultLowerCase().contains("windows")

project(":docker") {
    val dockerCommand = if (isWindows) "docker-compose.exe" else "docker-compose"

    val dockerSetupHttpServer = tasks.register<Exec>("dockerJetty") {
        commandLine(
            dockerCommand, "-f", "mangabaka-docker-compose.yml", "-p", "mangabaka", "up", "-d", "jetty"
        )
    }

    tasks.register("setupDocker") {
        dependsOn(dockerSetupHttpServer)
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
        dependsOn(tasks.named("build"))
    }
}

tasks.register("build") {
    dependsOn(":backend:buildBackend")
    dependsOn(":frontend:buildFrontend")
    finalizedBy(":docker:setupDocker")
}
