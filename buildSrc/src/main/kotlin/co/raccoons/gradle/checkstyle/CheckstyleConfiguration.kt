/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.gradle.checkstyle

import co.raccoons.gradle.Presets
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.quality.Checkstyle
import org.gradle.api.plugins.quality.CheckstyleExtension

/**
 * Checkstyle plugin configuration.
 */
class CheckstyleConfiguration(
    private val toolVersion: String,
    private val reportFormats: List<CheckstyleReportFormat>
) : Plugin<Project> {

    companion object {

        private const val CHECKSTYLE_PLUGIN_ID = "checkstyle"

        /** Returns new plugin configuration builder. */
        fun newBuilder() = Builder()

        /** Returns default configuration of the Checkstyle plugin. */
        fun defaultInstance(): CheckstyleConfiguration = newBuilder().build()

        /** The configuration builder */
        class Builder {

            private var toolVersion = Presets.CHECKSTYLE.version()
            private val enabledFormats = mutableListOf<CheckstyleReportFormat>()

            /** Sets Checkstyle tool version. */
            fun setVersion(version: String): Builder {
                this.toolVersion = version
                return this
            }

            /** Enables Checkstyle report format. */
            fun enable(reportFormat: CheckstyleReportFormat): Builder {
                this.enabledFormats.add(reportFormat)
                return this
            }

            /** Returns new Checkstyle plugin configuration. */
            fun build() = CheckstyleConfiguration(this.toolVersion, this.enabledFormats)
        }
    }

    /** @inheritDoc */
    override fun apply(project: Project) {
        this.setupPlugin(project)
        this.enableReports(project)
    }

    private fun setupPlugin(project: Project) {
        project.plugins.apply(CHECKSTYLE_PLUGIN_ID)
        project.extensions
            .getByType(CheckstyleExtension::class.java)
            .toolVersion = this.toolVersion
    }

    private fun enableReports(project: Project) {
        project.tasks
            .withType(Checkstyle::class.java)
            .configureEach { checkstyle ->

                for (format in this.reportFormats) {
                    format.subscribeTo(checkstyle.reports)
                }
            }
    }
}
