package com.example.core.logic

data class EquivalenceRow(
    val variableValues: Map<String, Boolean>,
    val val1: Boolean,
    val val2: Boolean,
    val matches: Boolean
)

data class EquivalenceResult(
    val expr1: String,
    val expr2: String,
    val areEquivalent: Boolean,
    val variables: List<String>,
    val rows: List<EquivalenceRow>,
    val explanation: String
)

object LogicEquivalence {

    fun checkEquivalence(expression1: String, expression2: String): EquivalenceResult {
        val parser = LogicParser()
        val ast1 = parser.parse(expression1)
        val ast2 = parser.parse(expression2)

        val vars1 = LogicEvaluator.extractVariables(ast1)
        val vars2 = LogicEvaluator.extractVariables(ast2)
        val allVars = (vars1 + vars2).distinct().sorted()

        if (allVars.size > 6) {
            throw LogicParseException("El conjunto conjunto de variables excede el límite de 6 variables para comprobación de equivalencia.")
        }

        val rowCount = if (allVars.isEmpty()) 1 else 1 shl allVars.size
        val rows = mutableListOf<EquivalenceRow>()
        var allMatch = true

        if (allVars.isEmpty()) {
            val v1 = LogicEvaluator.evaluate(ast1, emptyMap())
            val v2 = LogicEvaluator.evaluate(ast2, emptyMap())
            val matches = (v1 == v2)
            if (!matches) allMatch = false
            rows.add(
                EquivalenceRow(
                    variableValues = emptyMap(),
                    val1 = v1,
                    val2 = v2,
                    matches = matches
                )
            )
        } else {
            for (i in 0 until rowCount) {
                val assignment = mutableMapOf<String, Boolean>()
                for (vIndex in allVars.indices) {
                    val varName = allVars[vIndex]
                    val shift = allVars.size - 1 - vIndex
                    val bit = (i shr shift) and 1
                    assignment[varName] = (bit == 0) // 0 -> V, 1 -> F
                }

                val v1 = LogicEvaluator.evaluate(ast1, assignment)
                val v2 = LogicEvaluator.evaluate(ast2, assignment)
                val matches = (v1 == v2)
                if (!matches) allMatch = false

                rows.add(
                    EquivalenceRow(
                        variableValues = assignment,
                        val1 = v1,
                        val2 = v2,
                        matches = matches
                    )
                )
            }
        }

        val explanation = if (allMatch) {
            "Las expresiones son LÓGICAMENTE EQUIVALENTES ($expression1 ≡ $expression2). En todas las $rowCount combinaciones posibles de variables, ambas expresiones producen exactamente los mismos valores de verdad."
        } else {
            val diffCount = rows.count { !it.matches }
            "Las expresiones NO son equivalentes ($expression1 ≢ $expression2). Difieren en $diffCount de las $rowCount combinaciones posibles."
        }

        return EquivalenceResult(
            expr1 = expression1,
            expr2 = expression2,
            areEquivalent = allMatch,
            variables = allVars,
            rows = rows,
            explanation = explanation
        )
    }
}
