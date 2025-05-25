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
        compileOnly("org.projectlombok:lombok:${rootProject.properties["lombok_version"]}")
        annotationProcessor("org.projectlombok:lombok:${rootProject.properties["lombok_version"]}")
    }

    repositories {
        maven {
            name = "myRepositories"
            url = uri(layout.buildDirectory.dir("file://D:\\Maven\\MavenRepository"))
        }
        mavenCentral()
        mavenLocal()
        /* PaperMC */
        maven("https://repo.papermc.io/repository/maven-public/")
        /* PacketEvents */
        maven("https://repo.codemc.io/repository/maven-releases/")
        maven("https://repo.codemc.io/repository/maven-snapshots/")
    }

    // val target = file("$rootDir/target")
    val target = file("D:\\Minecraft\\Servers\\1.21.4\\Lobby\\plugins")

    if ("core" == project.name) {
        tasks.shadowJar {
            destinationDirectory.set(target)
            archiveClassifier.set("")
            archiveFileName.set("${rootProject.name}-${projectVersion}.jar")
        }
    }

    tasks.processResources {
        filteringCharset = "UTF-8"
        filesMatching(arrayListOf("plugin.yml")) {
            expand(
                Pair("projectVersion", rootProject.properties["projectVersion"]),
            )
        }
    }
}