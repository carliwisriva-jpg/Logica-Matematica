package com.example.core.logic

data class LogicLaw(
    val id: String,
    val name: String,
    val formulas: List<String>,
    val description: String,
    val example: String,
    val category: String = "Leyes Proposicionales"
)

object LogicLawsRepository {

    val laws = listOf(
        LogicLaw(
            id = "identidad",
            name = "Ley de Identidad",
            formulas = listOf("p ∧ V ≡ p", "p ∨ F ≡ p"),
            description = "Operar una proposición con el elemento neutro de la operación no cambia su valor de verdad. Para la conjunción es V, para la disyunción es F.",
            example = "Si sabemos que 'p' es cierta y la combinamos con algo siempre cierto (V), el resultado depende únicamente de 'p'."
        ),
        LogicLaw(
            id = "dominacion",
            name = "Ley de Dominación (Anulación)",
            formulas = listOf("p ∨ V ≡ V", "p ∧ F ≡ F"),
            description = "Un valor absorbe completamente el resultado: basta con que un lado de la disyunción sea V para que todo sea V, y basta con que un lado de la conjunción sea F para que todo sea F.",
            example = "Si dices 'Hoy llueve o 2+2=4', como 2+2=4 siempre es V, toda la frase es siempre V."
        ),
        LogicLaw(
            id = "idempotencia",
            name = "Ley de Idempotencia",
            formulas = listOf("p ∨ p ≡ p", "p ∧ p ≡ p"),
            description = "Repetir la misma proposición unida por conjunción o disyunción no aporta nueva información; es equivalente a la proposición simple.",
            example = "'Está lloviendo y está lloviendo' equivale simplemente a 'Está lloviendo'."
        ),
        LogicLaw(
            id = "doble_negacion",
            name = "Ley de Doble Negación",
            formulas = listOf("¬(¬p) ≡ p"),
            description = "Negar dos veces una proposición devuelve su valor original.",
            example = "'No es cierto que no fui al cine' equivale a 'Fui al cine'."
        ),
        LogicLaw(
            id = "complemento",
            name = "Ley de Complemento (Tercero Excluido y Contradicción)",
            formulas = listOf("p ∨ ¬p ≡ V", "p ∧ ¬p ≡ F"),
            description = "Una proposición o bien es verdadera o bien su negación lo es (tercero excluido, p ∨ ¬p = V). Pero nunca pueden ser verdaderas ambas a la vez (contradicción, p ∧ ¬p = F).",
            example = "'Está soleado o no está soleado' siempre es verdad (Tautología)."
        ),
        LogicLaw(
            id = "de_morgan",
            name = "Leyes de De Morgan",
            formulas = listOf("¬(p ∧ q) ≡ ¬p ∨ ¬q", "¬(p ∨ q) ≡ ¬p ∧ ¬q"),
            description = "La negación de una conjunción es la disyunción de las negaciones; y la negación de una disyunción es la conjunción de las negaciones.",
            example = "Negar 'comí pizza y pasta' equivale a 'no comí pizza o no comí pasta'."
        ),
        LogicLaw(
            id = "conmutativa",
            name = "Ley Conmutativa",
            formulas = listOf("p ∨ q ≡ q ∨ p", "p ∧ q ≡ q ∧ p", "p ↔ q ≡ q ↔ p", "p ⊕ q ≡ q ⊕ p"),
            description = "El orden de los operandos no altera el valor de verdad en conjunción, disyunción, disyunción exclusiva y bicondicional.",
            example = "'Llueve y hace frío' equivale a 'Hace frío y llueve'."
        ),
        LogicLaw(
            id = "asociativa",
            name = "Ley Asociativa",
            formulas = listOf("(p ∨ q) ∨ r ≡ p ∨ (q ∨ r)", "(p ∧ q) ∧ r ≡ p ∧ (q ∧ r)"),
            description = "Cuando se encadenan conectores del mismo tipo, la forma de agrupar con paréntesis no altera el resultado.",
            example = "((p ∧ q) ∧ r) equivale a (p ∧ (q ∧ r)) y se puede escribir simplemente p ∧ q ∧ r."
        ),
        LogicLaw(
            id = "distributiva",
            name = "Ley Distributiva",
            formulas = listOf(
                "p ∧ (q ∨ r) ≡ (p ∧ q) ∨ (p ∧ r)",
                "p ∨ (q ∧ r) ≡ (p ∨ q) ∧ (p ∨ r)"
            ),
            description = "La conjunción se distribuye sobre la disyunción y viceversa, similar a la multiplicación y suma en álgebra.",
            example = "'Estudio y (leo o escribo)' equivale a '(Estudio y leo) o (Estudio y escribo)'."
        ),
        LogicLaw(
            id = "absorcion",
            name = "Ley de Absorción",
            formulas = listOf("p ∨ (p ∧ q) ≡ p", "p ∧ (p ∨ q) ≡ p"),
            description = "La combinación de una proposición externa con una interna que la contiene elimina a la variable secundaria.",
            example = "Si p es verdadero, p ∨ (p ∧ q) es verdadero directamente; si p es falso, ambos términos son falsos."
        ),
        LogicLaw(
            id = "implicacion",
            name = "Ley de la Implicación (Condicional Material)",
            formulas = listOf("p → q ≡ ¬p ∨ q", "p → q ≡ ¬q → ¬p (Contrarrecíproca)"),
            description = "Una implicación solo es falsa cuando el antecedente es verdadero y el consecuente falso; por eso equivale a que p sea falso o q sea verdadero.",
            example = "'Si estudias, apruebas' equivale a 'No estudiaste o aprobaste'."
        ),
        LogicLaw(
            id = "bicondicional",
            name = "Ley del Bicondicional",
            formulas = listOf(
                "p ↔ q ≡ (p → q) ∧ (q → p)",
                "p ↔ q ≡ (¬p ∨ q) ∧ (¬q ∨ p)",
                "p ↔ q ≡ (p ∧ q) ∨ (¬p ∧ ¬q)"
            ),
            description = "La equivalencia mutua significa que p implica a q y q implica a p. Ambos deben tener el mismo valor de verdad.",
            example = "'Un triángulo es equilátero si y solo si es equiángulo'."
        )
    )

    fun searchLaws(query: String): List<LogicLaw> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return laws
        return laws.filter { law ->
            law.name.lowercase().contains(q) ||
            law.description.lowercase().contains(q) ||
            law.formulas.any { it.lowercase().contains(q) }
        }
    }
}
