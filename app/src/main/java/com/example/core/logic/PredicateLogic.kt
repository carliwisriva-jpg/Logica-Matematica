package com.example.core.logic

data class PredicateConcept(
    val id: String,
    val title: String,
    val symbol: String,
    val translation: String,
    val definition: String,
    val example: String,
    val formalExample: String,
    val negationLaw: String? = null
)

data class PredicateExpressionExplainer(
    val expression: String,
    val naturalLanguage: String,
    val structureAnalysis: String,
    val typicalExample: String,
    val negationEquivalent: String
)

data class InteractivePredicateExample(
    val predicateSymbol: String,
    val predicateDefinition: String,
    val domainName: String,
    val domainElements: List<Int>,
    val testFunction: (Int) -> Boolean
)

object PredicateLogicData {

    val concepts = listOf(
        PredicateConcept(
            id = "universal",
            title = "Cuantificador Universal",
            symbol = "∀",
            translation = "Para todo / Para cada / Para cualquier",
            definition = "Indica que una propiedad P(x) se cumple sin excepción para todos los elementos del dominio o universo del discurso.",
            example = "Si el dominio son los números enteros, ∀x (x² ≥ 0) afirma que el cuadrado de cualquier número entero es mayor o igual a cero.",
            formalExample = "∀x P(x)",
            negationLaw = "¬[∀x P(x)] ≡ ∃x ¬P(x)"
        ),
        PredicateConcept(
            id = "existential",
            title = "Cuantificador Existencial",
            symbol = "∃",
            translation = "Existe al menos un / Hay algún",
            definition = "Indica que existe al menos un elemento en el dominio para el cual la propiedad P(x) es verdadera.",
            example = "Si el dominio son los números reales, ∃x (x² = 2) afirma que existe al menos un número real cuyo cuadrado es 2 (en este caso √2).",
            formalExample = "∃x P(x)",
            negationLaw = "¬[∃x P(x)] ≡ ∀x ¬P(x)"
        ),
        PredicateConcept(
            id = "predicate",
            title = "Predicado y Proposición Abierta",
            symbol = "P(x), Q(x, y)",
            translation = "Propiedad o relación aplicada a variables",
            definition = "Un predicado es una afirmación que depende de una o más variables. Por sí sola no es una proposición (no tiene valor de verdad fijo) hasta que la variable se reemplaza por un elemento del dominio o se cuantifica.",
            example = "P(x): 'x es un número primo'. Si x=3, P(3) es Verdadero. Si x=4, P(4) es Falso.",
            formalExample = "P(x) donde x ∈ ℕ"
        ),
        PredicateConcept(
            id = "domain",
            title = "Dominio del Discurso (Universo)",
            symbol = "U o D",
            translation = "Conjunto de objetos admisibles para la variable",
            definition = "Es el conjunto de todos los posibles valores que puede tomar la variable x. El valor de verdad de una expresión con cuantificadores cambia drásticamente según el dominio elegido.",
            example = "Para ∀x (x > 0): es Falso si el dominio son los Enteros (ℤ), pero Verdadero si el dominio son los Números Naturales estrictos (ℕ₊).",
            formalExample = "x ∈ D"
        )
    )

    val negationRules = listOf(
        PredicateConcept(
            id = "neg_universal",
            title = "Negación del Cuantificador Universal",
            symbol = "¬∀x P(x) ↔ ∃x ¬P(x)",
            translation = "No todos cumplen P ≡ Al menos uno no cumple P",
            definition = "Negar que 'todos los elementos cumplen P(x)' equivale a decir que 'existe al menos un contraejemplo x que no cumple P(x)'.",
            example = "Afirmación: 'Todos los cisnes son blancos'. Negación: 'No todos los cisnes son blancos' ≡ 'Existe al menos un cisne que no es blanco'.",
            formalExample = "¬∀x CisneBlanco(x) ≡ ∃x ¬CisneBlanco(x)"
        ),
        PredicateConcept(
            id = "neg_existential",
            title = "Negación del Cuantificador Existencial",
            symbol = "¬∃x P(x) ↔ ∀x ¬P(x)",
            translation = "No existe ninguno con P ≡ Para todos, ninguno cumple P",
            definition = "Negar que 'existe al menos un elemento que cumpla P(x)' equivale a decir que 'para todos los elementos, ninguno cumple P(x)'.",
            example = "Afirmación: 'Existe una persona inmortal'. Negación: 'No existe nadie inmortal' ≡ 'Para toda persona, es mortal (no es inmortal)'.",
            formalExample = "¬∃x Inmortal(x) ≡ ∀x ¬Inmortal(x)"
        )
    )

