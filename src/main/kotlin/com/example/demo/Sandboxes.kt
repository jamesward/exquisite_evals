package com.example.demo

import org.springaicommunity.agent.exec.docker.DockerCliExecBackend
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermissions

/**
 * Docker sandbox for the shell arms (`sandbox/Dockerfile`): one managed container per run, the run's scratch
 * directory bind-mounted at `/workspace`, and nothing from the host environment, so model-written commands can't
 * read the API keys this JVM holds. The container is removed when the run closes it, or by a JVM shutdown hook.
 */
object Sandboxes {

    const val WORKSPACE = "/workspace"

    fun docker(image: String, workDir: Path): DockerCliExecBackend {
        // The image runs as a non-root user whose uid differs from the host user's: let it write to the mount.
        runCatching { Files.setPosixFilePermissions(workDir, PosixFilePermissions.fromString("rwxrwxrwx")) }
        return DockerCliExecBackend.builder()
            .image(image)
            .mount(workDir, WORKSPACE)
            .shellCommand("/bin/bash", "-c")
            .environment(mapOf("HOME" to "/home/agent", "LANG" to "C.UTF-8"))
            .build()
    }
}
