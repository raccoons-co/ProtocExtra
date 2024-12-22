/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

import co.raccoons.gradle.BuildWorkflow
import co.raccoons.gradle.java.JavaConfiguration
import co.raccoons.gradle.java.Manifest
import co.raccoons.gradle.publish.MavenPublishConfiguration
import co.raccoons.gradle.publish.maven.License
import co.raccoons.gradle.publish.maven.Pom
import co.raccoons.gradle.publish.maven.Publication
import com.google.protobuf.gradle.id

dependencies {
    implementation("com.google.protobuf:protobuf-java:4.28.3")
    implementation("com.google.guava:guava:33.4.0-jre")
    implementation("com.google.errorprone:error_prone_core:2.36.0")
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

tasks.jar {
    manifest {
        attributes(mapOf("Name" to "co/raccoons/protoc/protoc-extra-api"))
    }
}

BuildWorkflow.of(project)
    .use(Configuration.mavenPublish())

internal object Configuration {

    fun mavenPublish(): MavenPublishConfiguration {
        val license =
            License.newBuilder()
                .setName("ProtocExtra")
                .setUrl("https://opensource.org/license/mit")
                .build()

        val pom =
            Pom.newBuilder()
                .setName("ProtocExtra")
                .setDescription("Abstract Protoc Plugin Library")
                .setUrl("https://github.com/raccoons-co/ProtocExtra")
                .setLicense(license)
                .build()

        val publication =
            Publication.newBuilder()
                .setArtifactId("protoc-extra-api")
                .setPom(pom)
                .build()

        return MavenPublishConfiguration(publication)
    }
}
