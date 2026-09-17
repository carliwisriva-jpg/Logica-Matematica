package com.example.core.logic

data class InferenceRule(
    val id: String,
    val name: String,
    val latinName: String = "",
    val premises: List<String>,
    val conclusion: String,
    val symbolicForm: String,
    val explanation: String,
    val example: String,
    val tautologyFormula: String
)

data class ArgumentValidationResult(
    val premises: List<String>,
    val conclusion: String,
    val isValid: Boolean,
    val recognizedRule: InferenceRule?,
    val combinedFormula: String,
    val explanation: String,
    val counterExample: Map<String, Boolean>?
)

object LogicArguments {

    val standardRules = listOf(
        InferenceRule(
            id = "modus_ponens",
            name = "Modus Ponens (Regla de Separación)",
            latinName = "Modus Ponendo Ponens",
            premises = listOf("p → q", "p"),
            conclusion = "q",
            symbolicForm = "p → q\np\n∴ q",
            explanation = "Si sabemos que una implicación 'p → q' es verdadera y que el antecedente 'p' es verdadero, podemos concluir necesariamente que el consecuente 'q' es verdadero.",
            example = "Premisa 1: Si llueve, entonces la calle se moja.\nPremisa 2: Está lloviendo.\nConclusión: Por lo tanto, la calle se moja.",
            tautologyFormula = "((p → q) ∧ p) → q"
        ),
        InferenceRule(
            id = "modus_tollens",
            name = "Modus Tollens",
            latinName = "Modus Tollendo Tollens",
            premises = listOf("p → q", "¬q"),
            conclusion = "¬p",
            symbolicForm = "p → q\n¬q\n∴ ¬p",
            explanation = "Si una implicación es verdadera y el consecuente es falso, el antecedente obligatoriamente debe ser falso.",
            example = "Premisa 1: Si tienes fiebre, estás enfermo.\nPremisa 2: No estás enfermo.\nConclusión: Por lo tanto, no tienes fiebre.",
            tautologyFormula = "((p → q) ∧ ¬q) → ¬p"
        ),
        InferenceRule(
            id = "silogismo_hipotetico",
            name = "Silogismo Hipotético (Transitividad)",
            premises = listOf("p → q", "q → r"),
            conclusion = "p → r",
            symbolicForm = "p → q\nq → r\n∴ p → r",
            explanation = "La relación de implicación es transitiva: si p lleva a q, y q lleva a r, entonces p conduce directamente a r.",
            example = "Premisa 1: Si estudio, apruebo el examen.\nPremisa 2: Si apruebo el examen, me gradúo.\nConclusión: Si estudio, me gradúo.",
            tautologyFormula = "((p → q) ∧ (q → r)) → (p → r)"
        ),
        InferenceRule(
            id = "silogismo_disyuntivo",
            name = "Silogismo Disyuntivo",
            latinName = "Modus Tollendo Ponens",
            premises = listOf("p ∨ q", "¬p"),
            conclusion = "q",
            symbolicForm = "p ∨ q\n¬p\n∴ q",
            explanation = "Si tenemos dos opciones y se descarta una, la otra alternativa debe ser la verdadera.",
            example = "Premisa 1: Voy al cine o voy al parque.\nPremisa 2: No fui al cine.\nConclusión: Por lo tanto, fui al parque.",
            tautologyFormula = "((p ∨ q) ∧ ¬p) → q"
        ),
        InferenceRule(
            id = "conjuncion",
            name = "Regla de Conjunción (Adjunción)",
            premises = listOf("p", "q"),
            conclusion = "p ∧ q",
            symbolicForm = "p\nq\n∴ p ∧ q",
            explanation = "Si dos proposiciones son verdaderas por separado, su conjunción 'p y q' también es verdadera.",
            example = "Premisa 1: El sol brilla.\nPremisa 2: Hace calor.\nConclusión: El sol brilla y hace calor.",
            tautologyFormula = "(p ∧ q) → (p ∧ q)"
        ),
        InferenceRule(
            id = "simplificacion",
            name = "Regla de Simplificación",
            premises = listOf("p ∧ q"),
            conclusion = "p",
            symbolicForm = "p ∧ q\n∴ p",
            explanation = "Si una conjunción es verdadera, cada una de sus partes individuales es necesariamente verdadera.",
            example = "Premisa: El auto es rojo y veloz.\nConclusión: Por lo tanto, el auto es rojo.",
            tautologyFormula = "(p ∧ q) → p"
        ),
        InferenceRule(
            id = "adicion",
            name = "Regla de Adición",
            premises = listOf("p"),
            conclusion = "p ∨ q",
            symbolicForm = "p\n∴ p ∨ q",
            explanation = "A partir de una proposición verdadera se puede construir válidamente una disyunción con cualquier otra proposición.",
            example = "Premisa: Juan está leyendo.\nConclusión: Por lo tanto, Juan está leyendo o está durmiendo.",
            tautologyFormula = "p → (p ∨ q)"
        ),
        InferenceRule(
            id = "resolucion",
            name = "Regla de Resolución",
            premises = listOf("p ∨ q", "¬p ∨ r"),
            conclusion = "q ∨ r",
            symbolicForm = "p ∨ q\n¬p ∨ r\n∴ q ∨ r",
            explanation = "Fundamental en demostración automática de teoremas: cancela la variable p y su negación ¬p entre dos cláusulas.",
            example = "Premisa 1: Llueve o nieva.\nPremisa 2: No llueve o hace viento.\nConclusión: Nieva o hace viento.",
            tautologyFormula = "((p ∨ q) ∧ (¬p ∨ r)) → (q ∨ r)"
        )
    )

