dependencies {
    implementation("co.raccoons.protoc:protoc-extra-api:0.0.9")
    implementation("com.google.guava:guava:33.4.0-jre")
    implementation("com.google.protobuf:protobuf-java:4.28.3")
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
    manifest {
        attributes(mapOf("Main-Class" to "co.raccoons.protoc.extra.Plugin"))
    }
    from(
        configurations.runtimeClasspath.get().map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
    archiveExtension.set("exe")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    exclude("google/**")
}
