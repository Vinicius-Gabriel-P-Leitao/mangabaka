plugins {
    id("java")
    id("war")
}

group = "br.mangabaka"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // OUTRAS
    implementation("org.postgresql:postgresql:42.7.6")
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    implementation("com.graphql-java-generator:graphql-java-client-dependencies:2.9")
    // EBEAN
    val ebeanVersion = "15.11.0"
    implementation("io.ebean:ebean:$ebeanVersion")
    runtimeOnly("io.ebean:ebean-agent:$ebeanVersion")
    implementation("io.ebean:ebean-api:$ebeanVersion")
    implementation("io.ebean:ebean-core:$ebeanVersion")
    implementation("io.ebean:ebean-querybean:$ebeanVersion")
    implementation("io.ebean:ebean-ddl-generator:$ebeanVersion")
    // JAKARTA
    val jakartaVersion = "6.1.0"
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:4.0.0")
    implementation("jakarta.validation:jakarta.validation-api:3.1.1")
    implementation("jakarta.annotation:jakarta.annotation-api:3.0.0")
    compileOnly("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")
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
    // LOMBOK
    val lombokVersion = "1.18.38"
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    // JUNIT
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    sourceSets["main"].java.srcDirs("src/main/java")
}

tasks.withType<War> {
    archiveFileName.set("backend.war")
    manifest {
        attributes(
            "Implementation-Title" to "MangaBaka", "Implementation-Version" to "1.0.0", "Created-By" to "Gradle"
        )
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from("src/main/webapp") {
        into("")
    }
}

tasks.test {
    useJUnitPlatform()
}