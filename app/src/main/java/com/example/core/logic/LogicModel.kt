package com.example.core.logic

sealed class AstNode {
    abstract fun toPrettyString(): String

    data class Variable(val name: String) : AstNode() {
        override fun toPrettyString(): String = name
    }

    data class Constant(val value: Boolean) : AstNode() {
        override fun toPrettyString(): String = if (value) "V" else "F"
    }

    data class Not(val child: AstNode) : AstNode() {
        override fun toPrettyString(): String {
            return when (child) {
                is Variable, is Constant, is Not -> "¬${child.toPrettyString()}"
                else -> "¬(${child.toPrettyString()})"
            }
        }
    }

    data class And(val left: AstNode, val right: AstNode) : AstNode() {
        override fun toPrettyString(): String = "(${left.toPrettyString()} ∧ ${right.toPrettyString()})"
    }

    data class Or(val left: AstNode, val right: AstNode) : AstNode() {
        override fun toPrettyString(): String = "(${left.toPrettyString()} ∨ ${right.toPrettyString()})"
    }

    data class Xor(val left: AstNode, val right: AstNode) : AstNode() {
        override fun toPrettyString(): String = "(${left.toPrettyString()} ⊕ ${right.toPrettyString()})"
    }

    data class Implies(val left: AstNode, val right: AstNode) : AstNode() {
        override fun toPrettyString(): String = "(${left.toPrettyString()} → ${right.toPrettyString()})"
    }

    data class Iff(val left: AstNode, val right: AstNode) : AstNode() {
        override fun toPrettyString(): String = "(${left.toPrettyString()} ↔ ${right.toPrettyString()})"
    }
}

enum class TokenType {
    VAR,
    NOT,        // ¬, ~, !
    AND,        // ∧, &, ^, and
    OR,         // ∨, |, or
    XOR,        // ⊕, xor
    IMPLIES,    // →, ->, =>
    IFF,        // ↔, <->, <=>
    LPAREN,     // (
    RPAREN,     // )
    TRUE,       // V, 1, T
    FALSE,      // F, 0
    EOF
}

data class LogicToken(
    val type: TokenType,
    val text: String,
    val position: Int
)

class LogicParseException(
    override val message: String,
    val position: Int = -1
) : Exception(message)

enum class Classification(val label: String, val description: String) {
    TAUTOLOGIA("Tautología", "La proposición es VERDADERA para todas las combinaciones posibles de sus variables."),
    CONTRADICCION("Contradicción", "La proposición es FALSA para todas las combinaciones posibles de sus variables."),
    CONTINGENCIA("Contingencia", "La proposición toma valores VERDADEROS y FALSOS dependiendo de las combinaciones.")
}

data class TruthTableRow(
    val variableValues: Map<String, Boolean>,
    val intermediateValues: List<Boolean>,
    val finalValue: Boolean
)

data class TruthTableResult(
    val expression: String,
    val variables: List<String>,
    val intermediateExpressions: List<String>,
    val rows: List<TruthTableRow>,
    val classification: Classification,
    val trueCount: Int,
    val falseCount: Int,
    val explanation: String
)