    val standardExpressions = listOf(
        PredicateExpressionExplainer(
            expression = "∀x P(x)",
            naturalLanguage = "Para todo x, x tiene la propiedad P.",
            structureAnalysis = "Cuantificador universal directo sobre el predicado P.",
            typicalExample = "Si P(x) = 'x es mortal' y el dominio son los humanos: 'Todos los seres humanos son mortales'.",
            negationEquivalent = "∃x ¬P(x) ('Existe alguien que no es mortal')"
        ),
        PredicateExpressionExplainer(
            expression = "∃x P(x)",
            naturalLanguage = "Existe al menos un x tal que x cumple P.",
            structureAnalysis = "Cuantificador existencial directo.",
            typicalExample = "Si P(x) = 'x es estudiante universitario': 'Existe al menos una persona que es estudiante universitario'.",
            negationEquivalent = "∀x ¬P(x) ('Nadie es estudiante universitario')"
        ),
        PredicateExpressionExplainer(
            expression = "∀x (P(x) → Q(x))",
            naturalLanguage = "Para todo x, si x cumple P entonces x cumple Q.",
            structureAnalysis = "Estructura canónica para 'Todo P es Q'. Relaciona dos predicados condicionalmente.",
            typicalExample = "P(x): 'x es ave', Q(x): 'x tiene plumas'. Traducción: 'Todas las aves tienen plumas'.",
            negationEquivalent = "∃x (P(x) ∧ ¬Q(x)) ('Existe un ave que no tiene plumas')"
        ),
        PredicateExpressionExplainer(
            expression = "∃x (P(x) ∧ Q(x))",
            naturalLanguage = "Existe al menos un x que cumple tanto P como Q.",
            structureAnalysis = "Estructura canónica para 'Algún P es Q'. Conjunción bajo cuantificador existencial.",
            typicalExample = "P(x): 'x es estudiante', Q(x): 'x trabaja': 'Existen estudiantes que también trabajan'.",
            negationEquivalent = "∀x (P(x) → ¬Q(x)) ('Ningún estudiante trabaja')"
        ),
        PredicateExpressionExplainer(
            expression = "∀x ∃y R(x, y)",
            naturalLanguage = "Para cada x, existe un y tal que x se relaciona con y mediante R.",
            structureAnalysis = "Cuantificadores anidados: universal exterior y existencial interior. El valor de 'y' puede depender de 'x'.",
            typicalExample = "Si R(x, y) es 'y es la madre de x': 'Toda persona tiene una madre'.",
            negationEquivalent = "∃x ∀y ¬R(x, y) ('Existe una persona que no tiene madre')"
        )
    )

    val interactiveExamples = listOf(
        InteractivePredicateExample(
            predicateSymbol = "P(x): x es par",
            predicateDefinition = "x es divisible por 2",
            domainName = "Números {2, 4, 6, 8}",
            domainElements = listOf(2, 4, 6, 8),
            testFunction = { it % 2 == 0 }
        ),
        InteractivePredicateExample(
            predicateSymbol = "Q(x): x > 3",
            predicateDefinition = "x es estrictamente mayor que 3",
            domainName = "Enteros {1, 2, 3, 4, 5}",
            domainElements = listOf(1, 2, 3, 4, 5),
            testFunction = { it > 3 }
        ),
        InteractivePredicateExample(
            predicateSymbol = "R(x): x² ≥ 0",
            predicateDefinition = "el cuadrado de x es no negativo",
            domainName = "Enteros {-2, -1, 0, 1, 2}",
            domainElements = listOf(-2, -1, 0, 1, 2),
            testFunction = { it * it >= 0 }
        ),
        InteractivePredicateExample(
            predicateSymbol = "S(x): x < 0",
            predicateDefinition = "x es negativo",
            domainName = "Naturales {1, 2, 3, 4}",
            domainElements = listOf(1, 2, 3, 4),
            testFunction = { it < 0 }
        )
    )
}
