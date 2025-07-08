/*
 * SPDX-License-Identifier: BSD-3-Clause
 *
 * Copyright (c) 2025 Vinícius Gabriel Pereira Leitão
 * Licensed under the BSD 3-Clause License.
 * See LICENSE file in the project root for full license information.
 */
group = "br.mangabaka"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("plugin.serialization") version "2.0.0"
    kotlin("plugin.allopen") version "2.0.0"
    id("io.ebean") version "15.11.0"
    kotlin("jvm") version "2.0.0"
    id("war")
}

repositories {
    mavenCentral()
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

dependencies {
    // Postgresql
    implementation("org.postgresql:postgresql:42.7.6")
    // Serialização
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")
    // EBEAN
    val ebeanVersion = "15.11.0"
    implementation("io.ebean:ebean:$ebeanVersion")
    runtimeOnly("io.ebean:ebean-agent:$ebeanVersion")
    implementation("io.ebean:ebean-api:$ebeanVersion")
    implementation("io.ebean:ebean-core:$ebeanVersion")
    testImplementation("io.ebean:ebean-test:$ebeanVersion")
    implementation("io.ebean:ebean-ddl-generator:$ebeanVersion")
    implementation("io.ebean:kotlin-querybean-generator:$ebeanVersion")
    // JAKARTA
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.2.0")
    // HK2
    val hk2Version = "3.1.1"
    implementation("org.glassfish.hk2:hk2-locator:$hk2Version")
    implementation("org.glassfish.hk2:hk2-runlevel:$hk2Version")
    // JERSEY
    val jerseyVersion = "3.1.10"
    implementation("org.glassfish.jersey.inject:jersey-hk2:$jerseyVersion")
    implementation("org.glassfish.jersey.media:jersey-media-json-jackson:$jerseyVersion")
    implementation("org.glassfish.jersey.containers:jersey-container-servlet:$jerseyVersion")
    // JWT
    val jwtVersion = "0.12.6"
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jwtVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jwtVersion")
    // LOG4J
    val log4jVersion = "2.25.0"
    implementation("org.fusesource.jansi:jansi:2.4.2")
    implementation("org.apache.logging.log4j:log4j-api:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-core:$log4jVersion")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")
    // Kotlin testes
    testImplementation(kotlin("test"))
    // MOCKITO
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
    // JUNIT
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
}

kotlin {
    jvmToolchain(17)
}

java {
    sourceSets["main"].java.srcDirs("src/main/kotlin")
}

tasks.named<War>("war") {
    if (gradle.startParameter.taskNames.contains("dev")) {
        dependsOn(":frontend:copyFrontendDist")
    }

    archiveFileName.set("backend.war")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Implementation-Title" to "MangaBaka", "Implementation-Version" to "1.0.0", "Created-By" to "Gradle"
        )
    }
    from("src/main/webapp") {
        include("**/*")
        into("")
    }
    doLast {
        println("WAR gerado em: ${archiveFile.get().asFile.absolutePath}")
    }
}

tasks.test {
    useJUnitPlatform()
}