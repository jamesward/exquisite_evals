package com.example.demo

import org.junit.jupiter.api.Tag
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ReferenceFreshnessTest {

    @Test fun `latest release and latest of any kind, by Maven order`() {
        assertEquals("2.0.1" to "2.1.0-M1", ReferenceFreshness.latest(listOf("2.0.0-RC2", "2.0.0", "2.1.0-M1", "2.0.1")))
        // publish order is not version order
        assertEquals("2.4.2" to "2.4.2", ReferenceFreshness.latest(listOf("2.4.1", "2.4.2", "2.4.1.3")))
        assertEquals("4.2.18.Final" to "5.0.0.Alpha2", ReferenceFreshness.latest(listOf("4.2.18.Final", "5.0.0.Alpha2")))
        assertTrue(listOf("33.7.2-jre", "4.2.18.Final", "2.0.1").none(ReferenceFreshness::isPreRelease))
    }

    @Test fun `every task pins the versions its reference depends on`() {
        TaskCatalog.all.forEach { assertTrue(it.pinnedVersions.isNotEmpty(), it.id) }
    }

    @Test fun `a newer release on Maven Central is reported as a stale reference`() {
        val task = TaskCatalog.SPRING_AI_HOSTED_TOOL
        assertEquals(emptyList(), ReferenceFreshness.check(listOf(task)) { _, _ -> listOf("2.0.1", "2.1.0-M1") })
        val stale = ReferenceFreshness.check(listOf(task)) { _, _ -> listOf("2.0.1", "2.1.0-M1", "2.1.0") }
        assertTrue(stale.single().contains("release=2.1.0") && stale.single().contains("spring-ai-hostedtool"), stale.toString())
    }

    /** Fails when Maven Central has moved past a reference: re-verify the reference answer, then update the pin. */
    @Tag("live")
    @Test fun `every reference answer is still current on Maven Central`() {
        val stale = ReferenceFreshness.check(TaskCatalog.all)
        assertTrue(stale.isEmpty(), stale.joinToString("\n"))
    }
}
