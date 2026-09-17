package com.example

import com.example.core.logic.Classification
import com.example.core.logic.LogicArguments
import com.example.core.logic.LogicEquivalence
import com.example.core.logic.LogicEvaluator
import com.example.core.logic.LogicParser
import com.example.core.logic.LogicSimplifier
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LogicEngineTest {

    private val parser = LogicParser()

    @Test
    fun testParserPrecedence() {
        // ¬p ∧ q should parse as (¬p) ∧ q
        val ast = parser.parse("¬p ∧ q")
        assertEquals("(¬p ∧ q)", ast.toPrettyString())

        // p ∧ q ∨ r should parse as (p ∧ q) ∨ r
        val ast2 = parser.parse("p ∧ q ∨ r")
        assertEquals("((p ∧ q) ∨ r)", ast2.toPrettyString())

        // p → q ↔ ¬p ∨ q
        val ast3 = parser.parse("p → q ↔ ¬p ∨ q")
        assertEquals("((p → q) ↔ (¬p ∨ q))", ast3.toPrettyString())
    }

    @Test
    fun testTruthTableTautology() {
        val table = LogicEvaluator.generateTruthTable("p ∨ ¬p")
        assertEquals(Classification.TAUTOLOGIA, table.classification)
        assertEquals(2, table.rows.size)
        assertTrue(table.rows.all { it.finalValue })
    }

    @Test
    fun testTruthTableContradiction() {
        val table = LogicEvaluator.generateTruthTable("p ∧ ¬p")
        assertEquals(Classification.CONTRADICCION, table.classification)
        assertEquals(2, table.rows.size)
        assertTrue(table.rows.all { !it.finalValue })
    }

    @Test
    fun testTruthTableContingency() {
        val table = LogicEvaluator.generateTruthTable("(p ∧ q) → r")
        assertEquals(Classification.CONTINGENCIA, table.classification)
        assertEquals(8, table.rows.size) // 2^3 = 8
        assertEquals(listOf("p", "q", "r"), table.variables)
    }

    @Test
    fun testLogicalEquivalenceMaterialImplication() {
        val res = LogicEquivalence.checkEquivalence("p → q", "¬p ∨ q")
        assertTrue("p → q must be equivalent to ¬p ∨ q", res.areEquivalent)
        assertEquals(4, res.rows.size)
        assertTrue(res.rows.all { it.matches })
    }

    @Test
    fun testLogicalEquivalenceDeMorgan() {
        val res = LogicEquivalence.checkEquivalence("¬(p ∧ q)", "¬p ∨ ¬q")
        assertTrue("De Morgan ¬(p ∧ q) ≡ ¬p ∨ ¬q", res.areEquivalent)
    }

    @Test
    fun testLogicalNonEquivalenceConverse() {
        val res = LogicEquivalence.checkEquivalence("p → q", "q → p")
        assertFalse("p → q is not equivalent to q → p", res.areEquivalent)
    }

    @Test
    fun testSimplifierStepByStep() {
        val res = LogicSimplifier.simplify("¬(p ∧ q)")
        assertTrue(res.steps.isNotEmpty())
        assertEquals("(¬p ∨ ¬q)", res.finalExpression)

        val doubleNeg = LogicSimplifier.simplify("¬(¬p)")
        assertEquals("p", doubleNeg.finalExpression)
    }

    @Test
    fun testArgumentModusPonens() {
        val res = LogicArguments.validateArgument(listOf("p → q", "p"), "q")
        assertTrue("Modus Ponens must be valid", res.isValid)
        assertEquals("modus_ponens", res.recognizedRule?.id)
    }

    @Test
    fun testArgumentInvalidAffirmingConsequent() {
        val res = LogicArguments.validateArgument(listOf("p → q", "q"), "p")
        assertFalse("Affirming the consequent must be invalid (fallacy)", res.isValid)
    }
}
