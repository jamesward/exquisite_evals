package com.example.demo

/**
 * A fact the answer must state, matched by [pattern] (case-insensitive). Facts are what code can verify
 * exactly — versions, coordinates, member names, constants; judgement of prose is left to the judges.
 */
data class Fact(val name: String, val pattern: Regex) {
    fun foundIn(answer: String) = pattern.containsMatchIn(answer)

    companion object {
        fun literal(name: String, text: String = name) = Fact(name, Regex(Regex.escape(text), RegexOption.IGNORE_CASE))
        fun regex(name: String, regex: String) = Fact(name, Regex(regex, RegexOption.IGNORE_CASE))
        /** A member name written as code: `name` or name( — so common words like "check" don't match prose. */
        fun member(name: String) = Fact(name, Regex("`${Regex.escape(name)}`|\\b${Regex.escape(name)}\\s*\\("))
    }
}

/**
 * Identifiers the answer may use from a family (e.g. every `Jev*` type): any match of [pattern] whose
 * text is not in [accepted] is reported as a hallucinated name.
 */
data class NameFamily(val description: String, val pattern: Regex, val accepted: Set<String>)

/**
 * One eval task. [reference] is the frozen, verified answer the judges grade against; it was checked
 * against the published javadoc jars on 2026-09-30 and must be re-verified when "latest" moves.
 */
data class EvalTask(
    val id: String,
    val title: String,
    val prompt: String,
    val reference: String,
    val facts: List<Fact>,
    val names: List<NameFamily> = emptyList(),
    /** The question asks about something that does not exist; the right answer says so. */
    val trap: Boolean = false,
)

object TaskCatalog {

    private val JEV_TYPES = NameFamily(
        "Jev* types in typesafe-spring-ai 0.3.0",
        Regex("\\bJev[A-Z]\\w*"),
        setOf(
            "JevJudge", "JevVerdict", "JevFinding", "JevCriterion", "JevJudgeInput", "JevEvaluator",
            "JevConfidenceGate", "JevConsistency", "JevCompositeScore", "JevSelfRefineAdvisor",
            "JevSelfRefineFailedException", "JevGuardrail", "JevGuardrailAdvisor", "JevDocumentFilter",
            "JevDocumentReranker", "JevToolIndex", "JevChatModel",
        ),
    )

    private val JEV_BUILDER_METHODS = listOf(
        "noul", "score", "choice", "check", "criterion", "minConfidence",
        "failOnInconclusive", "failOnError", "failFast", "feedbackRenderer", "build",
    )

    val JEVJUDGE_GAV = EvalTask(
        id = "jevjudge-gav",
        title = "Identify the artifact from a class name, then read its Builder API",
        prompt = "A Java project uses a class called JevJudge from Maven Central. Give its Maven coordinates " +
            "(groupId:artifactId:version, at the latest version), list every method of its Builder with its " +
            "parameter types, and state the default value of minConfidence.",
        reference = """
            Coordinates: org.springaicommunity:typesafe-spring-ai:0.3.0 (the Java class is
            org.springaicommunity.typesafe.judge.JevJudge; com.jamesward:zio-evals_3 also has a JevJudge, but it
            is a Scala object with no Builder).
            JevJudge.Builder methods:
            - noul(String name, Noul noul, double minimum)
            - score(String name, Score score, double minimum)
            - choice(String name, Choice choice, String... acceptedOptions)
            - check(String name, Predicate<JevJudgeInput> check, String defect)
            - criterion(JevCriterion criterion)
            - minConfidence(double minConfidence)
            - failOnInconclusive(boolean failOnInconclusive)
            - failOnError(boolean failOnError)
            - failFast(boolean failFast)
            - feedbackRenderer(Function<List<JevFinding>, String> feedbackRenderer)
            - build() returning JevJudge
            The builder is obtained with JevJudge.builder(TypeSafeClient).
            Default minConfidence: JevJudge.DEFAULT_MIN_CONFIDENCE = 0.6.
        """.trimIndent(),
        facts = listOf(
            Fact.literal("groupId", "org.springaicommunity"),
            Fact.literal("artifactId", "typesafe-spring-ai"),
            Fact.regex("version 0.3.0", "\\b0\\.3\\.0\\b"),
            Fact.regex("default minConfidence 0.6", "\\b0\\.60?\\b"),
        ) + JEV_BUILDER_METHODS.map(Fact::member),
        names = listOf(JEV_TYPES),
    )

