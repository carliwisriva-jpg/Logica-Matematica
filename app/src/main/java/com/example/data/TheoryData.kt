package com.example.data

data class TheoryTopic(
    val id: String,
    val title: String,
    val summary: String,
    val icon: String,
    val sections: List<TheorySection>
)

data class TheorySection(
    val subtitle: String,
    val content: String,
    val formula: String? = null,
    val example: String? = null,
    val truthTableData: MiniTruthTable? = null,
    val keyTakeaways: List<String> = emptyList()
)

data class MiniTruthTable(
    val headers: List<String>,
    val rows: List<List<String>>
)

object TheoryRepository {

    val topics: List<TheoryTopic> = listOf(
        TheoryTopic(
            id = "intro",
            title = "1. Introducción a la Lógica",
            summary = "¿Qué es la lógica matemática, proposiciones simples y compuestas, y valores de verdad?",
            icon = "📚",
            sections = listOf(
                TheorySection(
                    subtitle = "¿Qué es la Lógica Matemática?",
                    content = "La lógica matemática es la disciplina que estudia los métodos y principios formales para distinguir el razonamiento válido del no válido. Proporciona un lenguaje simbólico exacto y reglas rigurosas que eliminan las ambigüedades inherentes al lenguaje natural.",
                    keyTakeaways = listOf(
                        "Elimina la ambigüedad de los idiomas cotidianos.",
                        "Fundamento de las matemáticas, la informática y los circuitos digitales.",
                        "Determina la validez de argumentos independientemente del contenido específico."
                    )
                ),
                TheorySection(
                    subtitle = "¿Qué es una Proposición?",
                    content = "Una proposición lógica es una oración declarativa de la cual se puede afirmar, sin ambigüedad alguna, que es exclusivamente VERDADERA (V) o FALSA (F), pero nunca ambas cosas a la vez (Principio del Tercero Excluido y Principio de No Contradicción).",
                    example = "Proposiciones válidas:\n• 'El número 7 es primo' (Verdadera)\n• 'La Luna es de queso' (Falsa)\n• '2 + 3 = 5' (Verdadera)\n\nNO son proposiciones:\n• '¿Qué hora es?' (Pregunta)\n• '¡Cierra la puerta!' (Orden)\n• 'x + 5 = 10' (Proposición abierta, depende de x)\n• 'Esta afirmación es falsa' (Paradoja)"
                ),
                TheorySection(
                    subtitle = "Proposiciones Simples vs Compuestas",
                    content = "• Proposiciones Simples (Atómicas): Expresan una sola idea o hecho básico sin conectores lógicos.\n• Proposiciones Compuestas (Moleculares): Resultan de unir dos o más proposiciones simples mediante operadores o conectores lógicos (o al aplicar la negación a una proposición).",
                    example = "Simple: p = 'Hoy es lunes'\nCompuesta: p ∧ q = 'Hoy es lunes Y tengo clase de matemáticas'"
                ),
                TheorySection(
                    subtitle = "Proposiciones Abiertas y Cerradas",
                    content = "• Proposición Cerrada: No contiene variables libres; su valor de verdad es determinable inmediatamente (ej. '5 > 3').\n• Proposición Abierta: Contiene variables (como x, y). No tiene valor de verdad fijo hasta sustituir la variable o aplicar un cuantificador (ej. 'x es divisible por 2')."
                )
            )
        ),
        TheoryTopic(
            id = "variables",
            title = "2. Variables Proposicionales",
            summary = "Uso de letras minúsculas (p, q, r, s...) y formalización de enunciados cotidianos.",
            icon = "🔤",
            sections = listOf(
                TheorySection(
                    subtitle = "Símbolos y Nomenclatura",
                    content = "En lógica proposicional usamos letras minúsculas a partir de la letra 'p' (p, q, r, s, t...) para representar proposiciones simples arbitrarias. Este proceso se llama abstracción o formalización.",
                    example = "Sean:\np = 'Está lloviendo.'\nq = 'Llevo paraguas.'\nr = 'El suelo está mojado.'\n\nPodemos formalizar: 'Si está lloviendo, llevo paraguas' como (p → q)."
                ),
                TheorySection(
                    subtitle = "Valores de Verdad",
                    content = "Cada variable proposicional puede adoptar únicamente dos estados posibles:\n• Verdadero (V, 1 o T - True)\n• Falso (F, 0 o F - False)\nCon 'n' variables proposicionales independientes, el número total de estados o combinaciones posibles del sistema es 2ⁿ.",
                    example = "1 variable (p) → 2¹ = 2 combinaciones: {V, F}\n2 variables (p, q) → 2² = 4 combinaciones: {VV, VF, FV, FF}\n3 variables (p, q, r) → 2³ = 8 combinaciones\n4 variables → 2⁴ = 16 combinaciones"
                )
            )
        ),
        TheoryTopic(
            id = "connectors",
            title = "3. Conectores Lógicos",
            summary = "Estudio detallado de ¬, ∧, ∨, ⊕, →, ↔ con tablas de verdad y ejemplos.",
            icon = "⚡",
            sections = listOf(
                TheorySection(
                    subtitle = "1. Negación (¬p)",
                    content = "La negación es un operador unario que invierte el valor de verdad de una proposición. Si p es verdadera, ¬p es falsa; si p es falsa, ¬p es verdadera.",
                    formula = "¬p  (Se lee: 'No p', 'Es falso que p')",
                    example = "Si p = 'Hoy llueve' (V), ¬p = 'Hoy no llueve' (F).",
                    truthTableData = MiniTruthTable(
                        headers = listOf("p", "¬p"),
                        rows = listOf(
                            listOf("V", "F"),
                            listOf("F", "V")
                        )
                    )
                ),
                TheorySection(
                    subtitle = "2. Conjunción (p ∧ q)",
                    content = "La conjunción une dos proposiciones y solo resulta VERDADERA si AMBAS componentes son simultáneamente verdaderas. En cualquier otro caso, es FALSA.",
                    formula = "p ∧ q  (Se lee: 'p y q')",
                    example = "p = 'Tengo el examen aprobado', q = 'Tengo la tarea entregada'.\nPara pasar la materia requieres aprobar el examen Y entregar la tarea.",
                    truthTableData = MiniTruthTable(
                        headers = listOf("p", "q", "p ∧ q"),
                        rows = listOf(
                            listOf("V", "V", "V"),
                            listOf("V", "F", "F"),
                            listOf("F", "V", "F"),
                            listOf("F", "F", "F")
                        )
                    )
                ),
                TheorySection(
                    subtitle = "3. Disyunción Inclusiva (p ∨ q)",
                    content = "La disyunción une dos proposiciones y es VERDADERA si al menos UNA de ellas es verdadera (pudiendo ser ambas verdaderas). Solo es FALSA cuando ambas son falsas.",
                    formula = "p ∨ q  (Se lee: 'p o q')",
                    example = "'Para la beca necesitas promedio superior a 9 O ser deportista destacado'. Cumplir cualquiera de las dos (o ambas) te califica.",
                    truthTableData = MiniTruthTable(
                        headers = listOf("p", "q", "p ∨ q"),
                        rows = listOf(
                            listOf("V", "V", "V"),
                            listOf("V", "F", "V"),
                            listOf("F", "V", "V"),
                            listOf("F", "F", "F")
                        )
                    )
                ),
                TheorySection(
                    subtitle = "4. Disyunción Exclusiva (p ⊕ q)",
                    content = "La disyunción exclusiva es VERDADERA cuando exactamente UNA de las dos proposiciones es verdadera, pero NO AMBAS a la vez. Si ambas tienen el mismo valor de verdad, resulta FALSA.",
                    formula = "p ⊕ q  (Se lee: 'p o q, pero no ambos')",
                    example = "'El menú incluye sopa o ensalada de entrada (no puedes pedir ambas)'.",
                    truthTableData = MiniTruthTable(
                        headers = listOf("p", "q", "p ⊕ q"),
                        rows = listOf(
                            listOf("V", "V", "F"),
                            listOf("V", "F", "V"),
                            listOf("F", "V", "V"),
                            listOf("F", "F", "F")
                        )
                    )
                ),
                TheorySection(
                    subtitle = "5. Condicional / Implicación (p → q)",
                    content = "El condicional relaciona un antecedente 'p' con un consecuente 'q'. Solo es FALSO cuando el antecedente es VERDADERO y el consecuente es FALSO. Si la promesa inicial (p) no se cumple (p es F), la implicación se considera vacíamente verdadera.",
                    formula = "p → q  (Se lee: 'Si p, entonces q' o 'p implica q')",
                    example = "Promesa: 'Si estudias (p), te compraré un libro (q)'.\nSi no estudiaste (p=F), nunca rompiste la promesa, por ende no se considera mentira.",
                    truthTableData = MiniTruthTable(
                        headers = listOf("p", "q", "p → q"),
                        rows = listOf(
                            listOf("V", "V", "V"),
                            listOf("V", "F", "F"),
                            listOf("F", "V", "V"),
                            listOf("F", "F", "V")
                        )
                    )
                ),
                TheorySection(
                    subtitle = "6. Bicondicional (p ↔ q)",
                    content = "El bicondicional afirma que p implica a q Y q implica a p. Es VERDADERO cuando ambas proposiciones tienen el MISMO valor de verdad (ambas verdaderas o ambas falsas). Es falso si difieren.",
                    formula = "p ↔ q  (Se lee: 'p si y solo si q')",
                    example = "'Un polígono es un triángulo si y solo si tiene exactamente tres lados'.",
                    truthTableData = MiniTruthTable(
                        headers = listOf("p", "q", "p ↔ q"),
                        rows = listOf(
                            listOf("V", "V", "V"),
                            listOf("V", "F", "F"),
                            listOf("F", "V", "F"),
                            listOf("F", "F", "V")
                        )
                    )
                )
            )
        ),
        TheoryTopic(
            id = "precedence",
            title = "4. Jerarquía de Operadores",
            summary = "Precedencia de los conectores lógicos para evaluar fórmulas sin ambigüedad.",
            icon = "🔢",
            sections = listOf(
                TheorySection(
                    subtitle = "Orden de Precedencia Estándar",
                    content = "Al igual que en álgebra convencional (* antes de +), en lógica proposicional existe una jerarquía estricta que se aplica cuando no hay paréntesis:\n\n1. Paréntesis: ( ... )  [Prioridad máxima]\n2. Negación: ¬  [Operador unario, liga fuertemente]\n3. Conjunción: ∧  [Multiplicación lógica]\n4. Disyunción: ∨ y Disyunción Exclusiva: ⊕\n5. Implicación / Condicional: →\n6. Bicondicional: ↔  [Prioridad mínima]",
                    example = "La expresión:  ¬p ∧ q → r\nSe evalúa exactamente como:  ((¬p) ∧ q) → r\n\nSiempre es recomendable utilizar paréntesis explícitos para facilitar la lectura y evitar confusiones."
                )
            )
        ),
        TheoryTopic(
            id = "classification",
            title = "5. Tautología, Contradicción y Contingencia",
            summary = "Clasificación de fórmulas según los resultados de su tabla de verdad.",
            icon = "⚖️",
            sections = listOf(
                TheorySection(
                    subtitle = "Tautología",
                    content = "Una fórmula proposicional es una Tautología si resulta VERDADERA para todas las interpretaciones o asignaciones posibles de valores de verdad a sus variables.",
                    formula = "Ejemplo clásico: p ∨ ¬p (Ley del Tercero Excluido)",
                    example = "Para cualquier valor de p (V o F), 'p ∨ ¬p' siempre resulta V."
                ),
                TheorySection(
                    subtitle = "Contradicción",
                    content = "Una fórmula proposicional es una Contradicción (o absurdo) si resulta FALSA para todas las interpretaciones posibles de sus variables.",
                    formula = "Ejemplo clásico: p ∧ ¬p (Principio de No Contradicción)",
                    example = "No es posible que algo ocurra y no ocurra simultáneamente: 'Hoy llueve y no llueve' es siempre F."
                ),
                TheorySection(
                    subtitle = "Contingencia (o Indeterminación)",
                    content = "Una fórmula proposicional es una Contingencia si su valor de verdad depende de los valores concretos que tomen sus variables; es decir, en su tabla de verdad aparecen tanto valores verdaderos como falsos.",
                    formula = "Ejemplo: p → q, o (p ∧ q) ∨ r",
                    example = "La gran mayoría de afirmaciones cotidianas son contingencias."
                )
            )
        ),
        TheoryTopic(
            id = "predicates",
            title = "6. Lógica de Predicados y Cuantificadores",
            summary = "Cuantificador universal (∀), existencial (∃), dominios y negaciones.",
            icon = "∀",
            sections = listOf(
                TheorySection(
                    subtitle = "Cuantificador Universal (∀)",
                    content = "El símbolo ∀ se lee 'para todo', 'para cada' o 'para cualquier'. Expresa que todos los elementos de un dominio satisfacen la condición.",
                    formula = "∀x P(x)",
                    example = "∀x (x + 0 = x) en el conjunto de los números reales."
                ),
                TheorySection(
                    subtitle = "Cuantificador Existencial (∃)",
                    content = "El símbolo ∃ se lee 'existe al menos un' o 'para algún'. Expresa que al menos un elemento del dominio satisface la condición.",
                    formula = "∃x P(x)",
                    example = "∃x (x > 100 ∧ Primo(x)): 'Existe un número primo mayor a 100'."
                ),
                TheorySection(
                    subtitle = "Leyes de Negación de Cuantificadores",
                    content = "Para negar una afirmación cuantificada:\n1. Negar un 'para todo' equivale a encontrar al menos un contraejemplo existencial:\n   ¬∀x P(x) ↔ ∃x ¬P(x)\n\n2. Negar un 'existe' equivale a afirmar que ninguno lo cumple (todos cumplen la negación):\n   ¬∃x P(x) ↔ ∀x ¬P(x)",
                    example = "Afirmación: 'Todos los estudiantes aprobaron' (∀x A(x)).\nNegación: 'Al menos un estudiante no aprobó' (∃x ¬A(x))."
                )
            )
        )
    )
}
