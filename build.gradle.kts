/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    id("com.google.protobuf") version "0.9.4"
    id("net.ltgt.errorprone") version "4.1.0"
    checkstyle
    jacoco
    id("jacoco-report-aggregation")
}

dependencies {
    jacocoAggregation(project(":api"))
    jacocoAggregation(project(":plugin"))
}

checkstyle {
    toolVersion = "10.12.4"
}

subprojects {
    group = "co.raccoons.protoc"
    version = "0.0.11"

    setOf(
        "java",
        "com.google.protobuf",
        "net.ltgt.errorprone",
        "checkstyle",
        "jacoco"
    ).forEach { apply(plugin = it) }

    dependencies {
        setOf(
            "com.google.protobuf:protobuf-java:4.28.3",
            "com.google.guava:guava:33.4.0-jre",
        ).forEach { implementation(it) }

        setOf(
            "org.junit.jupiter:junit-jupiter:5.11.4",
            "org.junit.jupiter:junit-jupiter-params:5.11.4",
            "com.google.guava:guava-testlib:33.4.0-jre",
            "com.google.truth:truth:1.4.4"
        ).forEach { testImplementation(it) }

        errorprone("com.google.errorprone:error_prone_core:2.31.0")
    }

    tasks {
        compileJava{
            options.errorprone.excludedPaths.set(".*/build/generated/.*")
        }
        test {
            useJUnitPlatform()
            finalizedBy(jacocoTestReport)
        }
        jacocoTestReport {
            dependsOn(test)
        }
    }
}
