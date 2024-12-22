/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

rootProject.name = "ProtocExtra"

include(
    "api",
    "plugin"
)

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}
