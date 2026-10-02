package com.example.demo

import org.apache.maven.artifact.versioning.ComparableVersion
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

/** A version a task's reference answer depends on, as it was when the reference was written. */
data class PinnedVersion(
    val groupId: String,
    val artifactId: String,
    /** Highest release (no alpha/beta/milestone/RC/snapshot). */
    val latestRelease: String,
    /** Highest version of any kind, pre-releases included. */
    val latestAny: String = latestRelease,
)

/**
 * Detects reference answers that have gone stale: a library published a newer release or pre-release after the
 * reference was frozen, so a correct agent would now report a different "latest". Reads Maven Central's
 * `maven-metadata.xml` directly (not javadocs.dev, the system under test) and orders versions with Maven's own
 * `ComparableVersion`.
 */
object ReferenceFreshness {

    private val http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build()
    private val versionTag = Regex("<version>([^<]+)</version>")
    // A pre-release marker as its own segment: 2.1.0-M1, 5.0.0.Alpha2, 4.0.0-rc-7, 1.0-SNAPSHOT, 1.0-b01
    private val preRelease = Regex("(?i)(?:^|[.\\-_])(?:alpha|beta|milestone|rc|cr|snapshot|preview|dev|ea|[abm](?=\\d))(?=$|[.\\-_\\d])")

    fun isPreRelease(version: String) = preRelease.containsMatchIn(version)

    /** (highest release, highest of any kind) by Maven version order. */
    fun latest(versions: List<String>): Pair<String?, String?> {
        val order = compareBy<String> { ComparableVersion(it) }
        return versions.filterNot(::isPreRelease).maxWithOrNull(order) to versions.maxWithOrNull(order)
    }

    fun versions(groupId: String, artifactId: String): List<String> {
        val url = "https://repo1.maven.org/maven2/${groupId.replace('.', '/')}/$artifactId/maven-metadata.xml"
        val response = http.send(HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30)).build(),
            HttpResponse.BodyHandlers.ofString())
        check(response.statusCode() == 200) { "HTTP ${response.statusCode()} for $url" }
        return versionTag.findAll(response.body()).map { it.groupValues[1] }.toList()
    }

    /** One message per pinned version that no longer matches Maven Central; empty when every reference is current. */
    fun check(tasks: List<EvalTask>, versionsOf: (String, String) -> List<String> = ::versions): List<String> =
        tasks.flatMap { task -> task.pinnedVersions.map { task to it } }.mapNotNull { (task, pin) ->
            val (release, any) = latest(versionsOf(pin.groupId, pin.artifactId))
            val ga = "${pin.groupId}:${pin.artifactId}"
            when {
                release != pin.latestRelease || any != pin.latestAny ->
                    "${task.id}: $ga is now release=$release, any=$any; the reference assumes " +
                        "release=${pin.latestRelease}, any=${pin.latestAny}. Re-verify the reference answer."
                else -> null
            }
        }
}
