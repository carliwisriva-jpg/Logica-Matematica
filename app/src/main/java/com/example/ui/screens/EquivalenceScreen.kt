package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.logic.EquivalenceResult
import com.example.core.logic.LogicEquivalence
import com.example.core.logic.LogicParseException
import com.example.ui.components.LogicKeypad
import com.example.ui.components.ValueBadge
import com.example.ui.theme.LogicFalse
import com.example.ui.theme.LogicTrue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EquivalenceScreen(
    initialExpr1: String = "p → q",
    initialExpr2: String = "¬p ∨ q",
    onSaveHistory: (String, String) -> Unit = { _, _ -> }
) {
    var expr1 by remember { mutableStateOf(initialExpr1) }
    var expr2 by remember { mutableStateOf(initialExpr2) }
    var activeInputTarget by remember { mutableStateOf(1) } // 1 or 2
    var result by remember { mutableStateOf<EquivalenceResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun testEquivalence() {
        errorMessage = null
        if (expr1.isBlank() || expr2.isBlank()) {
            errorMessage = "Introduce ambas expresiones para comparar."
            result = null
            return
        }
        try {
            val res = LogicEquivalence.checkEquivalence(expr1, expr2)
            result = res
            onSaveHistory(
                "$expr1 vs $expr2",
                if (res.areEquivalent) "Equivalentes (≡)" else "No equivalentes (≢)"
            )
        } catch (e: LogicParseException) {
            errorMessage = e.message
            result = null
        } catch (e: Exception) {
            errorMessage = "Error de sintaxis: ${e.message}"
            result = null
        }
    }

    remember(Unit) {
        testEquivalence()
    }

    val pairsPresets = listOf(
        Pair("p → q", "¬p ∨ q") to "Implicación material",
        Pair("¬(p ∧ q)", "¬p ∨ ¬q") to "De Morgan ∧",
        Pair("¬(p ∨ q)", "¬p ∧ ¬q") to "De Morgan ∨",
        Pair("p ↔ q", "(p → q) ∧ (q → p)") to "Bicondicional",
        Pair("p → q", "q → p") to "Falacia recíproca (No equiv.)"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Comprobador de Equivalencias",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Comprueba si dos expresiones producen la misma tabla de verdad.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Expression 1 input
        OutlinedTextField(
            value = expr1,
            onValueChange = { expr1 = it; errorMessage = null },
            label = { Text("Expresión 1 (A)") },
            placeholder = { Text("p → q") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_equiv_1"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            ),
            trailingIcon = {
                IconButton(onClick = { activeInputTarget = 1 }) {
                    Text(
                        "1",
                        fontWeight = FontWeight.Bold,
                        color = if (activeInputTarget == 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        // Expression 2 input
        OutlinedTextField(
            value = expr2,
            onValueChange = { expr2 = it; errorMessage = null },
            label = { Text("Expresión 2 (B)") },
            placeholder = { Text("¬p ∨ q") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_equiv_2"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            ),
            trailingIcon = {
                IconButton(onClick = { activeInputTarget = 2 }) {
                    Text(
                        "2",
                        fontWeight = FontWeight.Bold,
                        color = if (activeInputTarget == 2) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )

        // Target selector indicator
        Text(
            text = "Escribiendo en: Expresión $activeInputTarget (toca para cambiar)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        // Presets
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            pairsPresets.forEach { (pair, label) ->
                AssistChip(
                    onClick = {
                        expr1 = pair.first
                        expr2 = pair.second
                        testEquivalence()
                    },
                    label = { Text("${pair.first} ≡ ${pair.second}", fontFamily = FontFamily.Monospace) }
                )
            }
        }

        // Virtual Keypad
        LogicKeypad(
            onInsert = { sym ->
                if (activeInputTarget == 1) expr1 += sym else expr2 += sym
                errorMessage = null
            },
            onBackspace = {
                if (activeInputTarget == 1 && expr1.isNotEmpty()) expr1 = expr1.dropLast(1)
                else if (activeInputTarget == 2 && expr2.isNotEmpty()) expr2 = expr2.dropLast(1)
                errorMessage = null
            },
            onClear = {
                if (activeInputTarget == 1) expr1 = "" else expr2 = ""
                errorMessage = null
                result = null
            }
        )

        Button(
            onClick = { testEquivalence() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_compare_equivalence"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.CompareArrows, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Comprobar Equivalencia", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        if (errorMessage != null) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Equivalence Result
        result?.let { eqRes ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Equivalence status header
                    Surface(
                        color = if (eqRes.areEquivalent) LogicTrue.copy(alpha = 0.12f) else LogicFalse.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.5.dp,
                            if (eqRes.areEquivalent) LogicTrue else LogicFalse
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = if (eqRes.areEquivalent) Icons.Default.CheckCircle else Icons.Default.HighlightOff,
                                contentDescription = null,
                                tint = if (eqRes.areEquivalent) LogicTrue else LogicFalse,
                                modifier = Modifier.size(32.dp)
                            )
                            Column {
                                Text(
                                    text = if (eqRes.areEquivalent) "¡SON LÓGICAMENTE EQUIVALENTES!" else "NO SON EQUIVALENTES",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = if (eqRes.areEquivalent) LogicTrue else LogicFalse
                                )
                                Text(
                                    text = if (eqRes.areEquivalent) "${eqRes.expr1} ≡ ${eqRes.expr2}" else "${eqRes.expr1} ≢ ${eqRes.expr2}",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    // Side-by-side Truth Table
                    val horizScroll = rememberScrollState()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                            .horizontalScroll(horizScroll)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            // Headers
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                    .padding(vertical = 8.dp, horizontal = 4.dp)
                            ) {
                                eqRes.variables.forEach { v ->
                                    Text(
                                        text = v,
                                        modifier = Modifier.width(44.dp),
                                        textAlign = TextAlign.Center,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "A: ${eqRes.expr1}",
                                    modifier = Modifier.widthIn(min = 90.dp).padding(horizontal = 6.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "B: ${eqRes.expr2}",
                                    modifier = Modifier.widthIn(min = 90.dp).padding(horizontal = 6.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                    text = "A ↔ B",
                                    modifier = Modifier.width(60.dp),
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Rows
                            eqRes.rows.forEachIndexed { i, row ->
                                val rowBg = if (!row.matches) LogicFalse.copy(alpha = 0.1f)
                                else if (i % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(rowBg, RoundedCornerShape(6.dp))
                                        .padding(vertical = 4.dp, horizontal = 4.dp)
                                ) {
                                    eqRes.variables.forEach { v ->
                                        Box(
                                            modifier = Modifier.width(44.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            ValueBadge(value = row.variableValues[v] ?: false)
                                        }
                                    }
                                    Box(
                                        modifier = Modifier.widthIn(min = 90.dp).padding(horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ValueBadge(value = row.val1)
                                    }
                                    Box(
                                        modifier = Modifier.widthIn(min = 90.dp).padding(horizontal = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        ValueBadge(value = row.val2)
                                    }
                                    Box(
                                        modifier = Modifier.width(60.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = if (row.matches) "✓" else "✗",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = if (row.matches) LogicTrue else LogicFalse
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Explanation Box
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = eqRes.explanation,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
