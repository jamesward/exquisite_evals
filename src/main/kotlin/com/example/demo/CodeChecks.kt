package com.example.demo

/** Deterministic checks of an answer: required facts present, no invented names from a known family. */
data class CodeCheckResult(
    val found: List<String>,
    val missing: List<String>,
    val hallucinatedNames: List<String>,
) {
    val factScore: Double get() = if (found.isEmpty() && missing.isEmpty()) 1.0 else found.size.toDouble() / (found.size + missing.size)
    val passed: Boolean get() = missing.isEmpty() && hallucinatedNames.isEmpty()

    override fun toString() =
        "facts ${found.size}/${found.size + missing.size}" +
            (if (missing.isNotEmpty()) " missing=$missing" else "") +
            (if (hallucinatedNames.isNotEmpty()) " hallucinated=$hallucinatedNames" else "")
}

object CodeChecks {

    /**
     * Models typeset their answers: `2.1.0‑M1` with U+2011, narrow no-break spaces, soft hyphens. Fold those
     * to ASCII so a correct fact isn't missed because of typography.
     */
    fun normalize(text: String): String = text
        .replace(Regex("[\u2010-\u2015\u2212\uFE58\uFE63\uFF0D]"), "-")
        .replace(Regex("[\u00A0\u2007\u2009\u200A\u202F\u205F\u3000]"), " ")
        .replace(Regex("[\u00AD\u200B-\u200D\u2060\uFEFF]"), "")

    fun evaluate(task: EvalTask, answer: String?): CodeCheckResult {
        val text = normalize(answer.orEmpty())
        val (found, missing) = task.facts.partition { it.foundIn(text) }
        val invented = task.names.flatMap { family ->
            family.pattern.findAll(text).map { it.value }.filter { it !in family.accepted }.toList()
        }.distinct()
        return CodeCheckResult(found.map { it.name }, missing.map { it.name }, invented)
    }
}
