package com.example.data

enum class ExerciseCategory(val displayName: String, val icon: String) {
    TODAS("Todas", "⭐"),
    PROPOSICIONES("Proposiciones", "💬"),
    CONECTORES("Conectores", "⚡"),
    TABLAS_VERDAD("Tablas de Verdad", "🧮"),
    CLASIFICACION("Tautologías y Contradicciones", "⚖️"),
    EQUIVALENCIAS("Equivalencias", "🔄"),
    LEYES_LOGICAS("Leyes Lógicas", "📖"),
    SIMPLIFICACION("Simplificación", "✨"),
    REGLAS_INFERENCIA("Reglas de Inferencia", "🧠"),
    CUANTIFICADORES("Cuantificadores", "∀"),
    PREDICADOS("Predicados", "🎯")
}

enum class Difficulty(val displayName: String, val badgeColor: Long) {
    FACIL("Fácil", 0xFF2E7D32),
    INTERMEDIO("Intermedio", 0xFFE65100),
    DIFICIL("Difícil", 0xFFC2185B)
}

data class Exercise(
    val id: String,
    val category: ExerciseCategory,
    val difficulty: Difficulty,
    val question: String,
    val formula: String? = null,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val procedureSteps: List<String> = emptyList()
)

object ExerciseRepository {

