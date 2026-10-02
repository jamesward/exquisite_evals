package com.example.demo

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** The code checks are the deterministic half of every verdict: each task's reference must pass them. */
class CodeChecksTest {

    @Test fun `every task's own reference answer passes its code checks`() {
        TaskCatalog.all.forEach { task ->
            val r = CodeChecks.evaluate(task, task.reference)
            assertTrue(r.passed, "${task.id}: $r")
        }
    }

    @Test fun `task ids are unique and selection works`() {
        assertEquals(TaskCatalog.all.size, TaskCatalog.all.map { it.id }.toSet().size)
        assertEquals(TaskCatalog.all, TaskCatalog.select(listOf("all")))
        assertEquals(listOf("jackson3-ptv"), TaskCatalog.select(listOf("jackson3-ptv")).map { it.id })
        assertFailsWith<IllegalStateException> { TaskCatalog.select(listOf("nope")) }
    }

    @Test fun `jevjudge - an invented type and a missing version fail`() {
        val r = CodeChecks.evaluate(TaskCatalog.JEVJUDGE_GAV,
            "org.springaicommunity:typesafe-spring-ai. Use JevRubricScorer via `noul()` and `score()`.")
        assertEquals(listOf("JevRubricScorer"), r.hallucinatedNames)
        assertTrue("version 0.3.0" in r.missing && "check" in r.missing, r.toString())
        assertTrue(!r.passed)
    }

    @Test fun `member facts need code form, not prose`() {
        val prose = CodeChecks.evaluate(TaskCatalog.JEVJUDGE_GAV, "you can check the score of a choice and build it")
        assertTrue(listOf("check", "score", "choice", "build").all { it in prose.missing }, prose.toString())
        val code = CodeChecks.evaluate(TaskCatalog.JEVJUDGE_GAV, "check(String, Predicate, String), `score`, choice (…), build()")
        assertTrue(listOf("check", "score", "choice", "build").all { it in code.found }, code.toString())
    }

    @Test fun `jackson - stale 2x answer is caught`() {
        val stale = "Use com.fasterxml.jackson.core:jackson-databind:2.17.0. Methods: allowIfBaseType, allowIfSubType, " +
            "allowIfSubTypeIsArray (allows all arrays), denyForExactBaseType, allowIfSubTypeMatches."
        val r = CodeChecks.evaluate(TaskCatalog.JACKSON3_PTV, stale)
        assertEquals(listOf("allowIfSubTypeMatches"), r.hallucinatedNames)
        assertTrue("Jackson 3 groupId" in r.missing && "allowSubTypesWithExplicitDeserializer" in r.missing, r.toString())
    }

    @Test fun `hostedtool - stable version and an invented record fail`() {
        val r = CodeChecks.evaluate(TaskCatalog.SPRING_AI_HOSTED_TOOL,
            "Spring AI 2.0.1: HostedTool.WebSearch, HostedTool.FileSearch, HostedTool.Custom")
        assertEquals(listOf("Custom"), r.hallucinatedNames)
        assertTrue("version 2.1.0-M1" in r.missing && "Raw" in r.missing, r.toString())
    }

    @Test fun `trap - describing rubric() fails, saying it does not exist passes`() {
        val base = "spring-ai-agent-utils 0.12.0 uses /bin/bash -c; override with `shellCommand(...)`; cleanEnvironment defaults to false. "
        val fooled = CodeChecks.evaluate(TaskCatalog.AGENT_UTILS_SHELL_TRAP, base + "Call rubric(\"quality\", 0.8) on the builder.")
        assertTrue("says rubric() does not exist" in fooled.missing, fooled.toString())
        listOf("JevJudge.Builder has no rubric() method.", "There is no rubric() method.", "rubric() does not exist in 0.3.0.",
            "JevJudge.Builder doesn't have a rubric method.", "The `JevJudge.Builder` class **does not contain a `rubric()` method**.").forEach { denial ->
            val ok = CodeChecks.evaluate(TaskCatalog.AGENT_UTILS_SHELL_TRAP, base + denial)
            assertTrue(ok.passed, "$denial -> $ok")
        }
    }

    @Test fun `typographic hyphens and spaces do not hide a correct fact`() {
        val r = CodeChecks.evaluate(TaskCatalog.SPRING_AI_HOSTED_TOOL,
            "Spring\u202FAI 2.1.0\u2011M1 (a pre\u2011release): HostedTool.WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration, Raw")
        assertTrue(r.passed, r.toString())
    }

    @Test fun `hostedtool - either framing passes, an unqualified latest or a bare does-not-exist fails`() {
        val t = TaskCatalog.SPRING_AI_HOSTED_TOOL
        val types = "WebSearch, FileSearch, CodeInterpreter, Mcp, ImageGeneration and Raw (raw JSON)"
        // the answer the old reference wrongly failed
        assertTrue(CodeChecks.evaluate(t, "The stable release (2.0.1) does not contain HostedTool. It only appears in " +
            "the milestone 2.1.0-M1, where it permits $types.").passed)
        assertTrue(CodeChecks.evaluate(t, "Latest is 2.1.0-M1 (a milestone). HostedTool permits $types.").passed)
        val unqualified = CodeChecks.evaluate(t, "The latest release is 2.1.0-M1. HostedTool permits $types.")
        assertEquals(listOf("says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it"), unqualified.missing)
        val stopped = CodeChecks.evaluate(t, "HostedTool does not exist in the latest release, 2.0.1.")
        assertTrue("version 2.1.0-M1" in stopped.missing && "Raw" in stopped.missing, stopped.toString())
    }
}
