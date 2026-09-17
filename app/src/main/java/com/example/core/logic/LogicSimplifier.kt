package com.example.core.logic

data class SimplificationStep(
    val stepNumber: Int,
    val ruleName: String,
    val description: String,
    val expression: String
)

data class SimplificationResult(
    val originalExpression: String,
    val steps: List<SimplificationStep>,
    val finalExpression: String,
    val isTautology: Boolean,
    val isContradiction: Boolean
)

object LogicSimplifier {

    fun simplify(expression: String): SimplificationResult {
        val parser = LogicParser()
        var currentAst = parser.parse(expression)
        val steps = mutableListOf<SimplificationStep>()
        var stepCount = 0

        var changed = true
        var loopGuard = 0

        while (changed && loopGuard < 15) {
            changed = false
            loopGuard++

            // Step 1: Eliminate Implication p → q => ¬p ∨ q
            val stepImp = transformOnce(currentAst) { node ->
                if (node is AstNode.Implies) {
                    val transformed = AstNode.Or(AstNode.Not(node.left), node.right)
                    TransformHit(
                        transformed,
                        "Ley de la Implicación",
                        "Transformamos (${node.left.toPrettyString()} → ${node.right.toPrettyString()}) en (¬${node.left.toPrettyString()} ∨ ${node.right.toPrettyString()})."
                    )
                } else null
            }
            if (stepImp != null) {
                currentAst = stepImp.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepImp.rule, stepImp.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }

            // Step 2: Eliminate Biconditional p ↔ q => (¬p ∨ q) ∧ (¬q ∨ p)
            val stepIff = transformOnce(currentAst) { node ->
                if (node is AstNode.Iff) {
                    val p = node.left
                    val q = node.right
                    val leftPart = AstNode.Or(AstNode.Not(p), q)
                    val rightPart = AstNode.Or(AstNode.Not(q), p)
                    val transformed = AstNode.And(leftPart, rightPart)
                    TransformHit(
                        transformed,
                        "Ley del Bicondicional",
                        "Transformamos (${p.toPrettyString()} ↔ ${q.toPrettyString()}) en (${leftPart.toPrettyString()} ∧ ${rightPart.toPrettyString()})."
                    )
                } else null
            }
            if (stepIff != null) {
                currentAst = stepIff.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepIff.rule, stepIff.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }

            // Step 3: De Morgan laws: ¬(A ∧ B) => ¬A ∨ ¬B, ¬(A ∨ B) => ¬A ∧ ¬B
            val stepDeMorgan = transformOnce(currentAst) { node ->
                if (node is AstNode.Not) {
                    when (val inner = node.child) {
                        is AstNode.And -> {
                            val transformed = AstNode.Or(AstNode.Not(inner.left), AstNode.Not(inner.right))
                            TransformHit(
                                transformed,
                                "Ley de De Morgan",
                                "Aplicamos De Morgan sobre ¬(${inner.left.toPrettyString()} ∧ ${inner.right.toPrettyString()}) obteniendo (${AstNode.Not(inner.left).toPrettyString()} ∨ ${AstNode.Not(inner.right).toPrettyString()})."
                            )
                        }
                        is AstNode.Or -> {
                            val transformed = AstNode.And(AstNode.Not(inner.left), AstNode.Not(inner.right))
                            TransformHit(
                                transformed,
                                "Ley de De Morgan",
                                "Aplicamos De Morgan sobre ¬(${inner.left.toPrettyString()} ∨ ${inner.right.toPrettyString()}) obteniendo (${AstNode.Not(inner.left).toPrettyString()} ∧ ${AstNode.Not(inner.right).toPrettyString()})."
                            )
                        }
                        else -> null
                    }
                } else null
            }
            if (stepDeMorgan != null) {
                currentAst = stepDeMorgan.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepDeMorgan.rule, stepDeMorgan.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }

            // Step 4: Double negation: ¬(¬A) => A
            val stepDblNot = transformOnce(currentAst) { node ->
                if (node is AstNode.Not && node.child is AstNode.Not) {
                    val inner = (node.child as AstNode.Not).child
                    TransformHit(
                        inner,
                        "Ley de Doble Negación",
                        "Eliminamos la doble negación ¬(¬${inner.toPrettyString()}) quedando ${inner.toPrettyString()}."
                    )
                } else null
            }
            if (stepDblNot != null) {
                currentAst = stepDblNot.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepDblNot.rule, stepDblNot.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }

            // Step 5: Constant propagation: ¬V => F, ¬F => V
            val stepConstNot = transformOnce(currentAst) { node ->
                if (node is AstNode.Not && node.child is AstNode.Constant) {
                    val v = (node.child as AstNode.Constant).value
                    TransformHit(
                        AstNode.Constant(!v),
                        "Negación de Constante",
                        "¬${if (v) "V" else "F"} es ${if (!v) "V" else "F"}."
                    )
                } else null
            }
            if (stepConstNot != null) {
                currentAst = stepConstNot.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepConstNot.rule, stepConstNot.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }

            // Step 6: Complement laws: A ∨ ¬A => V, A ∧ ¬A => F
            val stepComp = transformOnce(currentAst) { node ->
                when (node) {
                    is AstNode.Or -> {
                        if (isNegationOf(node.left, node.right) || isNegationOf(node.right, node.left)) {
                            TransformHit(
                                AstNode.Constant(true),
                                "Ley del Complemento (Tercero Excluido)",
                                "${node.left.toPrettyString()} ∨ ${node.right.toPrettyString()} siempre es Verdadero (V)."
                            )
                        } else null
                    }
                    is AstNode.And -> {
                        if (isNegationOf(node.left, node.right) || isNegationOf(node.right, node.left)) {
                            TransformHit(
                                AstNode.Constant(false),
                                "Ley de Contradicción / Complemento",
                                "${node.left.toPrettyString()} ∧ ${node.right.toPrettyString()} siempre es Falso (F)."
                            )
                        } else null
                    }
                    else -> null
                }
            }
            if (stepComp != null) {
                currentAst = stepComp.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepComp.rule, stepComp.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }

            // Step 7: Idempotence: A ∨ A => A, A ∧ A => A
            val stepIdemp = transformOnce(currentAst) { node ->
                when (node) {
                    is AstNode.Or -> {
                        if (node.left.toPrettyString() == node.right.toPrettyString()) {
                            TransformHit(
                                node.left,
                                "Ley de Idempotencia",
                                "${node.left.toPrettyString()} ∨ ${node.right.toPrettyString()} se reduce a ${node.left.toPrettyString()}."
                            )
                        } else null
                    }
                    is AstNode.And -> {
                        if (node.left.toPrettyString() == node.right.toPrettyString()) {
                            TransformHit(
                                node.left,
                                "Ley de Idempotencia",
                                "${node.left.toPrettyString()} ∧ ${node.right.toPrettyString()} se reduce a ${node.left.toPrettyString()}."
                            )
                        } else null
                    }
                    else -> null
                }
            }
            if (stepIdemp != null) {
                currentAst = stepIdemp.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepIdemp.rule, stepIdemp.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }

            // Step 8: Identity and Domination with constants
            val stepConst = transformOnce(currentAst) { node ->
                when (node) {
                    is AstNode.And -> {
                        when {
                            node.left is AstNode.Constant -> {
                                val c = (node.left as AstNode.Constant).value
                                if (c) TransformHit(node.right, "Ley de Identidad", "V ∧ ${node.right.toPrettyString()} = ${node.right.toPrettyString()}.")
                                else TransformHit(AstNode.Constant(false), "Ley de Dominación", "F ∧ ${node.right.toPrettyString()} = F.")
                            }
                            node.right is AstNode.Constant -> {
                                val c = (node.right as AstNode.Constant).value
                                if (c) TransformHit(node.left, "Ley de Identidad", "${node.left.toPrettyString()} ∧ V = ${node.left.toPrettyString()}.")
                                else TransformHit(AstNode.Constant(false), "Ley de Dominación", "${node.left.toPrettyString()} ∧ F = F.")
                            }
                            else -> null
                        }
                    }
                    is AstNode.Or -> {
                        when {
                            node.left is AstNode.Constant -> {
                                val c = (node.left as AstNode.Constant).value
                                if (c) TransformHit(AstNode.Constant(true), "Ley de Dominación", "V ∨ ${node.right.toPrettyString()} = V.")
                                else TransformHit(node.right, "Ley de Identidad", "F ∨ ${node.right.toPrettyString()} = ${node.right.toPrettyString()}.")
                            }
                            node.right is AstNode.Constant -> {
                                val c = (node.right as AstNode.Constant).value
                                if (c) TransformHit(AstNode.Constant(true), "Ley de Dominación", "${node.left.toPrettyString()} ∨ V = V.")
                                else TransformHit(node.left, "Ley de Identidad", "${node.left.toPrettyString()} ∨ F = ${node.left.toPrettyString()}.")
                            }
                            else -> null
                        }
                    }
                    else -> null
                }
            }
            if (stepConst != null) {
                currentAst = stepConst.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepConst.rule, stepConst.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }

            // Step 9: Absorption laws: A ∨ (A ∧ B) => A, A ∧ (A ∨ B) => A
            val stepAbs = transformOnce(currentAst) { node ->
                when (node) {
                    is AstNode.Or -> {
                        val innerAnd = when {
                            node.right is AstNode.And -> node.right as AstNode.And
                            node.left is AstNode.And -> node.left as AstNode.And
                            else -> null
                        }
                        val outer = if (node.right is AstNode.And) node.left else node.right
                        if (innerAnd != null && (innerAnd.left.toPrettyString() == outer.toPrettyString() || innerAnd.right.toPrettyString() == outer.toPrettyString())) {
                            TransformHit(outer, "Ley de Absorción", "${node.toPrettyString()} se reduce a ${outer.toPrettyString()}.")
                        } else null
                    }
                    is AstNode.And -> {
                        val innerOr = when {
                            node.right is AstNode.Or -> node.right as AstNode.Or
                            node.left is AstNode.Or -> node.left as AstNode.Or
                            else -> null
                        }
                        val outer = if (node.right is AstNode.Or) node.left else node.right
                        if (innerOr != null && (innerOr.left.toPrettyString() == outer.toPrettyString() || innerOr.right.toPrettyString() == outer.toPrettyString())) {
                            TransformHit(outer, "Ley de Absorción", "${node.toPrettyString()} se reduce a ${outer.toPrettyString()}.")
                        } else null
                    }
                    else -> null
                }
            }
            if (stepAbs != null) {
                currentAst = stepAbs.newNode
                stepCount++
                steps.add(SimplificationStep(stepCount, stepAbs.rule, stepAbs.desc, currentAst.toPrettyString()))
                changed = true
                continue
            }
        }

        val finalStr = currentAst.toPrettyString()
        val isTautology = (currentAst is AstNode.Constant && currentAst.value)
        val isContradiction = (currentAst is AstNode.Constant && !currentAst.value)

        return SimplificationResult(
            originalExpression = expression,
            steps = steps,
            finalExpression = finalStr,
            isTautology = isTautology,
            isContradiction = isContradiction
        )
    }

