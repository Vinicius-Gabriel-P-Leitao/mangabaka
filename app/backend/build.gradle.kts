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
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    implementation("org.postgresql:postgresql:42.7.6")
    val ebeanVersion = "15.11.0"
    implementation("io.ebean:ebean:$ebeanVersion")
    runtimeOnly("io.ebean:ebean-agent:$ebeanVersion")
    implementation("io.ebean:ebean-api:$ebeanVersion")
    implementation("io.ebean:ebean-core:$ebeanVersion")
    implementation("io.ebean:ebean-querybean:$ebeanVersion")
    implementation("io.ebean:ebean-ddl-generator:$ebeanVersion")
    val jakartaVersion = "6.1.0"
    providedCompile("jakarta.servlet:jakarta.servlet-api:$jakartaVersion")
    val lombokVersion = "1.18.38"
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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