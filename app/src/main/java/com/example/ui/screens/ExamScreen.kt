package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Difficulty
import com.example.data.Exercise
import com.example.data.ExerciseRepository
import com.example.ui.theme.LogicFalse
import com.example.ui.theme.LogicTrue
import kotlinx.coroutines.delay

enum class ExamState {
    CONFIG,
    IN_PROGRESS,
    FINISHED
}

@Composable
fun ExamScreen(
    onExamFinished: (Int) -> Unit = {}
) {
    var examState by remember { mutableStateOf(ExamState.CONFIG) }

    // Configuration
    var questionCount by remember { mutableIntStateOf(5) }
    var selectedDifficulty by remember { mutableStateOf<Difficulty?>(null) } // null = Mixto
    var timeLimitMinutes by remember { mutableIntStateOf(5) } // 0 = sin límite

    // Active Exam Data
    var examQuestions by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    val userAnswers = remember { mutableStateMapOf<Int, Int>() } // questionIndex -> optionIndex
    var remainingSeconds by remember { mutableIntStateOf(300) }

    // Timer countdown
    LaunchedEffect(examState, remainingSeconds) {
        if (examState == ExamState.IN_PROGRESS && timeLimitMinutes > 0 && remainingSeconds > 0) {
            delay(1000L)
            remainingSeconds--
            if (remainingSeconds <= 0) {
                // Time's up -> Finish exam
                examState = ExamState.FINISHED
                val score = calculateScore(examQuestions, userAnswers)
                onExamFinished(score)
            }
        }
    }

    fun startExam() {
        examQuestions = ExerciseRepository.generateExam(questionCount, selectedDifficulty)
        currentQuestionIndex = 0
        userAnswers.clear()
        remainingSeconds = if (timeLimitMinutes > 0) timeLimitMinutes * 60 else 0
        examState = ExamState.IN_PROGRESS
    }

    fun submitExam() {
        examState = ExamState.FINISHED
        val score = calculateScore(examQuestions, userAnswers)
        onExamFinished(score)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        when (examState) {
            ExamState.CONFIG -> {
                ExamConfigView(
                    questionCount = questionCount,
                    onQuestionCountChange = { questionCount = it },
                    selectedDifficulty = selectedDifficulty,
                    onDifficultyChange = { selectedDifficulty = it },
                    timeLimitMinutes = timeLimitMinutes,
                    onTimeLimitChange = { timeLimitMinutes = it },
                    onStart = { startExam() }
                )
            }
            ExamState.IN_PROGRESS -> {
                ExamInProgressView(
                    questions = examQuestions,
                    currentIndex = currentQuestionIndex,
                    userAnswers = userAnswers,
                    remainingSeconds = remainingSeconds,
                    hasTimer = timeLimitMinutes > 0,
                    onSelectOption = { qIdx, optIdx ->
                        userAnswers[qIdx] = optIdx
                    },
                    onNext = {
                        if (currentQuestionIndex + 1 < examQuestions.size) {
                            currentQuestionIndex++
                        }
                    },
                    onPrev = {
                        if (currentQuestionIndex > 0) {
                            currentQuestionIndex--
                        }
                    },
                    onSubmit = { submitExam() }
                )
            }
            ExamState.FINISHED -> {
                val scorePercent = calculateScore(examQuestions, userAnswers)
                ExamResultsView(
                    questions = examQuestions,
                    userAnswers = userAnswers,
                    scorePercent = scorePercent,
                    onRestart = { examState = ExamState.CONFIG }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun calculateScore(questions: List<Exercise>, userAnswers: Map<Int, Int>): Int {
    if (questions.isEmpty()) return 0
    var correct = 0
    questions.forEachIndexed { idx, q ->
        if (userAnswers[idx] == q.correctIndex) {
            correct++
        }
    }
    return (correct * 100) / questions.size
}

@Composable
fun ExamConfigView(
    questionCount: Int,
    onQuestionCountChange: (Int) -> Unit,
    selectedDifficulty: Difficulty?,
    onDifficultyChange: (Difficulty?) -> Unit,
    timeLimitMinutes: Int,
    onTimeLimitChange: (Int) -> Unit,
    onStart: () -> Unit
) {
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
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column {
                Text(
                    text = "Modo Examen",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Configura un test cronometrado para evaluar tus conocimientos de lógica.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Number of questions
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Número de preguntas:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15).forEach { count ->
                        FilterChip(
                            selected = questionCount == count,
                            onClick = { onQuestionCountChange(count) },
                            label = { Text("$count preguntas") },
                            modifier = Modifier.testTag("exam_count_$count")
                        )
                    }
                }
            }

            // Difficulty
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Nivel de dificultad:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedDifficulty == null,
                        onClick = { onDifficultyChange(null) },
                        label = { Text("Mixto (Aleatorio)") }
                    )
                    Difficulty.values().forEach { diff ->
                        FilterChip(
                            selected = selectedDifficulty == diff,
                            onClick = { onDifficultyChange(diff) },
                            label = { Text(diff.displayName) }
                        )
                    }
                }
            }

            // Time Limit
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Límite de tiempo:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0 to "Sin límite", 3 to "3 min", 5 to "5 min", 10 to "10 min").forEach { (min, label) ->
                        FilterChip(
                            selected = timeLimitMinutes == min,
                            onClick = { onTimeLimitChange(min) },
                            label = { Text(label) }
                        )
                    }
                }
            }

            Button(
                onClick = onStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("btn_start_exam"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Comenzar Examen", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ExamInProgressView(
    questions: List<Exercise>,
    currentIndex: Int,
    userAnswers: Map<Int, Int>,
    remainingSeconds: Int,
    hasTimer: Boolean,
    onSelectOption: (Int, Int) -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onSubmit: () -> Unit
) {
    val currentExercise = questions[currentIndex]
    val selectedOption = userAnswers[currentIndex]

    // Top status: Timer + Question Progress
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
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
                Text(
                    text = "Pregunta ${currentIndex + 1} de ${questions.size}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                if (hasTimer) {
                    val mins = remainingSeconds / 60
                    val secs = remainingSeconds % 60
                    val timerColor = if (remainingSeconds < 60) LogicFalse else MaterialTheme.colorScheme.primary

                    Surface(
                        color = timerColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.Alarm, contentDescription = null, tint = timerColor, modifier = Modifier.size(16.dp))
                            Text(
                                text = String.format("%02d:%02d", mins, secs),
                                fontWeight = FontWeight.Bold,
                                color = timerColor,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            LinearProgressIndicator(
                progress = { (currentIndex + 1).toFloat() / questions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
        }
    }

    // Question content card
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = currentExercise.question,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp
            )

            currentExercise.formula?.let { form ->
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = form,
                        modifier = Modifier.padding(12.dp),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentExercise.options.forEachIndexed { optIndex, optionText ->
                    val isSelected = selectedOption == optIndex
                    val (bgColor, borderColor) = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) to MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f) to MaterialTheme.colorScheme.outlineVariant
                    }

                    Surface(
                        color = bgColor,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.5.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOption(currentIndex, optIndex) }
                            .testTag("exam_opt_$optIndex")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = borderColor.copy(alpha = 0.2f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${('A' + optIndex)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            Text(
                                text = optionText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Bottom Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onPrev,
                    enabled = currentIndex > 0,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Anterior")
                }

                if (currentIndex + 1 < questions.size) {
                    Button(
                        onClick = onNext,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_exam_next")
                    ) {
                        Text("Siguiente")
                    }
                } else {
                    Button(
                        onClick = onSubmit,
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = LogicTrue
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_exam_finish")
                    ) {
                        Icon(Icons.Default.Done, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Finalizar Examen")
                    }
                }
            }
        }
    }
}

@Composable
fun ExamResultsView(
    questions: List<Exercise>,
    userAnswers: Map<Int, Int>,
    scorePercent: Int,
    onRestart: () -> Unit
) {
    val correctCount = questions.filterIndexed { idx, q -> userAnswers[idx] == q.correctIndex }.size
    val isPassed = scorePercent >= 60

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isPassed) LogicTrue.copy(alpha = 0.12f) else LogicFalse.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, if (isPassed) LogicTrue else LogicFalse),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (isPassed) LogicTrue else LogicFalse,
                modifier = Modifier.size(54.dp)
            )

            Text(
                text = if (scorePercent >= 90) "¡Excelente Desempeño!"
                else if (isPassed) "¡Examen Aprobado!"
                else "Necesitas Reforzar Conceptos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = if (isPassed) LogicTrue else LogicFalse
            )

            Text(
                text = "$scorePercent%",
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = if (isPassed) LogicTrue else LogicFalse
            )

            Text(
                text = "$correctCount correctas de ${questions.size} preguntas",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_retry_exam"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Replay, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Nuevo Examen", fontWeight = FontWeight.Bold)
            }
        }
    }

    Text(
        text = "Revisión Detallada de Respuestas:",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )

    // Review each question
    questions.forEachIndexed { index, q ->
        val chosen = userAnswers[index]
        val isCorrect = chosen == q.correctIndex

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
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
                    Text(
                        text = "Pregunta ${index + 1}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (isCorrect) "Correcta (+1)" else "Incorrecta (0)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (isCorrect) LogicTrue else LogicFalse
                    )
                }

                Text(text = q.question, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)

                q.formula?.let { form ->
                    Text(
                        text = form,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = "Tu respuesta: ${if (chosen != null) q.options[chosen] else "Sin responder"}",
                    color = if (isCorrect) LogicTrue else LogicFalse,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                if (!isCorrect) {
                    Text(
                        text = "Respuesta correcta: ${q.options[q.correctIndex]}",
                        color = LogicTrue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Explicación: ${q.explanation}",
                        modifier = Modifier.padding(10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
