dependencies {
    implementation("co.raccoons.protoc:protoc-extra-api:${project.version}")
}

protobuf {
    generateProtoTasks {
        ofSourceSet("main")
    }
}

tasks.jar {
    manifest.attributes(
        mapOf("Main-Class" to "co.raccoons.protoc.extra.Plugin")
    )
    from(
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
    exclude("google/**")
    archiveClassifier.set("exe")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