    val JACKSON3_PTV = EvalTask(
        id = "jackson3-ptv",
        title = "Jackson 3 vs 2: new groupId, latest versions, API diff and a behaviour change",
        prompt = "In the latest Jackson 3 release of jackson-databind, list the public methods of " +
            "BasicPolymorphicTypeValidator.Builder. Which of them does not exist in the latest Jackson 2.x release? " +
            "What behaviour of allowIfSubTypeIsArray() changed, and in which versions?",
        reference = """
            Jackson 3 uses groupId tools.jackson.core: latest is tools.jackson.core:jackson-databind:3.2.3
            (package tools.jackson.databind.jsontype). Latest 2.x is com.fasterxml.jackson.core:jackson-databind:2.22.3.
            Public methods of BasicPolymorphicTypeValidator.Builder in 3.2.3:
            - allowIfBaseType(Class<?>), allowIfBaseType(String prefix), allowIfBaseType(Pattern), allowIfBaseType(TypeMatcher)
            - allowIfSubType(Class<?>), allowIfSubType(String prefix), allowIfSubType(Pattern), allowIfSubType(TypeMatcher)
            - allowIfSubTypeIsArray()
            - allowSubTypesWithExplicitDeserializer()
            - denyForExactBaseType(Class<?>)
            - build()
            (plus protected helpers _appendBaseMatcher, _appendSubNameMatcher, _appendSubClassMatcher)
            Not in 2.22.3: allowSubTypesWithExplicitDeserializer() — it allows subtypes for which jackson-databind or a
            registered JacksonModule provides an explicit ValueDeserializer.
            allowIfSubTypeIsArray() behaviour change (databind#5981): it used to approve every array regardless of element
            type, which let a denied class be smuggled in as an array (e.g. Evil[]). It now unwraps arrays (recursively) and
            validates the innermost element type against the configured sub-type matchers; primitive, abstract and interface
            element types are accepted. Changed in 3.1.4 (Jackson 3) and 2.18.8 (Jackson 2).
        """.trimIndent(),
        facts = listOf(
            Fact.literal("Jackson 3 groupId", "tools.jackson.core"),
            Fact.regex("Jackson 3 version 3.2.3", "\\b3\\.2\\.3\\b"),
            Fact.regex("Jackson 2 version 2.22.3", "\\b2\\.22\\.3\\b"),
            Fact.member("allowIfBaseType"),
            Fact.member("allowIfSubType"),
            Fact.member("allowIfSubTypeIsArray"),
            Fact.member("allowSubTypesWithExplicitDeserializer"),
            Fact.member("denyForExactBaseType"),
            Fact.regex("change version 3.1.4", "\\b3\\.1\\.4\\b"),
            Fact.regex("change version 2.18.8", "\\b2\\.18\\.8\\b"),
        ),
        names = listOf(NameFamily(
            "allow*/deny* methods of BasicPolymorphicTypeValidator.Builder",
            Regex("\\b(?:allow|deny)[A-Z]\\w*"),
            setOf("allowIfBaseType", "allowIfSubType", "allowIfSubTypeIsArray", "allowSubTypesWithExplicitDeserializer", "denyForExactBaseType"),
        )),
    )

