/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

import co.raccoons.gradle.BuildWorkflow
import co.raccoons.gradle.checkstyle.CheckstyleConfiguration
import co.raccoons.gradle.jacoco.JacocoConfiguration
import co.raccoons.gradle.java.JavaConfiguration
import co.raccoons.gradle.java.Manifest
import co.raccoons.gradle.java.TestImplementation
import co.raccoons.gradle.java.Version
import co.raccoons.gradle.test.TestJUnitConfiguration
import java.time.LocalDateTime

plugins {
    java
    id("com.google.protobuf") version "0.9.4"
    id("jacoco-report-aggregation")
}

dependencies {
    implementation(project(":api"))
    implementation(project(":plugin"))
}

subprojects {
    apply(plugin = "com.google.protobuf")

    BuildWorkflow.of(project)
        .setGroup("co.raccoons.protoc")
        .setVersion("0.0.9")
        .setDescription("Protocol buffers compiler extra code generation")
        .use(Version.JAVA.of(11))
        .use(Configuration.java(project))
        .use(Configuration.testJUnit())
        .use(JacocoConfiguration.defaultInstance())
        .use(CheckstyleConfiguration.defaultInstance())
}

internal object Configuration {

    fun java(project: Project): JavaConfiguration =
        JavaConfiguration.newBuilder()
            .addManifest(
                Manifest.newBuilder()
                    .putAttributes("Implementation-Title", project.description)
                    .putAttributes("Implementation-Version", project.version.toString())
                    .putAttributes("Implementation-Vendor", "Raccoons")
                    .putAttributes("Implementation-Build-Date", LocalDateTime.now().toString())
                    .build()
            )
            .build()

    fun testJUnit(): TestJUnitConfiguration =
        TestJUnitConfiguration.newBuilder()
            .addDependency(TestImplementation("org.junit.jupiter", "junit-jupiter","5.11.4"))
            .addDependency(TestImplementation("org.junit.jupiter","junit-jupiter-params","5.11.4"))
            .addDependency(TestImplementation("com.google.guava","guava-testlib","33.4.0-jre"))
            .addDependency(TestImplementation("com.google.truth", "truth", "1.4.4"))
            .build()
}
