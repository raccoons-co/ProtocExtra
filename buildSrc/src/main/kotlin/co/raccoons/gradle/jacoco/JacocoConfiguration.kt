/*
 * Copyright 2024, Raccoons. Developing simple way to change.
 *
 * @license MIT
 */

package co.raccoons.gradle.jacoco

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.testing.jacoco.tasks.JacocoReport

/**
 * Jacoco plugin configuration.
 */
class JacocoConfiguration private constructor(
    private val reportFormats: List<JacocoReportFormat>
) : Plugin<Project> {

    companion object {

        private const val JACOCO_PLUGIN_ID = "jacoco"

        /** Returns new plugin configuration builder. */
        fun newBuilder() = Builder()

        /** Returns default configuration of the Jacoco plugin. */
        fun defaultInstance(): JacocoConfiguration = newBuilder().build()

        /** The configuration builder */
        class Builder {

            private val enabledFormats = mutableListOf<JacocoReportFormat>()

            /** Enables Jacoco report format. */
            fun enable(reportFormat: JacocoReportFormat): Builder {
                this.enabledFormats.add(reportFormat)
                return this
            }

            /** Returns new Jacoco plugin configuration. */
            fun build() = JacocoConfiguration(this.enabledFormats)
        }
    }

    /** @inheritDoc */
    override fun apply(project: Project) {
        this.setupPlugin(project)
        this.enableReports(project)
        this.addTaskFinalizer(project)
    }

    private fun setupPlugin(project: Project) {
        project.plugins.apply(JACOCO_PLUGIN_ID)
    }

    private fun enableReports(project: Project) {
        project.tasks
            .withType(JacocoReport::class.java) { jacocoReport ->
                jacocoReport.dependsOn(project.tasks.withType(Test::class.java))

                for (format in this.reportFormats) {
                    format.subscribeTo(jacocoReport.reports)
                }
            }

        project.tasks
            .withType(Test::class.java) { test ->
                test.finalizedBy(project.tasks.withType(JacocoReport::class.java))
            }
    }

    private fun addTaskFinalizer(project: Project) {
        project.tasks
            .withType(Test::class.java) { test ->
                test.finalizedBy(project.tasks.withType(JacocoReport::class.java))
            }
    }
}
