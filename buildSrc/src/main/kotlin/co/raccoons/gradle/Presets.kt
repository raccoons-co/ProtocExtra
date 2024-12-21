/*
 * Copyright 2023, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.gradle

/**
 * Presets for plugins versions.
 */
enum class Presets(private val version: String) {
    CHECKSTYLE("10.12.4");

    fun version() = version
}
