package com.example.core.logic

object LogicEvaluator {

    fun evaluate(node: AstNode, assignment: Map<String, Boolean>): Boolean {
        return when (node) {
            is AstNode.Variable -> assignment[node.name]
                ?: throw IllegalArgumentException("Variable '${node.name}' no tiene asignación.")
            is AstNode.Constant -> node.value
            is AstNode.Not -> !evaluate(node.child, assignment)
            is AstNode.And -> evaluate(node.left, assignment) && evaluate(node.right, assignment)
            is AstNode.Or -> evaluate(node.left, assignment) || evaluate(node.right, assignment)
            is AstNode.Xor -> evaluate(node.left, assignment) xor evaluate(node.right, assignment)
            is AstNode.Implies -> {
                val l = evaluate(node.left, assignment)
                val r = evaluate(node.right, assignment)
                !l || r // p → q is ¬p ∨ q
            }
            is AstNode.Iff -> {
                val l = evaluate(node.left, assignment)
                val r = evaluate(node.right, assignment)
                l == r // p ↔ q
            }
        }
    }

    fun extractVariables(node: AstNode): List<String> {
        val vars = mutableSetOf<String>()
        fun collect(n: AstNode) {
            when (n) {
                is AstNode.Variable -> vars.add(n.name)
                is AstNode.Constant -> {}
                is AstNode.Not -> collect(n.child)
                is AstNode.And -> { collect(n.left); collect(n.right) }
                is AstNode.Or -> { collect(n.left); collect(n.right) }
                is AstNode.Xor -> { collect(n.left); collect(n.right) }
                is AstNode.Implies -> { collect(n.left); collect(n.right) }
                is AstNode.Iff -> { collect(n.left); collect(n.right) }
            }
        }
        collect(node)
        return vars.sorted()
    }

    // Extract subexpressions for intermediate columns
    fun extractSubExpressions(node: AstNode): List<AstNode> {
        val subNodes = mutableListOf<AstNode>()
        fun collect(n: AstNode) {
            when (n) {
                is AstNode.Variable, is AstNode.Constant -> {
                    // Don't add simple variable as intermediate since variables are separate columns
                }
                is AstNode.Not -> {
                    collect(n.child)
                    subNodes.add(n)
                }
                is AstNode.And -> {
                    collect(n.left)
                    collect(n.right)
                    subNodes.add(n)
                }
                is AstNode.Or -> {
                    collect(n.left)
                    collect(n.right)
                    subNodes.add(n)
                }
                is AstNode.Xor -> {
                    collect(n.left)
                    collect(n.right)
                    subNodes.add(n)
                }
                is AstNode.Implies -> {
                    collect(n.left)
                    collect(n.right)
                    subNodes.add(n)
                }
                is AstNode.Iff -> {
                    collect(n.left)
                    collect(n.right)
                    subNodes.add(n)
                }
            }
        }
        collect(node)

        // Remove duplicates and the root expression itself from intermediate list
        val unique = mutableListOf<AstNode>()
        val seenStrings = mutableSetOf<String>()
        val rootString = node.toPrettyString()

        for (item in subNodes) {
            val str = item.toPrettyString()
            if (str != rootString && !seenStrings.contains(str)) {
                seenStrings.add(str)
                unique.add(item)
            }
        }
        return unique
    }

    fun generateTruthTable(expression: String): TruthTableResult {
        val parser = LogicParser()
        val ast = parser.parse(expression)
        return generateTruthTable(ast, expression)
    }

    fun generateTruthTable(node: AstNode, originalExpression: String? = null): TruthTableResult {
        val variables = extractVariables(node)
        val intermediateNodes = extractSubExpressions(node)
        val intermediateExprs = intermediateNodes.map { it.toPrettyString() }
        val prettyExpression = originalExpression?.trim() ?: node.toPrettyString()

        if (variables.size > 6) {
            throw LogicParseException("La expresión tiene ${variables.size} variables. El límite máximo para generar tablas de verdad es de 6 variables (64 filas) para garantizar un rendimiento óptimo.")
        }

        val rowCount = if (variables.isEmpty()) 1 else 1 shl variables.size
        val rows = mutableListOf<TruthTableRow>()
        var trueCount = 0
        var falseCount = 0

        if (variables.isEmpty()) {
            val finalVal = evaluate(node, emptyMap())
            if (finalVal) trueCount++ else falseCount++
            rows.add(
                TruthTableRow(
                    variableValues = emptyMap(),
                    intermediateValues = intermediateNodes.map { evaluate(it, emptyMap()) },
                    finalValue = finalVal
                )
            )
        } else {
            // Standard truth table order: row 0 starts with all true (V), descending
            for (i in 0 until rowCount) {
                val assignment = mutableMapOf<String, Boolean>()
                for (vIndex in variables.indices) {
                    val varName = variables[vIndex]
                    val shift = variables.size - 1 - vIndex
                    // In standard tables: first half V, second half F
                    val bit = (i shr shift) and 1
                    // 0 -> true (V), 1 -> false (F)
                    assignment[varName] = (bit == 0)
                }

                val intermediateVals = intermediateNodes.map { evaluate(it, assignment) }
                val finalVal = evaluate(node, assignment)
                if (finalVal) trueCount++ else falseCount++

                rows.add(
                    TruthTableRow(
                        variableValues = assignment,
                        intermediateValues = intermediateVals,
                        finalValue = finalVal
                    )
                )
            }
        }

        val classification = when {
            falseCount == 0 -> Classification.TAUTOLOGIA
            trueCount == 0 -> Classification.CONTRADICCION
            else -> Classification.CONTINGENCIA
        }

        val explanation = when (classification) {
            Classification.TAUTOLOGIA ->
                "La proposición resulta VERDADERA en todas las $rowCount combinaciones posibles. Es una TAUTOLOGÍA (siempre se cumple independientemente de los valores de entrada)."
            Classification.CONTRADICCION ->
                "La proposición resulta FALSA en todas las $rowCount combinaciones posibles. Es una CONTRADICCIÓN (nunca es verdadera, su valor siempre es F)."
            Classification.CONTINGENCIA ->
                "La proposición contiene $trueCount combinaciones verdaderas y $falseCount combinaciones falsas de un total de $rowCount. Es una CONTINGENCIA (su valor de verdad depende de los valores asignados a sus variables)."
        }

        return TruthTableResult(
            expression = prettyExpression,
            variables = variables,
            intermediateExpressions = intermediateExprs,
            rows = rows,
            classification = classification,
            trueCount = trueCount,
            falseCount = falseCount,
            explanation = explanation
        )
    }
}
