/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license http://www.apache.org/licenses/LICENSE-2.0
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
