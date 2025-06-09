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
    val servletApiVersion = "6.0.0"
    providedCompile("jakarta.servlet:jakarta.servlet-api:$servletApiVersion")
    val jettyVersion = "12.0.13"
    implementation("org.eclipse.jetty:jetty-server:$jettyVersion")
    implementation("org.eclipse.jetty.ee10:jetty-ee10-servlet:$jettyVersion")
    val lombokVersion = "1.18.38"
    compileOnly("org.projectlombok:lombok:$lombokVersion")
    annotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testCompileOnly("org.projectlombok:lombok:$lombokVersion")
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceSets["main"].java.srcDirs("src/main/java")
}

tasks.withType<War> {
    archiveFileName.set("backend.war")
}

tasks.test {
    useJUnitPlatform()
}