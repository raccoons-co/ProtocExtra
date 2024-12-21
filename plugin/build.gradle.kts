import co.raccoons.gradle.BuildWorkflow
import co.raccoons.gradle.java.Implementation
import co.raccoons.gradle.java.JavaLibraryConfiguration
import co.raccoons.gradle.publish.MavenPublishConfiguration
import co.raccoons.gradle.publish.maven.License
import co.raccoons.gradle.publish.maven.Pom
import co.raccoons.gradle.publish.maven.Publication

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.5"
    }

    generateProtoTasks {
        ofSourceSet("main")
    }
}

BuildWorkflow.of(project)
    .use(Configuration.javaLibrary())
    .use(Configuration.mavenPublish())

internal object Configuration {

    fun javaLibrary(): JavaLibraryConfiguration =
        JavaLibraryConfiguration.newBuilder()
            .addDependency(Implementation("co.raccoons.protoc", "protoc-extra-lib", "0.0.9"))
            .addDependency(Implementation("com.google.guava","guava","33.4.0-jre"))
            .addDependency(Implementation("com.google.protobuf", "protobuf-java", "4.28.3"))
            .build()

    fun mavenPublish(): MavenPublishConfiguration {
        val license =
            License.newBuilder()
                .setName("ProtocExtra")
                .setUrl("https://opensource.org/license/mit")
                .build()

        val pom =
            Pom.newBuilder()
                .setName("ProtocExtra")
                .setDescription("Protoc Plugin")
                .setUrl("https://github.com/raccoons-co/ProtocExtra")
                .setLicense(license)
                .build()

        val publication =
            Publication.newBuilder()
                .setArtifactId("protoc-extra-plugin")
                .setPom(pom)
                .build()

        return MavenPublishConfiguration(publication)
    }
}
