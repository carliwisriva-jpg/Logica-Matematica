package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.logic.AstNode
import com.example.core.logic.LogicEvaluator
import com.example.core.logic.LogicParseException
import com.example.core.logic.LogicParser
import com.example.ui.components.ClassificationBadge
import com.example.ui.components.LogicKeypad
import com.example.ui.components.ValueBadge
import com.example.ui.theme.LogicFalse
import com.example.ui.theme.LogicTrue

@Composable
fun AnalyzeScreen(
    initialExpression: String = "¬(p ∨ q) ↔ (¬p ∧ ¬q)",
    onSaveHistory: (String, String) -> Unit = { _, _ -> }
) {
    var expression by remember { mutableStateOf(initialExpression) }
    var parsedAst by remember { mutableStateOf<AstNode?>(null) }
    var detectedVariables by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var syntaxValid by remember { mutableStateOf(false) }

    // Live assignments for manual evaluation calculator
    val variableAssignments = remember { mutableStateMapOf<String, Boolean>() }
    var manualResult by remember { mutableStateOf<Boolean?>(null) }

    fun analyze() {
        errorMessage = null
        if (expression.isBlank()) {
            errorMessage = "Introduce una expresión lógica para analizar."
            parsedAst = null
            detectedVariables = emptyList()
            syntaxValid = false
            manualResult = null
            return
        }

        try {
            val parser = LogicParser()
            val ast = parser.parse(expression)
            val vars = LogicEvaluator.extractVariables(ast)
            parsedAst = ast
            detectedVariables = vars
            syntaxValid = true

            // Initialize assignments if not set
            vars.forEach { v ->
                if (!variableAssignments.containsKey(v)) {
                    variableAssignments[v] = true
                }
            }

            // Calculate manual value
            manualResult = LogicEvaluator.evaluate(ast, variableAssignments)
            onSaveHistory(expression, "Sintaxis válida, ${vars.size} variables ($vars)")
        } catch (e: LogicParseException) {
            errorMessage = e.message
            parsedAst = null
            detectedVariables = emptyList()
            syntaxValid = false
            manualResult = null
        } catch (e: Exception) {
            errorMessage = "Error de sintaxis: ${e.message ?: "Comprueba la estructura de operadores y paréntesis."}"
            parsedAst = null
            detectedVariables = emptyList()
            syntaxValid = false
            manualResult = null
        }
    }

    remember(Unit) {
        analyze()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Analizador y Calculadora Lógica",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Valida gramática, detecta variables y calcula el valor paso a paso.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Formula Input
        OutlinedTextField(
            value = expression,
            onValueChange = {
                expression = it
                analyze()
            },
            label = { Text("Expresión lógica") },
            placeholder = { Text("Ejemplo: (p → q) ∧ (q → r)") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("analyze_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            ),
            trailingIcon = {
                if (expression.isNotEmpty()) {
                    IconButton(
                        onClick = { expression = ""; analyze() }
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            isError = errorMessage != null
        )

        // Virtual Keypad
        LogicKeypad(
            onInsert = { sym ->
                expression += sym
                analyze()
            },
            onBackspace = {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    analyze()
                }
            },
            onClear = {
                expression = ""
                analyze()
            }
        )

        // Status Card
        if (syntaxValid && parsedAst != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Válido",
                            tint = LogicTrue,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "Sintaxis Correcta",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = LogicTrue
                        )
                    }

                    // Detected Variables
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Variables detectadas (${detectedVariables.size}):",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        detectedVariables.forEach { v ->
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = v,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }

                    // AST Notation with normalized parentheses
                    Text(
                        text = "Estructura formal con precedencia:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = parsedAst?.toPrettyString() ?: "",
                            modifier = Modifier.padding(12.dp),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            // Interactive Variable Values Calculator
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = "Calculadora de Asignaciones",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = "Cambia el valor de cada variable para ver el resultado de la expresión al instante:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Switches for each variable
                    detectedVariables.forEach { v ->
                        val currentVal = variableAssignments[v] ?: true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Variable $v",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                ValueBadge(value = currentVal)
                            }
                            Switch(
                                checked = currentVal,
                                onCheckedChange = { newVal ->
                                    variableAssignments[v] = newVal
                                    parsedAst?.let {
                                        manualResult = LogicEvaluator.evaluate(it, variableAssignments)
                                    }
                                }
                            )
                        }
                    }

                    // Result of assignment
                    manualResult?.let { result ->
                        Surface(
                            color = if (result) LogicTrue.copy(alpha = 0.12f) else LogicFalse.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp,
                                if (result) LogicTrue else LogicFalse
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Resultado para esta asignación:",
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    val assignmentStr = detectedVariables.joinToString(", ") {
                                        "$it=${if (variableAssignments[it] == true) "V" else "F"}"
                                    }
                                    Text(
                                        text = assignmentStr,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                ValueBadge(value = result, modifier = Modifier.size(38.dp))
                            }
                        }
                    }
                }
            }
        } else if (errorMessage != null) {
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

        Spacer(modifier = Modifier.height(24.dp))
    }
}
