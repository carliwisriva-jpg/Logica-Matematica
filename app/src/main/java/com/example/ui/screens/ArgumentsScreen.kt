package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.example.core.logic.ArgumentValidationResult
import com.example.core.logic.InferenceRule
import com.example.core.logic.LogicArguments
import com.example.core.logic.LogicParseException
import com.example.ui.components.LogicKeypad
import com.example.ui.theme.LogicFalse
import com.example.ui.theme.LogicTrue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ArgumentsScreen(
    onTestFormula: (String) -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Tester, 1: Reference Rules

    // Argument tester state
    val premises = remember { mutableStateListOf("p → q", "p") }
    var conclusion by remember { mutableStateOf("q") }
    var activePremiseIndex by remember { mutableIntStateOf(0) }
    var editingConclusion by remember { mutableStateOf(false) }
    var validationResult by remember { mutableStateOf<ArgumentValidationResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun runValidation() {
        errorMessage = null
        if (premises.isEmpty() || premises.any { it.isBlank() } || conclusion.isBlank()) {
            errorMessage = "Introduce al menos una premisa válida y una conclusión."
            validationResult = null
            return
        }
        try {
            val res = LogicArguments.validateArgument(premises.toList(), conclusion)
            validationResult = res
        } catch (e: LogicParseException) {
            errorMessage = e.message
            validationResult = null
        } catch (e: Exception) {
            errorMessage = "Error al analizar argumento: ${e.message}"
            validationResult = null
        }
    }

    remember(Unit) {
        runValidation()
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
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Argumentos y Reglas de Inferencia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Comprueba la validez de deducciones lógicas y consulta las reglas formales.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Tab Selector
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = androidx.compose.ui.graphics.Color.Transparent
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Validador de Argumentos", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Reglas Clásicas (${LogicArguments.standardRules.size})", fontWeight = FontWeight.Bold) }
                )
            }
        }

        if (selectedTab == 0) {
            // Presets
            Text(
                text = "Ejemplos clásicos:",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LogicArguments.standardRules.take(4).forEach { rule ->
                    AssistChip(
                        onClick = {
                            premises.clear()
                            premises.addAll(rule.premises)
                            conclusion = rule.conclusion
                            runValidation()
                        },
                        label = { Text(rule.name.split("(")[0].trim()) }
                    )
                }
            }

            // Premises List
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Premisas:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    premises.forEachIndexed { index, premise ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = premise,
                                onValueChange = {
                                    premises[index] = it
                                    errorMessage = null
                                },
                                label = { Text("Premisa ${index + 1}") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("premise_input_$index"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp),
                                textStyle = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace),
                                trailingIcon = {
                                    IconButton(onClick = { activePremiseIndex = index; editingConclusion = false }) {
                                        Text(
                                            "P${index + 1}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (!editingConclusion && activePremiseIndex == index) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            )

                            if (premises.size > 1) {
                                IconButton(
                                    onClick = {
                                        premises.removeAt(index)
                                        if (activePremiseIndex >= premises.size) {
                                            activePremiseIndex = premises.size - 1
                                        }
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }

                    // Add premise button
                    if (premises.size < 4) {
                        Button(
                            onClick = {
                                premises.add("r")
                                activePremiseIndex = premises.size - 1
                                editingConclusion = false
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Añadir Premisa")
                        }
                    }

                    // Conclusion Field
                    OutlinedTextField(
                        value = conclusion,
                        onValueChange = { conclusion = it; errorMessage = null },
                        label = { Text("Conclusión (∴)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("conclusion_input"),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        textStyle = MaterialTheme.typography.titleMedium.copy(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
                        trailingIcon = {
                            IconButton(onClick = { editingConclusion = true }) {
                                Text(
                                    "∴ C",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (editingConclusion) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )
                }
            }

            // Keypad
            LogicKeypad(
                onInsert = { sym ->
                    if (editingConclusion) {
                        conclusion += sym
                    } else if (activePremiseIndex in premises.indices) {
                        premises[activePremiseIndex] = premises[activePremiseIndex] + sym
                    }
                    errorMessage = null
                },
                onBackspace = {
                    if (editingConclusion && conclusion.isNotEmpty()) {
                        conclusion = conclusion.dropLast(1)
                    } else if (activePremiseIndex in premises.indices && premises[activePremiseIndex].isNotEmpty()) {
                        premises[activePremiseIndex] = premises[activePremiseIndex].dropLast(1)
                    }
                    errorMessage = null
                },
                onClear = {
                    if (editingConclusion) conclusion = ""
                    else if (activePremiseIndex in premises.indices) premises[activePremiseIndex] = ""
                    errorMessage = null
                    validationResult = null
                }
            )

            // Validate Action
            Button(
                onClick = { runValidation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_validate_argument"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Validar Argumento", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text(errorMessage ?: "", color = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
            }

            // Result
            validationResult?.let { res ->
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
                        Surface(
                            color = if (res.isValid) LogicTrue.copy(alpha = 0.12f) else LogicFalse.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (res.isValid) LogicTrue else LogicFalse),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = if (res.isValid) Icons.Default.CheckCircle else Icons.Default.HighlightOff,
                                    contentDescription = null,
                                    tint = if (res.isValid) LogicTrue else LogicFalse,
                                    modifier = Modifier.size(32.dp)
                                )
                                Column {
                                    Text(
                                        text = if (res.isValid) "ARGUMENTO VÁLIDO" else "ARGUMENTO INVÁLIDO (FALACIA)",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = if (res.isValid) LogicTrue else LogicFalse
                                    )
                                    res.recognizedRule?.let { rule ->
                                        Text(
                                            text = "Regla identificada: ${rule.name}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }

                        // Formula unificada
                        Text(
                            text = "Fórmula condicional asociada:",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = res.combinedFormula,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Button(
                                    onClick = { onTestFormula(res.combinedFormula) },
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("Ver Tabla", fontSize = 11.sp)
                                }
                            }
                        }

                        Text(
                            text = res.explanation,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            // Standard Rules List
            LogicArguments.standardRules.forEach { rule ->
                RuleCard(rule = rule, onTestFormula = onTestFormula)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RuleCard(
    rule: InferenceRule,
    onTestFormula: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = rule.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (rule.latinName.isNotEmpty()) {
                        Text(
                            text = rule.latinName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Button(
                    onClick = { onTestFormula(rule.tautologyFormula) },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Verificar Tautología", fontSize = 11.sp)
                }
            }

            // Symbolic form
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = rule.symbolicForm,
                    modifier = Modifier.padding(12.dp),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Text(
                text = rule.explanation,
                style = MaterialTheme.typography.bodyMedium
            )

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = rule.example,
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