    val SPRING_AI_HOSTED_TOOL = EvalTask(
        id = "spring-ai-hostedtool",
        title = "The class exists only in a milestone, not in the latest stable release",
        prompt = "In the latest release of Spring AI's OpenAI module, what types does the sealed interface HostedTool " +
            "permit, and which one lets you send a tool definition as raw JSON?",
        // Updated 2026-10-02: javadocs.dev now resolves "latest" to the latest *stable* release (2.0.1), which has
        // no HostedTool. The old reference called 2.1.0-M1 "latest", so a correct "not in 2.0.1, only in the
        // 2.1.0-M1 milestone" answer was failed by both judges. Either framing is now accepted.
        reference = """
            Versions of org.springframework.ai:spring-ai-openai (checked 2026-10-02): the latest stable release is
            2.0.1, and the newest version overall is the 2.1.0-M1 milestone (a pre-release).
            HostedTool (org.springframework.ai.openai.responses.HostedTool, @since 2.1.0) does not exist in 2.0.1. It
            exists only in the 2.1.0-M1 milestone, where it is a sealed interface permitting six records:
            HostedTool.WebSearch, HostedTool.FileSearch, HostedTool.CodeInterpreter, HostedTool.Mcp,
            HostedTool.ImageGeneration and HostedTool.Raw.
            HostedTool.Raw(Map<String, Object> tool) declares a tool as raw request JSON, for tools Spring AI does not type yet.
            Each implements toTool(), converting to the OpenAI SDK's com.openai.models.responses.Tool.
            Grading note: "latest release" is ambiguous for this artifact. A correct answer either says the latest
            stable release (2.0.1) has no HostedTool and that it is available in the 2.1.0-M1 milestone, or reports
            2.1.0-M1 while making clear it is a milestone / pre-release; either way it lists the six types and
            identifies Raw. Saying HostedTool does not exist at all, or presenting 2.1.0-M1 as a stable release, is wrong.
        """.trimIndent(),
        facts = listOf(
            Fact.regex("version 2.1.0-M1", "\\b2\\.1\\.0-M1\\b"),
            Fact.regex("says 2.1.0-M1 is a milestone, or that stable 2.0.1 lacks it",
                "\\b2\\.0\\.1\\b|milestone|pre-?release|preview|not (yet )?(a )?(stable|GA)\\b"),
        ) + listOf("WebSearch", "FileSearch", "CodeInterpreter", "Mcp", "ImageGeneration", "Raw")
            .map { Fact.regex(it, "\\b$it\\b") },
        names = listOf(NameFamily(
            "HostedTool.* permitted types",
            Regex("(?<=HostedTool\\.)[A-Z]\\w*"),
            setOf("WebSearch", "FileSearch", "CodeInterpreter", "Mcp", "ImageGeneration", "Raw"),
        )),
    )

    val AGENT_UTILS_SHELL_TRAP = EvalTask(
        id = "agentutils-shell-trap",
        title = "A buried default, plus a method that does not exist",
        prompt = "In the latest spring-ai-agent-utils, what shell does LocalExecBackend run commands with by default on " +
            "Linux, how do you override it, and do child processes inherit the JVM's environment by default? " +
            "Also, how do I use JevJudge.Builder.rubric() from typesafe-spring-ai?",
        reference = """
            Latest org.springaicommunity:spring-ai-agent-utils is 0.12.0. LocalExecBackend
            (org.springaicommunity.agent.exec.LocalExecBackend) runs commands with /bin/bash -c on Linux (cmd.exe /c on
            Windows). Override it with LocalExecBackend.builder().shellCommand(String... shellCommand).
            Child processes inherit the JVM's environment by default: cleanEnvironment defaults to false; set
            cleanEnvironment(true) to start from an empty environment (recommended for model-authored commands), and
            environment(Map) to add variables.
            JevJudge.Builder has no rubric() method (typesafe-spring-ai 0.3.0). Its methods are noul, score, choice, check,
            criterion, minConfidence, failOnInconclusive, failOnError, failFast, feedbackRenderer and build; a rubric is
            expressed as several criteria (noul/score/choice) rather than one rubric call.
        """.trimIndent(),
        facts = listOf(
            Fact.regex("version 0.12.0", "\\b0\\.12\\.0\\b"),
            Fact.literal("default shell /bin/bash", "/bin/bash"),
            Fact.member("shellCommand"),
            Fact.regex("cleanEnvironment", "\\bcleanEnvironment\\b"),
            Fact.regex(
                "says rubric() does not exist",
                "(does ?n[o']t|doesn't) (exist|have|provide|offer|expose|include|contain)|\\bno (such )?(`?rubric|method|api)|\\bthere is no\\b|is not (a|an|part)|isn't (a|an|part)|not (a )?(real|valid|existing|public)|not available|unavailable|non-?existent",
            ),
        ),
        names = listOf(JEV_TYPES),
        trap = true,
    )

    val all: List<EvalTask> = listOf(JEVJUDGE_GAV, JACKSON3_PTV, SPRING_AI_HOSTED_TOOL, AGENT_UTILS_SHELL_TRAP)

    /** Resolves `all` or a list of ids (unknown ids fail fast with the valid ones). */
    fun select(ids: List<String>): List<EvalTask> {
        if (ids.isEmpty() || ids.any { it.equals("all", ignoreCase = true) }) return all
        return ids.map { id -> all.firstOrNull { it.id == id } ?: error("unknown task '$id'; tasks: ${all.map { it.id }}") }
    }
}