    private data class TransformHit(
        val newNode: AstNode,
        val rule: String,
        val desc: String
    )

    private fun isNegationOf(a: AstNode, b: AstNode): Boolean {
        if (a is AstNode.Not && a.child.toPrettyString() == b.toPrettyString()) return true
        if (b is AstNode.Not && b.child.toPrettyString() == a.toPrettyString()) return true
        return false
    }

    private fun transformOnce(node: AstNode, transformer: (AstNode) -> TransformHit?): TransformHit? {
        val direct = transformer(node)
        if (direct != null) return direct

        return when (node) {
            is AstNode.Variable, is AstNode.Constant -> null
            is AstNode.Not -> {
                val childHit = transformOnce(node.child, transformer)
                if (childHit != null) {
                    TransformHit(AstNode.Not(childHit.newNode), childHit.rule, childHit.desc)
                } else null
            }
            is AstNode.And -> {
                val lHit = transformOnce(node.left, transformer)
                if (lHit != null) {
                    TransformHit(AstNode.And(lHit.newNode, node.right), lHit.rule, lHit.desc)
                } else {
                    val rHit = transformOnce(node.right, transformer)
                    if (rHit != null) {
                        TransformHit(AstNode.And(node.left, rHit.newNode), rHit.rule, rHit.desc)
                    } else null
                }
            }
            is AstNode.Or -> {
                val lHit = transformOnce(node.left, transformer)
                if (lHit != null) {
                    TransformHit(AstNode.Or(lHit.newNode, node.right), lHit.rule, lHit.desc)
                } else {
                    val rHit = transformOnce(node.right, transformer)
                    if (rHit != null) {
                        TransformHit(AstNode.Or(node.left, rHit.newNode), rHit.rule, rHit.desc)
                    } else null
                }
            }
            is AstNode.Xor -> {
                val lHit = transformOnce(node.left, transformer)
                if (lHit != null) {
                    TransformHit(AstNode.Xor(lHit.newNode, node.right), lHit.rule, lHit.desc)
                } else {
                    val rHit = transformOnce(node.right, transformer)
                    if (rHit != null) {
                        TransformHit(AstNode.Xor(node.left, rHit.newNode), rHit.rule, rHit.desc)
                    } else null
                }
            }
            is AstNode.Implies -> {
                val lHit = transformOnce(node.left, transformer)
                if (lHit != null) {
                    TransformHit(AstNode.Implies(lHit.newNode, node.right), lHit.rule, lHit.desc)
                } else {
                    val rHit = transformOnce(node.right, transformer)
                    if (rHit != null) {
                        TransformHit(AstNode.Implies(node.left, rHit.newNode), rHit.rule, rHit.desc)
                    } else null
                }
            }
            is AstNode.Iff -> {
                val lHit = transformOnce(node.left, transformer)
                if (lHit != null) {
                    TransformHit(AstNode.Iff(lHit.newNode, node.right), lHit.rule, lHit.desc)
                } else {
                    val rHit = transformOnce(node.right, transformer)
                    if (rHit != null) {
                        TransformHit(AstNode.Iff(node.left, rHit.newNode), rHit.rule, rHit.desc)
                    } else null
                }
            }
        }
    }
}
