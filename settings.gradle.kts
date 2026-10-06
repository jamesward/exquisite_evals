rootProject.name = "exquisite_evals"

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        // -Pinspector: the Spring AI Inspector starter, installed locally with `mvn install` (see README)
        if (providers.gradleProperty("inspector").isPresent) {
            mavenLocal {
                content {
                    includeModule("org.springaicommunity", "spring-ai-inspector-starter")
                    includeModule("org.springaicommunity", "spring-ai-inspector-parent")
                }
            }
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