    fun validateArgument(premises: List<String>, conclusion: String): ArgumentValidationResult {
        val parser = LogicParser()
        val parsedPremises = premises.map { parser.parse(it) }
        val parsedConclusion = parser.parse(conclusion)

        // Combine premises: (P1 ∧ P2 ∧ ... ∧ Pn)
        val combinedPremisesAst: AstNode = if (parsedPremises.size == 1) {
            parsedPremises[0]
        } else {
            var curr = parsedPremises[0]
            for (i in 1 until parsedPremises.size) {
                curr = AstNode.And(curr, parsedPremises[i])
            }
            curr
        }

        val argumentAst = AstNode.Implies(combinedPremisesAst, parsedConclusion)
        val combinedFormula = argumentAst.toPrettyString()

        val table = LogicEvaluator.generateTruthTable(argumentAst, combinedFormula)
        val isValid = table.classification == Classification.TAUTOLOGIA

        var counterExample: Map<String, Boolean>? = null
        if (!isValid) {
            val falseRow = table.rows.find { !it.finalValue }
            counterExample = falseRow?.variableValues
        }

        // Check if matches known inference rule pattern
        val recognizedRule = matchStandardRule(premises, conclusion)

        val explanation = if (isValid) {
            val ruleNote = if (recognizedRule != null) " Corresponde a la regla clásica: ${recognizedRule.name}." else ""
            "El argumento es VÁLIDO.$ruleNote En todas las interpretaciones donde todas las premisas son verdaderas, la conclusión necesariamente es verdadera (la implicación global es una Tautología)."
        } else {
            val ceText = counterExample?.entries?.joinToString(", ") { "${it.key}=${if (it.value) "V" else "F"}" } ?: ""
            "El argumento es INVÁLIDO (Falacia lógica). Existe al menos un contraejemplo donde las premisas son verdaderas pero la conclusión resulta falsa ($ceText)."
        }

        return ArgumentValidationResult(
            premises = premises,
            conclusion = conclusion,
            isValid = isValid,
            recognizedRule = recognizedRule,
            combinedFormula = combinedFormula,
            explanation = explanation,
            counterExample = counterExample
        )
    }

    private fun matchStandardRule(premises: List<String>, conclusion: String): InferenceRule? {
        val normConclusion = conclusion.replace(" ", "").trim()
        val normPremises = premises.map { it.replace(" ", "").trim() }

        for (rule in standardRules) {
            if (rule.premises.size == normPremises.size) {
                val ruleNormPremises = rule.premises.map { it.replace(" ", "") }
                val ruleNormConcl = rule.conclusion.replace(" ", "")
                if (normConclusion == ruleNormConcl && normPremises.containsAll(ruleNormPremises)) {
                    return rule
                }
            }
        }
        return null
    }
}
