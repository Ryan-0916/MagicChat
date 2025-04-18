plugins {
    id("java")
    id("maven-publish")
    id("com.gradleup.shadow") version "9.0.0-beta11"
}

val projectVersion : String by project

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    dependencies {
        compileOnly("org.projectlombok:lombok:1.18.24")
        annotationProcessor("org.projectlombok:lombok:1.18.24")
        testAnnotationProcessor("org.projectlombok:lombok:1.18.24")
        testCompileOnly("org.projectlombok:lombok:1.18.24")
    }

    repositories {
        maven {
            name = "myRepositories"
            url = uri(layout.buildDirectory.dir("file://D:\\Maven\\MavenRepository"))
        }
        mavenCentral()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://repo.dmulloy2.net/repository/public/")
    }

    if ("core" == project.name) {
        tasks.shadowJar {
            destinationDirectory.set(file("$rootDir/target"))
            archiveClassifier.set("")
            archiveFileName.set("${rootProject.name}-${projectVersion}.jar")
        }
    }
}