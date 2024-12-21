/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

rootProject.name = "ProtocExtra"

include(
    "lib",
    "plugin"
)

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}
