/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

import co.raccoons.gradle.BuildWorkflow
import co.raccoons.gradle.java.*
import co.raccoons.gradle.java.Manifest
import co.raccoons.gradle.publish.MavenPublishConfiguration
import co.raccoons.gradle.publish.maven.License
import co.raccoons.gradle.publish.maven.Pom
import co.raccoons.gradle.publish.maven.Publication
import java.time.LocalDateTime

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }

    generateProtoTasks {
        ofSourceSet("main")
    }

    plugins {
    }
}

configure<SourceSetContainer> {
    this.named("main").configure {
        this.java.srcDirs(project.layout.projectDirectory.dir("../generated/"))
    }
}


BuildWorkflow.of(project)
    .use(Configuration.javaLibrary())
    .use(Configuration.mavenPublish())

internal object Configuration {

    fun javaLibrary(): JavaLibraryConfiguration =
        JavaLibraryConfiguration.newBuilder()
            .addDependency(Implementation("com.google.guava","guava","33.4.0-jre"))
            .addDependency(Implementation("com.google.errorprone","error_prone_core","2.36.0"))
            .addDependency(Implementation("com.google.protobuf","protobuf-java","4.28.3"))
            .build()


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
                .setArtifactId("protoc-extra-lib")
                .setPom(pom)
                .build()

        return MavenPublishConfiguration(publication)
    }
}
