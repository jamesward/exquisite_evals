plugins {
    kotlin("jvm") version "2.4.20"
    kotlin("plugin.spring") version "2.4.20"
    id("org.springframework.boot") version "4.2.0-M2"
    id("io.spring.dependency-management") version "1.1.7"
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(platform("org.springframework.ai:spring-ai-bom:2.1.0-M1"))
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.springframework.ai:spring-ai-starter-mcp-client")
    implementation("org.springframework.ai:spring-ai-starter-tool-search-advisor")
    // SimpleVectorStore for VectorToolIndex (optional dependency of the tool-search module)
    implementation("org.springframework.ai:spring-ai-vector-store")

    // ShellTools / SmartWebFetchTool (built against Spring AI 2.0.1; verified on 2.1.0-M1 by the spike)
    implementation("org.springaicommunity:spring-ai-agent-utils:0.12.0")
    // Runs the shell arms' commands in a Docker container instead of on the host
    implementation("org.springaicommunity:spring-ai-agent-utils-docker-cli:0.12.0")
    // JevJudge + auto-configured TypeSafeClient
    implementation("org.springaicommunity:spring-ai-starter-typesafe:0.3.0")
    implementation("org.springaicommunity:typesafe-spring-ai:0.3.0")
    // Maven's version ordering, for ReferenceFreshness (plexus-utils is only needed by classes we don't use)
    implementation("org.apache.maven:maven-artifact:3.9.16") { exclude(group = "org.codehaus.plexus", module = "plexus-utils") }
    // -Pinspector: show the runs in a running Spring AI Inspector (it stays inactive when none is reachable)
    if (project.hasProperty("inspector")) implementation("org.springaicommunity:spring-ai-inspector-starter:0.0.1-SNAPSHOT")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform {
        // live tests hit Bedrock / javadocs.dev / TypeSafe and cost money: opt in with -Plive
        if (!project.hasProperty("live")) excludeTags("live")
    }
    testLogging {
        events("passed", "failed", "skipped")
        showStandardStreams = project.hasProperty("live")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

// `./gradlew bootRun [-PevalTasks=a,b] [-PevalArms=x,y]` runs the evals; the test context never does (evals.run unset).
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    args("--evals.run=true")
    project.findProperty("evalTasks")?.let { args("--evals.tasks=$it") }
    project.findProperty("evalArms")?.let { args("--evals.arms=$it") }
    project.findProperty("evalLabel")?.let { args("--evals.label=$it") }
}
