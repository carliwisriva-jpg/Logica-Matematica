package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.core.logic.Classification
import com.example.core.logic.TruthTableResult
import com.example.ui.theme.LogicContingency
import com.example.ui.theme.LogicContingencyLight
import com.example.ui.theme.LogicFalse
import com.example.ui.theme.LogicFalseLight
import com.example.ui.theme.LogicTrue
import com.example.ui.theme.LogicTrueLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LogicKeypad(
    onInsert: (String) -> Unit,
    onBackspace: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("logic_keypad"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Row 1: Logical Operators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val operators = listOf(
                    "¬" to "Negación",
                    "∧" to "Conjunción",
                    "∨" to "Disyunción",
                    "⊕" to "Disyunción Exclusiva",
                    "→" to "Implicación",
                    "↔" to "Bicondicional"
                )
                operators.forEach { (sym, desc) ->
                    Button(
                        onClick = { onInsert(sym) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("key_operator_$sym"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = sym,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Row 2: Variables, Parentheses & Constants
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val elements = listOf("p", "q", "r", "s", "(", ")", "V", "F")
                elements.forEach { elem ->
                    OutlinedButton(
                        onClick = { onInsert(elem) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("key_elem_$elem"),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = elem,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = when (elem) {
                                "V" -> LogicTrue
                                "F" -> LogicFalse
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }

                // Delete Button
                Button(
                    onClick = onBackspace,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(44.dp)
                        .testTag("key_backspace"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "Borrar",
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Clear All Button
                Button(
                    onClick = onClear,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(44.dp)
                        .testTag("key_clear"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("AC", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun ValueBadge(
    value: Boolean,
    modifier: Modifier = Modifier
) {
    val bg = if (value) LogicTrue.copy(alpha = 0.15f) else LogicFalse.copy(alpha = 0.15f)
    val textColor = if (value) LogicTrue else LogicFalse
    val symbol = if (value) "V" else "F"

    Box(
        modifier = modifier
            .size(32.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            color = textColor,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 15.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}

@Composable
fun ClassificationBadge(
    classification: Classification,
    modifier: Modifier = Modifier
) {
    val (color, icon) = when (classification) {
        Classification.TAUTOLOGIA -> LogicTrue to Icons.Default.CheckCircle
        Classification.CONTRADICCION -> LogicFalse to Icons.Default.ErrorOutline
        Classification.CONTINGENCIA -> LogicContingency to Icons.Default.Info
    }

    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, color)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = classification.label,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = classification.label.uppercase(),
                color = color,
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun TruthTableView(
    table: TruthTableResult,
    modifier: Modifier = Modifier
) {
    val horizontalScrollState = rememberScrollState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("truth_table_view"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with classification
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tabla de Verdad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${table.rows.size} combinaciones (${table.variables.size} variables)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                ClassificationBadge(classification = table.classification)
            }

            // Scrollable Grid Table
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
                    .horizontalScroll(horizontalScrollState)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Headers Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 8.dp, horizontal = 4.dp)
                    ) {
                        Text(
                            text = "#",
                            modifier = Modifier.width(36.dp),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        table.variables.forEach { v ->
                            Text(
                                text = v,
                                modifier = Modifier.width(44.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        table.intermediateExpressions.forEach { expr ->
                            Text(
                                text = expr,
                                modifier = Modifier
                                    .widthIn(min = 70.dp, max = 150.dp)
                                    .padding(horizontal = 4.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        // Main/Final column
                        Surface(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = table.expression,
                                modifier = Modifier
                                    .widthIn(min = 90.dp)
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Rows
                    table.rows.forEachIndexed { index, row ->
                        val rowBg = if (index % 2 == 0) Color.Transparent else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg, RoundedCornerShape(6.dp))
                                .padding(vertical = 4.dp, horizontal = 4.dp)
                        ) {
                            Text(
                                text = "${index + 1}",
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            table.variables.forEach { v ->
                                val vVal = row.variableValues[v] ?: false
                                Box(
                                    modifier = Modifier.width(44.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ValueBadge(value = vVal)
                                }
                            }

                            row.intermediateValues.forEach { iVal ->
                                Box(
                                    modifier = Modifier.widthIn(min = 70.dp, max = 150.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    ValueBadge(value = iVal)
                                }
                            }

                            // Final column value
                            Box(
                                modifier = Modifier
                                    .widthIn(min = 90.dp)
                                    .padding(horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ValueBadge(value = row.finalValue)
                            }
                        }
                    }
                }
            }

            // Explanation box
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Explicación",
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(
                        text = table.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
