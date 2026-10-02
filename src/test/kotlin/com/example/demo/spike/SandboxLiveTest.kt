package com.example.demo.spike

import com.example.demo.Sandboxes
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springaicommunity.agent.tools.ShellTools
import java.nio.file.Files
import kotlin.test.assertTrue

/**
 * The Docker sandbox the shell arms use (needs Docker and the image: `docker build -t exquisite-evals-sandbox:1 sandbox`).
 * Checks what matters for the evals: the tools agents use are there, the network works, the host's secrets and files
 * are not visible, and the container is removed afterwards.
 */
@Tag("live")
class SandboxLiveTest {

    private fun containers(): String =
        ProcessBuilder("docker", "ps", "-aq", "--filter", "label=org.springaicommunity.agent.exec-backend=docker-cli")
            .redirectErrorStream(true).start().inputStream.bufferedReader().readText().trim()

    @Test fun `commands run in an isolated container with the tools agents use`() {
        val workDir = Files.createTempDirectory("sandbox-test")
        val before = containers()
        val backend = Sandboxes.docker("exquisite-evals-sandbox:1", workDir)
        val bash = ShellTools.builder().execBackend(backend).build()
        try {
            fun run(command: String) = bash.bash(command, null, null, null).also { println("SANDBOX \$ $command\n$it") }

            val tools = run("for c in curl unzip jar jq git; do command -v \$c >/dev/null || echo MISSING-\$c; done; echo tools-ok")
            assertTrue(tools.contains("tools-ok") && !tools.contains("MISSING"), tools)

            val env = run("env")
            listOf("AWS_BEARER_TOKEN_BEDROCK", "TYPESAFE_API_KEY", "BRAVE_API_KEY").forEach { key ->
                assertTrue(!env.contains(key), "$key leaked into the sandbox")
                System.getenv(key)?.takeIf { it.isNotBlank() }?.let { assertTrue(!env.contains(it), "the value of $key leaked into the sandbox") }
            }

            val host = run("ls /home; test -e ${System.getProperty("user.home")}/projects && echo HOST-VISIBLE || echo host-hidden")
            assertTrue(host.contains("host-hidden"), host)

            val net = run("curl -s -o /dev/null -w '%{http_code}' https://repo1.maven.org/maven2/org/springaicommunity/typesafe-spring-ai/maven-metadata.xml")
            assertTrue(net.contains("200"), net)

            run("echo from-container > /workspace/out.txt")
            assertTrue(Files.readString(workDir.resolve("out.txt")).contains("from-container"), "the mount is writable both ways")
        } finally {
            backend.close()
            workDir.toFile().deleteRecursively()
        }
        assertTrue(containers() == before, "the run's container was removed")
    }
}
