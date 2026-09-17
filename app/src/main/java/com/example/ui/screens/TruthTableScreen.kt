package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.logic.LogicEvaluator
import com.example.core.logic.LogicParseException
import com.example.core.logic.TruthTableResult
import com.example.ui.components.LogicKeypad
import com.example.ui.components.TruthTableView
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TruthTableScreen(
    initialExpression: String = "(p ∧ q) → r",
    onSaveHistory: (String, String) -> Unit = { _, _ -> }
) {
    var expression by remember { mutableStateOf(initialExpression) }
    var tableResult by remember { mutableStateOf<TruthTableResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isCalculating by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun calculateTable() {
        errorMessage = null
        if (expression.isBlank()) {
            errorMessage = "Introduce una expresión lógica válida."
            tableResult = null
            return
        }
        try {
            isCalculating = true
            val res = LogicEvaluator.generateTruthTable(expression)
            tableResult = res
            onSaveHistory(res.expression, "${res.classification.label} (${res.rows.size} filas)")
        } catch (e: LogicParseException) {
            errorMessage = e.message
            tableResult = null
        } catch (e: Exception) {
            errorMessage = "Error al evaluar expresión: ${e.localizedMessage ?: "Sintaxis inválida"}"
            tableResult = null
        } finally {
            isCalculating = false
        }
    }

    // Auto-calculate on initial load
    remember(Unit) {
        calculateTable()
    }

    val presets = listOf(
        "p ∨ ¬p" to "Tautología clásica",
        "p ∧ ¬p" to "Contradicción",
        "(p ∧ q) → r" to "3 variables",
        "¬p ∨ (q ∧ r)" to "Conectores mixtos",
        "(p → q) ∧ (q → r)" to "Silogismo"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title banner
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
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
                            imageVector = Icons.Default.Calculate,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Generador de Tablas de Verdad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Calcula todas las combinaciones, suboperaciones y clasifica la fórmula.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Input Field
        OutlinedTextField(
            value = expression,
            onValueChange = {
                expression = it
                errorMessage = null
            },
            label = { Text("Expresión lógica") },
            placeholder = { Text("Ejemplo: (p ∧ q) → r") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("expression_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold
            ),
            trailingIcon = {
                if (expression.isNotEmpty()) {
                    IconButton(
                        onClick = { expression = ""; errorMessage = null; tableResult = null },
                        modifier = Modifier.testTag("btn_clear_input")
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            isError = errorMessage != null
        )

        // Presets Chips
        Text(
            text = "Ejemplos rápidos:",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presets.forEach { (preset, label) ->
                AssistChip(
                    onClick = {
                        expression = preset
                        calculateTable()
                    },
                    label = { Text(preset, fontFamily = FontFamily.Monospace) }
                )
            }
        }

        // Virtual Keypad
        LogicKeypad(
            onInsert = { sym ->
                expression += sym
                errorMessage = null
            },
            onBackspace = {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    errorMessage = null
                }
            },
            onClear = {
                expression = ""
                errorMessage = null
                tableResult = null
            }
        )

        // Calculate Button
        Button(
            onClick = { calculateTable() },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_generate_table"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Generar Tabla de Verdad", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }

        // Error message if any
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

        // Table Result View
        if (isCalculating) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (tableResult != null) {
            TruthTableView(table = tableResult!!)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
