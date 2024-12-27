dependencies {
    implementation("co.raccoons.protoc:protoc-extra-api:0.0.13")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }
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
