package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.UserProgressEntity

enum class ScreenDestination {
    HOME,
    THEORY,
    TRUTH_TABLE,
    ANALYZE,
    SIMPLIFY,
    EQUIVALENCE,
    LAWS,
    ARGUMENTS,
    QUANTIFIERS,
    PRACTICE,
    EXAM,
    PROGRESS,
    HELP
}

data class QuickActionItem(
    val destination: ScreenDestination,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val badge: String,
    val containerColor: Color,
    val iconColor: Color
)

@Composable
fun HomeScreen(
    progress: UserProgressEntity,
    onNavigate: (ScreenDestination) -> Unit,
    onQuickFormula: (String) -> Unit
) {
    val quickActions = listOf(
        QuickActionItem(
            destination = ScreenDestination.THEORY,
            title = "Aprender",
            description = "Conceptos, conectores y definiciones teóricas",
            icon = Icons.Default.MenuBook,
            badge = "Teoría",
            containerColor = Color(0xFFE0F2FE),
            iconColor = Color(0xFF0284C7)
        ),
        QuickActionItem(
            destination = ScreenDestination.TRUTH_TABLE,
            title = "Tabla de Verdad",
            description = "Generador paso a paso de tablas completas",
            icon = Icons.Default.Calculate,
            badge = "Herramienta",
            containerColor = Color(0xFFEDE9FE),
            iconColor = Color(0xFF7C3AED)
        ),
        QuickActionItem(
            destination = ScreenDestination.ANALYZE,
            title = "Analizador & Calculadora",
            description = "Valida sintaxis y calcula asignaciones en vivo",
            icon = Icons.Default.Search,
            badge = "Calculadora",
            containerColor = Color(0xFFDCFCE7),
            iconColor = Color(0xFF16A34A)
        ),
        QuickActionItem(
            destination = ScreenDestination.SIMPLIFY,
            title = "Simplificador",
            description = "Simplifica expresiones paso a paso con leyes",
            icon = Icons.Default.AutoAwesome,
            badge = "Paso a paso",
            containerColor = Color(0xFFFEF3C7),
            iconColor = Color(0xFFD97706)
        ),
        QuickActionItem(
            destination = ScreenDestination.EQUIVALENCE,
            title = "Equivalencias",
            description = "Compara dos expresiones con doble tabla",
            icon = Icons.Default.CompareArrows,
            badge = "Demostración",
            containerColor = Color(0xFFFCE7F3),
            iconColor = Color(0xFFDB2777)
        ),
        QuickActionItem(
            destination = ScreenDestination.LAWS,
            title = "Leyes de la Lógica",
            description = "Biblioteca consultable con ejemplos de uso",
            icon = Icons.Default.AutoStories,
            badge = "Biblioteca",
            containerColor = Color(0xFFE0E7FF),
            iconColor = Color(0xFF4F46E5)
        ),
        QuickActionItem(
            destination = ScreenDestination.ARGUMENTS,
            title = "Inferencia & Argumentos",
            description = "Modus Ponens, Tollens y validador de premisas",
            icon = Icons.Default.Psychology,
            badge = "Deducción",
            containerColor = Color(0xFFCCFBF1),
            iconColor = Color(0xFF0D9488)
        ),
        QuickActionItem(
            destination = ScreenDestination.QUANTIFIERS,
            title = "Cuantificadores",
            description = "Lógica de predicados, ∀, ∃ y dominios",
            icon = Icons.Default.AutoAwesome,
            badge = "Predicados",
            containerColor = Color(0xFFFFEDD5),
            iconColor = Color(0xFFEA580C)
        ),
        QuickActionItem(
            destination = ScreenDestination.PRACTICE,
            title = "Practicar",
            description = "Banco de ejercicios con explicaciones inmediatas",
            icon = Icons.Default.Quiz,
            badge = "Ejercicios",
            containerColor = Color(0xFFE0F2FE),
            iconColor = Color(0xFF0369A1)
        ),
        QuickActionItem(
            destination = ScreenDestination.EXAM,
            title = "Modo Examen",
            description = "Evaluación cronometrada con puntaje y revisión",
            icon = Icons.Default.Assignment,
            badge = "Test",
            containerColor = Color(0xFFFEE2E2),
            iconColor = Color(0xFFDC2626)
        ),
        QuickActionItem(
            destination = ScreenDestination.PROGRESS,
            title = "Mi Progreso",
            description = "Estadísticas, medallas e historial guardado",
            icon = Icons.Default.BarChart,
            badge = "Estadísticas",
            containerColor = Color(0xFFF1F5F9),
            iconColor = Color(0xFF475569)
        ),
        QuickActionItem(
            destination = ScreenDestination.HELP,
            title = "Ayuda y Símbolos",
            description = "Guía de notación, teclado y errores frecuentes",
            icon = Icons.Default.HelpOutline,
            badge = "Guía",
            containerColor = Color(0xFFF5F3FF),
            iconColor = Color(0xFF6D28D9)
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("hero_banner"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "LÓGICA MATEMÁTICA",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }

                    Text(
                        text = "p ∧ q → r",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Aprende, calcula y domina la Lógica Proposicional",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    lineHeight = 28.sp
                )

                Text(
                    text = "Genera tablas de verdad en milisegundos, simplifica con leyes formales, valida silogismos y evalúa tu comprensión.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                )

                // Quick stats pill
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ejercicios resueltos: ${progress.questionsAnswered}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Aciertos: ${progress.questionsCorrect}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Section Title
        Text(
            text = "Módulos y Herramientas:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        // 2-Column Grid of Modules
        for (i in quickActions.indices step 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HomeModuleCard(
                    item = quickActions[i],
                    onClick = { onNavigate(quickActions[i].destination) },
                    modifier = Modifier.weight(1f)
                )
                if (i + 1 < quickActions.size) {
                    HomeModuleCard(
                        item = quickActions[i + 1],
                        onClick = { onNavigate(quickActions[i + 1].destination) },
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun HomeModuleCard(
    item: QuickActionItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .testTag("home_card_${item.destination.name.lowercase()}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = item.containerColor,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = item.iconColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Surface(
                    color = item.containerColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = item.badge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = item.iconColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp,
                maxLines = 2
            )
        }
    }
}
