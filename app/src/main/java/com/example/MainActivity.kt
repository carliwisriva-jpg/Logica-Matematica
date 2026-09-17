package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.data.AppRepository
import com.example.data.db.HistoryEntity
import com.example.data.db.UserProgressEntity
import com.example.ui.screens.AnalyzeScreen
import com.example.ui.screens.ArgumentsScreen
import com.example.ui.screens.EquivalenceScreen
import com.example.ui.screens.ExamScreen
import com.example.ui.screens.HelpScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LawsScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.ProgressScreen
import com.example.ui.screens.QuantifiersScreen
import com.example.ui.screens.ScreenDestination
import com.example.ui.screens.SimplifyScreen
import com.example.ui.screens.TheoryScreen
import com.example.ui.screens.TruthTableScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDark = isSystemInDarkTheme()
            var darkTheme by remember { mutableStateOf(systemDark) }

            MyApplicationTheme(darkTheme = darkTheme) {
                MainAppContent(
                    isDarkTheme = darkTheme,
                    onToggleDarkTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContent(
    isDarkTheme: Boolean,
    onToggleDarkTheme: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { AppRepository(context) }
    val coroutineScope = rememberCoroutineScope()

    val progress by repository.progress.collectAsState(initial = UserProgressEntity())
    val historyList by repository.allHistory.collectAsState(initial = emptyList())

    var currentDestination by remember { mutableStateOf(ScreenDestination.HOME) }
    var selectedFormulaForTable by remember { mutableStateOf("(p ∧ q) → r") }

    // Intercept back button if not in HOME
    BackHandler(enabled = currentDestination != ScreenDestination.HOME) {
        currentDestination = ScreenDestination.HOME
    }

    val topBarTitle = when (currentDestination) {
        ScreenDestination.HOME -> "Lógica Matemática"
        ScreenDestination.THEORY -> "Aprender Teoría"
        ScreenDestination.TRUTH_TABLE -> "Tabla de Verdad"
        ScreenDestination.ANALYZE -> "Analizador & Calculadora"
        ScreenDestination.SIMPLIFY -> "Simplificador Lógico"
        ScreenDestination.EQUIVALENCE -> "Equivalencias Lógicas"
        ScreenDestination.LAWS -> "Leyes Lógicas"
        ScreenDestination.ARGUMENTS -> "Argumentos e Inferencia"
        ScreenDestination.QUANTIFIERS -> "Cuantificadores"
        ScreenDestination.PRACTICE -> "Banco de Ejercicios"
        ScreenDestination.EXAM -> "Modo Examen"
        ScreenDestination.PROGRESS -> "Mi Progreso"
        ScreenDestination.HELP -> "Ayuda y Símbolos"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = topBarTitle,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    if (currentDestination != ScreenDestination.HOME) {
                        IconButton(
                            onClick = { currentDestination = ScreenDestination.HOME },
                            modifier = Modifier.testTag("btn_navigate_back")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás"
                            )
                        }
                    }
                },
                actions = {
                    // Dark / Light Mode Toggle
                    IconButton(
                        onClick = onToggleDarkTheme,
                        modifier = Modifier.testTag("btn_toggle_theme")
                    ) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Cambiar tema"
                        )
                    }

                    // Help button
                    if (currentDestination != ScreenDestination.HELP) {
                        IconButton(
                            onClick = { currentDestination = ScreenDestination.HELP },
                            modifier = Modifier.testTag("btn_help")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                contentDescription = "Ayuda"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = currentDestination == ScreenDestination.HOME,
                    onClick = { currentDestination = ScreenDestination.HOME },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") },
                    modifier = Modifier.testTag("nav_home")
                )
                NavigationBarItem(
                    selected = currentDestination == ScreenDestination.THEORY,
                    onClick = { currentDestination = ScreenDestination.THEORY },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "Aprender") },
                    label = { Text("Aprender") },
                    modifier = Modifier.testTag("nav_theory")
                )
                NavigationBarItem(
                    selected = currentDestination == ScreenDestination.TRUTH_TABLE,
                    onClick = { currentDestination = ScreenDestination.TRUTH_TABLE },
                    icon = { Icon(Icons.Default.Calculate, contentDescription = "Tabla") },
                    label = { Text("Tabla") },
                    modifier = Modifier.testTag("nav_table")
                )
                NavigationBarItem(
                    selected = currentDestination == ScreenDestination.PRACTICE,
                    onClick = { currentDestination = ScreenDestination.PRACTICE },
                    icon = { Icon(Icons.Default.Quiz, contentDescription = "Practicar") },
                    label = { Text("Practicar") },
                    modifier = Modifier.testTag("nav_practice")
                )
                NavigationBarItem(
                    selected = currentDestination == ScreenDestination.PROGRESS,
                    onClick = { currentDestination = ScreenDestination.PROGRESS },
                    icon = { Icon(Icons.Default.BarChart, contentDescription = "Progreso") },
                    label = { Text("Progreso") },
                    modifier = Modifier.testTag("nav_progress")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentDestination) {
                ScreenDestination.HOME -> {
                    HomeScreen(
                        progress = progress,
                        onNavigate = { dest -> currentDestination = dest },
                        onQuickFormula = { formula ->
                            selectedFormulaForTable = formula
                            currentDestination = ScreenDestination.TRUTH_TABLE
                        }
                    )
                }

                ScreenDestination.THEORY -> {
                    TheoryScreen(
                        onNavigateToTruthTable = { formula ->
                            selectedFormulaForTable = formula
                            currentDestination = ScreenDestination.TRUTH_TABLE
                        }
                    )
                }

                ScreenDestination.TRUTH_TABLE -> {
                    TruthTableScreen(
                        initialExpression = selectedFormulaForTable,
                        onSaveHistory = { expr, summary ->
                            coroutineScope.launch {
                                repository.recordHistory("TABLA", expr, summary)
                            }
                        }
                    )
                }

                ScreenDestination.ANALYZE -> {
                    AnalyzeScreen(
                        onSaveHistory = { expr, summary ->
                            coroutineScope.launch {
                                repository.recordHistory("ANALISIS", expr, summary)
                            }
                        }
                    )
                }

                ScreenDestination.SIMPLIFY -> {
                    SimplifyScreen(
                        onSaveHistory = { expr, summary ->
                            coroutineScope.launch {
                                repository.recordHistory("SIMPLIFICACION", expr, summary)
                            }
                        }
                    )
                }

                ScreenDestination.EQUIVALENCE -> {
                    EquivalenceScreen(
                        onSaveHistory = { expr, summary ->
                            coroutineScope.launch {
                                repository.recordHistory("EQUIVALENCIA", expr, summary)
                            }
                        }
                    )
                }

                ScreenDestination.LAWS -> {
                    LawsScreen(
                        onTestFormula = { formula ->
                            selectedFormulaForTable = formula
                            currentDestination = ScreenDestination.TRUTH_TABLE
                        }
                    )
                }

                ScreenDestination.ARGUMENTS -> {
                    ArgumentsScreen(
                        onTestFormula = { formula ->
                            selectedFormulaForTable = formula
                            currentDestination = ScreenDestination.TRUTH_TABLE
                        }
                    )
                }

                ScreenDestination.QUANTIFIERS -> {
                    QuantifiersScreen()
                }

                ScreenDestination.PRACTICE -> {
                    PracticeScreen(
                        onQuestionAnswered = { isCorrect ->
                            coroutineScope.launch {
                                repository.recordQuestionAnswer(isCorrect, progress)
                            }
                        }
                    )
                }

                ScreenDestination.EXAM -> {
                    ExamScreen(
                        onExamFinished = { scorePercent ->
                            coroutineScope.launch {
                                repository.recordExamCompleted(scorePercent, progress)
                            }
                        }
                    )
                }

                ScreenDestination.PROGRESS -> {
                    ProgressScreen(
                        progress = progress,
                        historyList = historyList,
                        onClearHistory = {
                            coroutineScope.launch {
                                repository.clearAllHistory()
                            }
                        },
                        onDeleteHistoryItem = { id ->
                            coroutineScope.launch {
                                repository.deleteHistory(id)
                            }
                        },
                        onReuseExpression = { expr ->
                            selectedFormulaForTable = expr
                            currentDestination = ScreenDestination.TRUTH_TABLE
                        }
                    )
                }

                ScreenDestination.HELP -> {
                    HelpScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Lógica Matemática: $name", modifier = modifier)
}

