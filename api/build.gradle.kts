/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

import com.google.protobuf.gradle.id
import java.time.LocalDateTime

plugins {
    `maven-publish`
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }
    plugins {
        id("extra") {
            path = "util/plugin.sh"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("extra") {
                    outputSubDir = "java"
                }
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "protoc-extra-api"
            from(components.getByName("java"))
            pom {
                name.set("Protoc Plugin Library")
                description.set("Abstract Protobuf Compiler Plugin API")
                url.set("https://github.com/raccoons-co/ProtocExtra")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit")
                    }
                }
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    manifest {
        attributes(
            mapOf(
                "Name" to "co/raccoons/protoc/protoc-extra-api",
                "Implementation-Version" to project.version.toString(),
                "Implementation-Title" to "Protocol Buffers Compiler Plugin Library",
                "Implementation-Vendor" to "Raccoons",
                "Implementation-Build-Date" to LocalDateTime.now().toString()
            )
        )
    }
}
