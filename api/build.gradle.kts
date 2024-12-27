/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

import com.google.protobuf.gradle.id
import java.time.LocalDateTime

plugins {
    `maven-publish`
    signing
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
                name.set("${project.group}:${artifactId}")
                description.set(
                    "The library that simplifies developing of Protocol Buffers Compiler Plugin"
                )
                url.set("https://github.com/raccoons-co/ProtocExtra")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit")
                    }
                }
                developers {
                    developer {
                        organization.set("Raccoons")
                        organizationUrl.set("https://github.com/raccoons-co/")
                        name.set("iselo")
                        email.set("iselo+maven@raccoons.co")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/raccoons-co/ProtocExtra.git")
                    developerConnection.set("scm:git:ssh://github.com:raccoons-co/ProtocExtra.git")
                    url.set("https://github.com/raccoons-co/ProtocExtra/tree/master")
                }
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/raccoons-co/ProtocExtra")
            credentials {
                username = System.getenv("GITHUB_USERNAME")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

signing {
    val gpgPrivateKey: String? = System.getenv("GPG_PRIVATE_KEY")
    val gpgPassphrase: String? = System.getenv("GPG_PASSPHRASE")
    if (gpgPrivateKey != null && gpgPassphrase != null) {
        val gpgSignKey = gpgPrivateKey.replace(" ", "\n")
        useInMemoryPgpKeys(gpgSignKey, gpgPassphrase)
        sign(publishing.publications.getByName("mavenJava"))
    }
}

tasks.jar {
    manifest.attributes(
        mapOf(
            "Name" to "co/raccoons/protoc/protoc-extra-api",
            "Implementation-Version" to project.version.toString(),
            "Implementation-Title" to "Protocol Buffers Compiler Plugin Library",
            "Implementation-Vendor" to "Raccoons",
            "Implementation-Build-Date" to LocalDateTime.now().toString()
        )
    )
}