    val allExercises: List<Exercise> = listOf(
        // Category: PROPOSICIONES
        Exercise(
            id = "prop_1",
            category = ExerciseCategory.PROPOSICIONES,
            difficulty = Difficulty.FACIL,
            question = "¿Cuál de las siguientes oraciones es una PROPOSICIÓN lógica válida?",
            options = listOf(
                "¿A qué hora empieza la clase?",
                "El número 17 es primo.",
                "¡Por favor, guarda silencio!",
                "x + 3 = 8"
            ),
            correctIndex = 1,
            explanation = "Una proposición lógica debe ser una oración declarativa de la cual se pueda determinar sin ambigüedad si es Verdadera o Falsa. 'El número 17 es primo' es una afirmación declarativa y verdadera.",
            procedureSteps = listOf(
                "Analizamos opción 1: Pregunta interrogativa (no tiene valor de verdad).",
                "Analizamos opción 2: '17 es primo' es un enunciado declarativo con valor de verdad V.",
                "Analizamos opción 3: Oración imperativa/exclamativa.",
                "Analizamos opción 4: Enunciado abierto (depende de x)."
            )
        ),
        Exercise(
            id = "prop_2",
            category = ExerciseCategory.PROPOSICIONES,
            difficulty = Difficulty.FACIL,
            question = "Si una fórmula tiene 3 variables proposicionales independientes (p, q, r), ¿cuántas filas tendrá su tabla de verdad?",
            options = listOf("6 filas", "8 filas", "9 filas", "16 filas"),
            correctIndex = 1,
            explanation = "El número de combinaciones de verdad para n variables independientes se calcula mediante la fórmula 2ⁿ. Para n = 3: 2³ = 8 filas.",
            procedureSteps = listOf(
                "Fórmula: Número de filas = 2ⁿ",
                "Sustituimos n = 3 variables",
                "Cálculo: 2 × 2 × 2 = 8 filas"
            )
        ),

        // Category: CONECTORES
        Exercise(
            id = "conn_1",
            category = ExerciseCategory.CONECTORES,
            difficulty = Difficulty.FACIL,
            question = "¿En qué único caso la implicación condicional (p → q) resulta FALSA?",
            options = listOf(
                "Cuando p es Falso y q es Falso",
                "Cuando p es Falso y q es Verdadero",
                "Cuando p es Verdadero y q es Falso",
                "Cuando p es Verdadero y q es Verdadero"
            ),
            correctIndex = 2,
            explanation = "La implicación p → q solo se rompe cuando la premisa/antecedente se cumple (p = V) pero la consecuencia o promesa no se cumple (q = F). En los demás casos es Verdadera.",
            procedureSteps = listOf(
                "Revisamos tabla de verdad de p → q:",
                "V → V = V",
                "V → F = F (Único caso Falso)",
                "F → V = V",
                "F → F = V"
            )
        ),
        Exercise(
            id = "conn_2",
            category = ExerciseCategory.CONECTORES,
            difficulty = Difficulty.INTERMEDIO,
            question = "Si el valor de verdad de p es V y el de q es F, ¿cuál es el valor de: ¬(p ∧ q) → (p ⊕ q)?",
            options = listOf("Verdadero (V)", "Falso (F)"),
            correctIndex = 0,
            explanation = "Evaluamos paso a paso:\n1) (p ∧ q) = V ∧ F = F\n2) ¬(p ∧ q) = ¬F = V (Antecedente = V)\n3) (p ⊕ q) = V ⊕ F = V (Consecuente = V)\n4) V → V = Verdadero (V).",
            procedureSteps = listOf(
                "Antecedente: p ∧ q = V ∧ F = F. Luego ¬(F) = V.",
                "Consecuente: p ⊕ q = V ⊕ F = V (difieren, por tanto XOR es V).",
                "Condicional global: V → V = V."
            )
        ),

        // Category: TABLAS_VERDAD
        Exercise(
            id = "tv_1",
            category = ExerciseCategory.TABLAS_VERDAD,
            difficulty = Difficulty.FACIL,
            question = "¿Cuál es el valor final de la expresión (p ∨ q) cuando p = F y q = V?",
            options = listOf("Falso (F)", "Verdadero (V)"),
            correctIndex = 1,
            explanation = "En la disyunción inclusiva (∨), basta con que al menos una de las proposiciones sea Verdadera para que toda la disyunción sea Verdadera.",
            procedureSteps = listOf(
                "Sustitución: F ∨ V",
                "Regla de la disyunción: 0 o 1 = 1",
                "Resultado = Verdadero (V)"
            )
        ),
        Exercise(
            id = "tv_2",
            category = ExerciseCategory.TABLAS_VERDAD,
            difficulty = Difficulty.INTERMEDIO,
            question = "Para la expresión (p ∧ q) ↔ (q ∧ p), ¿cuál es el resultado de su tabla de verdad?",
            options = listOf(
                "Contradicción (todo F)",
                "Contingencia (mezcla de V y F)",
                "Tautología (todo V)",
                "Depende del valor de p"
            ),
            correctIndex = 2,
            explanation = "Por la ley conmutativa de la conjunción, (p ∧ q) y (q ∧ p) siempre tienen el mismo valor de verdad. Por tanto, el bicondicional (↔) entre ambas siempre es Verdadero (Tautología).",
            procedureSteps = listOf(
                "Fila 1 (p=V, q=V): V ↔ V = V",
                "Fila 2 (p=V, q=F): F ↔ F = V",
                "Fila 3 (p=F, q=V): F ↔ F = V",
                "Fila 4 (p=F, q=F): F ↔ F = V",
                "Como todas las filas son V, es TAUTOLOGÍA."
            )
        ),

        // Category: CLASIFICACION
        Exercise(
            id = "clas_1",
            category = ExerciseCategory.CLASIFICACION,
            difficulty = Difficulty.FACIL,
            question = "¿Cómo se clasifica la proposición 'p ∨ ¬p'?",
            options = listOf("Contradicción", "Tautología", "Contingencia", "Paradoja"),
            correctIndex = 1,
            explanation = "Es el Principio del Tercero Excluido. Si p es V, p ∨ ¬p = V ∨ F = V. Si p es F, p ∨ ¬p = F ∨ V = V. Siempre es verdadera, por tanto es una Tautología.",
            procedureSteps = listOf(
                "Caso p = V: V ∨ ¬V = V ∨ F = V",
                "Caso p = F: F ∨ ¬F = F ∨ V = V",
                "Siempre resulta V -> TAUTOLOGÍA."
            )
        ),
        Exercise(
            id = "clas_2",
            category = ExerciseCategory.CLASIFICACION,
            difficulty = Difficulty.INTERMEDIO,
            question = "¿Cómo se clasifica la proposición (p → q) ∧ (p ∧ ¬q)?",
            options = listOf("Tautología", "Contingencia", "Contradicción", "Equivalencia"),
            correctIndex = 2,
            explanation = "Sabemos que (p → q) equivale a ¬(p ∧ ¬q). Por tanto la expresión es de la forma A ∧ ¬A, lo que siempre produce FALSO (Contradicción).",
            procedureSteps = listOf(
                "Identificamos que p → q es Falso exactamente cuando (p ∧ ¬q) es Verdadero.",
                "No pueden ser verdaderas simultáneamente.",
                "El resultado en todas las filas es F -> CONTRADICCIÓN."
            )
        ),

        // Category: EQUIVALENCIAS
        Exercise(
            id = "equiv_1",
            category = ExerciseCategory.EQUIVALENCIAS,
            difficulty = Difficulty.FACIL,
            question = "¿Cuál de las siguientes fórmulas es lógicamente equivalente a la implicación (p → q)?",
            options = listOf("¬p ∧ q", "¬p ∨ q", "p ∧ ¬q", "q → p"),
            correctIndex = 1,
            explanation = "Por la Ley del Condicional Material, p → q ≡ ¬p ∨ q. Ambas expresiones son falsas únicamente cuando p=V y q=F.",
            procedureSteps = listOf(
                "Revisamos valores de ¬p ∨ q para p=V, q=F: ¬V ∨ F = F ∨ F = F",
                "Para las demás combinaciones da V.",
                "Coincide exactamente con la tabla de p → q."
            )
        ),
        Exercise(
            id = "equiv_2",
            category = ExerciseCategory.EQUIVALENCIAS,
            difficulty = Difficulty.INTERMEDIO,
            question = "¿Cuál es la contrarrecíproca (lógicamente equivalente) de la afirmación: 'Si llueve (p), entonces el suelo se moja (q)'?",
            options = listOf(
                "Si el suelo se moja, entonces llueve (q → p)",
                "Si no llueve, entonces el suelo no se moja (¬p → ¬q)",
                "Si el suelo no se moja, entonces no llueve (¬q → ¬p)",
                "Llueve y el suelo no se moja (p ∧ ¬q)"
            ),
            correctIndex = 2,
            explanation = "La contrarrecíproca de p → q es ¬q → ¬p y es la única proposición derivada que siempre conserva el mismo valor de verdad que la original.",
            procedureSteps = listOf(
                "Original: p → q",
                "Recíproca: q → p (no equivalente)",
                "Inversa: ¬p → ¬q (no equivalente)",
                "Contrarrecíproca: ¬q → ¬p (Lógicamente equivalente a p → q)"
            )
        ),

        // Category: LEYES_LOGICAS
        Exercise(
            id = "ley_1",
            category = ExerciseCategory.LEYES_LOGICAS,
            difficulty = Difficulty.FACIL,
            question = "Según las Leyes de De Morgan, la expresión ¬(p ∧ q) equivale a:",
            options = listOf("¬p ∧ ¬q", "¬p ∨ ¬q", "p ∨ q", "¬p → ¬q"),
            correctIndex = 1,
            explanation = "La negación de una conjunción se convierte en la disyunción de las negaciones: ¬(p ∧ q) ≡ ¬p ∨ ¬q.",
            procedureSteps = listOf(
                "Regla de De Morgan 1: ¬(A ∧ B) ≡ ¬A ∨ ¬B",
                "El operador ∧ cambia a ∨ y se niega cada término."
            )
        ),
        Exercise(
            id = "ley_2",
            category = ExerciseCategory.LEYES_LOGICAS,
            difficulty = Difficulty.INTERMEDIO,
            question = "¿Qué ley lógica justifica que: p ∨ (p ∧ q) ≡ p?",
            options = listOf(
                "Ley Distributiva",
                "Ley de Idempotencia",
                "Ley de Absorción",
                "Ley de Dominación"
            ),
            correctIndex = 2,
            explanation = "La Ley de Absorción establece que p ∨ (p ∧ q) ≡ p y que p ∧ (p ∨ q) ≡ p. El término externo absorbe al término compuesto.",
            procedureSteps = listOf(
                "Si p = V: V ∨ (V ∧ q) = V ∨ q = V = p",
                "Si p = F: F ∨ (F ∧ q) = F ∨ F = F = p",
                "En ambos casos resulta el valor de p -> Ley de Absorción."
            )
        ),

        // Category: SIMPLIFICACION
        Exercise(
            id = "simp_1",
            category = ExerciseCategory.SIMPLIFICACION,
            difficulty = Difficulty.INTERMEDIO,
            question = "Al simplificar la expresión ¬(¬p ∨ q), ¿cuál es el resultado?",
            options = listOf("p ∨ ¬q", "p ∧ ¬q", "¬p ∧ ¬q", "p ∧ q"),
            correctIndex = 1,
            explanation = "Aplicamos De Morgan: ¬(¬p ∨ q) = ¬(¬p) ∧ ¬q. Luego por doble negación ¬(¬p) = p, resultando p ∧ ¬q.",
            procedureSteps = listOf(
                "Paso 1: Aplicar De Morgan -> ¬(¬p) ∧ ¬q",
                "Paso 2: Aplicar Doble Negación en ¬(¬p) -> p",
                "Resultado final: p ∧ ¬q"
            )
        ),
        Exercise(
            id = "simp_2",
            category = ExerciseCategory.SIMPLIFICACION,
            difficulty = Difficulty.DIFICIL,
            question = "Al simplificar completamente (p ∧ q) ∨ (p ∧ ¬q), obtenemos:",
            options = listOf("q", "p", "V (Verdadero)", "p ∧ q"),
            correctIndex = 1,
            explanation = "Por distributividad inversa (factorización): p ∧ (q ∨ ¬q). Por complemento, (q ∨ ¬q) = V. Por identidad, p ∧ V = p.",
            procedureSteps = listOf(
                "Paso 1 (Distributiva inversa): p ∧ (q ∨ ¬q)",
                "Paso 2 (Ley del Complemento): q ∨ ¬q = V",
                "Paso 3 (Ley de Identidad): p ∧ V = p",
                "Resultado final: p"
            )
        ),

        // Category: REGLAS_INFERENCIA
        Exercise(
            id = "inf_1",
            category = ExerciseCategory.REGLAS_INFERENCIA,
            difficulty = Difficulty.FACIL,
            question = "Dado el siguiente argumento:\nPremisa 1: p → q\nPremisa 2: p\nConclusión: ∴ q\n¿A qué regla de inferencia corresponde?",
            options = listOf(
                "Modus Tollens",
                "Silogismo Hipotético",
                "Modus Ponens",
                "Silogismo Disyuntivo"
            ),
            correctIndex = 2,
            explanation = "Modus Ponens (Modus Ponendo Ponens) afirma que si se cumple la implicación y se afirma el antecedente, se concluye necesariamente el consecuente.",
            procedureSteps = listOf(
                "Estructura: Si P entonces Q. P ocurre. Por lo tanto Q.",
                "Nombre formal: Modus Ponens."
            )
        ),
        Exercise(
            id = "inf_2",
            category = ExerciseCategory.REGLAS_INFERENCIA,
            difficulty = Difficulty.INTERMEDIO,
            question = "Dado el argumento:\nPremisa 1: p ∨ q\nPremisa 2: ¬p\nConclusión: ∴ q\n¿Qué regla de inferencia se aplicó?",
            options = listOf(
                "Modus Tollens",
                "Silogismo Disyuntivo",
                "Regla de Resolución",
                "Regla de Adición"
            ),
            correctIndex = 1,
            explanation = "El Silogismo Disyuntivo establece que si una de dos opciones debe ser verdadera (p ∨ q) y se descarta una (¬p), la otra (q) debe ser necesariamente la verdadera.",
            procedureSteps = listOf(
                "Estructura: p ∨ q, ¬p ∴ q",
                "Regla: Silogismo Disyuntivo (Modus Tollendo Ponens)."
            )
        ),

        // Category: CUANTIFICADORES
        Exercise(
            id = "cuant_1",
            category = ExerciseCategory.CUANTIFICADORES,
            difficulty = Difficulty.FACIL,
            question = "¿Cuál es la negación correcta de la proposición cuantificada: ∀x P(x)?",
            options = listOf(
                "∀x ¬P(x)",
                "∃x ¬P(x)",
                "¬∃x P(x)",
                "∃x P(x)"
            ),
            correctIndex = 1,
            explanation = "La negación de 'Para todo x se cumple P(x)' es 'Existe al menos un x tal que NO se cumple P(x)' (∃x ¬P(x)). Basta un contraejemplo para invalidar un universal.",
            procedureSteps = listOf(
                "Ley de negación: ¬[∀x P(x)] ≡ ∃x ¬P(x)",
                "El cuantificador cambia de ∀ a ∃ y se niega el predicado interior."
            )
        ),
        Exercise(
            id = "cuant_2",
            category = ExerciseCategory.CUANTIFICADORES,
            difficulty = Difficulty.INTERMEDIO,
            question = "Si el dominio son las personas y P(x) significa 'x es honesto', ¿cómo se traduce en lenguaje natural ¬∃x P(x)?",
            options = listOf(
                "Todas las personas son honestas.",
                "Existe al menos una persona honesta.",
                "Ninguna persona es honesta (Nadie es honesto).",
                "Algunas personas no son honestas."
            ),
            correctIndex = 2,
            explanation = "¬∃x P(x) significa 'No existe ninguna persona x que sea honesta', lo cual equivale a ∀x ¬P(x) ('Para toda persona, no es honesta' o 'Nadie es honesto').",
            procedureSteps = listOf(
                "∃x P(x) = 'Existe alguien honesto'",
                "¬∃x P(x) = 'No existe nadie honesto' ≡ 'Nadie es honesto'"
            )
        ),

        // Category: PREDICADOS
        Exercise(
            id = "pred_1",
            category = ExerciseCategory.PREDICADOS,
            difficulty = Difficulty.INTERMEDIO,
            question = "Sea el predicado P(x): 'x > 5'. Si el dominio son los números enteros {1, 2, 3, 4}, ¿cuál es el valor de verdad de ∃x P(x)?",
            options = listOf("Verdadero (V)", "Falso (F)"),
            correctIndex = 1,
            explanation = "En el dominio {1, 2, 3, 4}, ninguno de los números es mayor a 5 (P(1)=F, P(2)=F, P(3)=F, P(4)=F). Por tanto, no existe ningún x que cumpla la condición; la afirmación existencial es FALSA.",
            procedureSteps = listOf(
                "Comprobamos cada elemento del dominio:",
                "x=1: 1 > 5 (F)",
                "x=2: 2 > 5 (F)",
                "x=3: 3 > 5 (F)",
                "x=4: 4 > 5 (F)",
                "No hay ninguno verdadero -> ∃x P(x) es Falso (F)."
            )
        )
    )

    fun getFilteredExercises(category: ExerciseCategory, difficulty: Difficulty?): List<Exercise> {
        return allExercises.filter { ex ->
            (category == ExerciseCategory.TODAS || ex.category == category) &&
            (difficulty == null || ex.difficulty == difficulty)
        }
    }

    fun generateExam(questionCount: Int, difficulty: Difficulty?): List<Exercise> {
        val pool = if (difficulty == null) allExercises else allExercises.filter { it.difficulty == difficulty }
        val count = questionCount.coerceAtMost(pool.size)
        return pool.shuffled().take(count)
    }
}
