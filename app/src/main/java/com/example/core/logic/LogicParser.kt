package com.example.core.logic

class LogicParser {

    fun tokenize(input: String): List<LogicToken> {
        val tokens = mutableListOf<LogicToken>()
        var i = 0
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return listOf(LogicToken(TokenType.EOF, "", 0))
        }

        while (i < trimmed.length) {
            val c = trimmed[i]
            if (c.isWhitespace()) {
                i++
                continue
            }

            // Check multi-character operators first
            if (trimmed.startsWith("<->", i)) {
                tokens.add(LogicToken(TokenType.IFF, "↔", i))
                i += 3
                continue
            }
            if (trimmed.startsWith("<=>", i)) {
                tokens.add(LogicToken(TokenType.IFF, "↔", i))
                i += 3
                continue
            }
            if (trimmed.startsWith("->", i)) {
                tokens.add(LogicToken(TokenType.IMPLIES, "→", i))
                i += 2
                continue
            }
            if (trimmed.startsWith("=>", i)) {
                tokens.add(LogicToken(TokenType.IMPLIES, "→", i))
                i += 2
                continue
            }
            if (trimmed.startsWith("xor", i, ignoreCase = true) && 
                (i + 3 >= trimmed.length || !trimmed[i + 3].isLetterOrDigit())) {
                tokens.add(LogicToken(TokenType.XOR, "⊕", i))
                i += 3
                continue
            }
            if (trimmed.startsWith("and", i, ignoreCase = true) && 
                (i + 3 >= trimmed.length || !trimmed[i + 3].isLetterOrDigit())) {
                tokens.add(LogicToken(TokenType.AND, "∧", i))
                i += 3
                continue
            }
            if (trimmed.startsWith("not", i, ignoreCase = true) && 
                (i + 3 >= trimmed.length || !trimmed[i + 3].isLetterOrDigit())) {
                tokens.add(LogicToken(TokenType.NOT, "¬", i))
                i += 3
                continue
            }
            if (trimmed.startsWith("or", i, ignoreCase = true) && 
                (i + 2 >= trimmed.length || !trimmed[i + 2].isLetterOrDigit())) {
                tokens.add(LogicToken(TokenType.OR, "∨", i))
                i += 2
                continue
            }

            when (c) {
                '¬', '~', '!' -> {
                    tokens.add(LogicToken(TokenType.NOT, "¬", i))
                    i++
                }
                '∧', '&', '^' -> {
                    tokens.add(LogicToken(TokenType.AND, "∧", i))
                    i++
                }
                '∨', '|' -> {
                    tokens.add(LogicToken(TokenType.OR, "∨", i))
                    i++
                }
                '⊕' -> {
                    tokens.add(LogicToken(TokenType.XOR, "⊕", i))
                    i++
                }
                '→' -> {
                    tokens.add(LogicToken(TokenType.IMPLIES, "→", i))
                    i++
                }
                '↔' -> {
                    tokens.add(LogicToken(TokenType.IFF, "↔", i))
                    i++
                }
                '(' -> {
                    tokens.add(LogicToken(TokenType.LPAREN, "(", i))
                    i++
                }
                ')' -> {
                    tokens.add(LogicToken(TokenType.RPAREN, ")", i))
                    i++
                }
                'T', 'V', '1' -> {
                    // Check if uppercase 'V' or '1' is boolean constant TRUE
                    tokens.add(LogicToken(TokenType.TRUE, "V", i))
                    i++
                }
                'F', '0' -> {
                    // Check if uppercase 'F' or '0' is boolean constant FALSE
                    tokens.add(LogicToken(TokenType.FALSE, "F", i))
                    i++
                }
                'v' -> {
                    // Lowercase 'v': If preceded by a variable or closing paren, it's very often OR (p v q)
                    val lastToken = tokens.lastOrNull()?.type
                    val couldBeOr = lastToken in listOf(TokenType.VAR, TokenType.RPAREN, TokenType.TRUE, TokenType.FALSE)
                    if (couldBeOr) {
                        tokens.add(LogicToken(TokenType.OR, "∨", i))
                    } else {
                        tokens.add(LogicToken(TokenType.VAR, "v", i))
                    }
                    i++
                }
                else -> {
                    if (c.isLetter()) {
                        tokens.add(LogicToken(TokenType.VAR, c.lowercaseChar().toString(), i))
                        i++
                    } else {
                        throw LogicParseException(
                            message = "Símbolo no reconocido: '$c' en la posición ${i + 1}",
                            position = i
                        )
                    }
                }
            }
        }
        tokens.add(LogicToken(TokenType.EOF, "", trimmed.length))
        return tokens
    }

    fun parse(expression: String): AstNode {
        val trimmed = expression.trim()
        if (trimmed.isEmpty()) {
            throw LogicParseException("La expresión está vacía. Introduce una fórmula como (p ∧ q) → r.")
        }
        val tokens = tokenize(trimmed)
        val parserState = ParserState(tokens)
        val node = parserState.parseBiconditional()

        if (parserState.peek().type != TokenType.EOF) {
            val unexpected = parserState.peek()
            if (unexpected.type == TokenType.RPAREN) {
                throw LogicParseException(
                    "Paréntesis de cierre ')' inesperado sin apertura previa.",
                    unexpected.position
                )
            }
            throw LogicParseException(
                "Símbolo inesperado '${unexpected.text}' en posición ${unexpected.position + 1}.",
                unexpected.position
            )
        }
        return node
    }

    private class ParserState(private val tokens: List<LogicToken>) {
        private var index = 0

        fun peek(): LogicToken = tokens[index]

        fun consume(): LogicToken {
            val token = tokens[index]
            if (index < tokens.size - 1) {
                index++
            }
            return token
        }

        fun match(type: TokenType): Boolean {
            if (peek().type == type) {
                consume()
                return true
            }
            return false
        }

        // Biconditional: p ↔ q (lowest precedence)
        fun parseBiconditional(): AstNode {
            var left = parseImplication()
            while (match(TokenType.IFF)) {
                val right = parseImplication()
                left = AstNode.Iff(left, right)
            }
            return left
        }

        // Implication: p → q (right-associative)
        fun parseImplication(): AstNode {
            val left = parseDisjunction()
            if (match(TokenType.IMPLIES)) {
                val right = parseImplication() // right-associative: p → q → r is p → (q → r)
                return AstNode.Implies(left, right)
            }
            return left
        }

        // Disjunction & XOR: p ∨ q, p ⊕ q
        fun parseDisjunction(): AstNode {
            var left = parseConjunction()
            while (true) {
                when {
                    match(TokenType.OR) -> {
                        val right = parseConjunction()
                        left = AstNode.Or(left, right)
                    }
                    match(TokenType.XOR) -> {
                        val right = parseConjunction()
                        left = AstNode.Xor(left, right)
                    }
                    else -> break
                }
            }
            return left
        }

        // Conjunction: p ∧ q
        fun parseConjunction(): AstNode {
            var left = parseUnary()
            while (match(TokenType.AND)) {
                val right = parseUnary()
                left = AstNode.And(left, right)
            }
            return left
        }

        // Unary: ¬p
        fun parseUnary(): AstNode {
            if (match(TokenType.NOT)) {
                val child = parseUnary()
                return AstNode.Not(child)
            }
            return parsePrimary()
        }

        // Primary: variable, constant, or (expression)
        fun parsePrimary(): AstNode {
            val current = peek()
            when (current.type) {
                TokenType.VAR -> {
                    consume()
                    return AstNode.Variable(current.text)
                }
                TokenType.TRUE -> {
                    consume()
                    return AstNode.Constant(true)
                }
                TokenType.FALSE -> {
                    consume()
                    return AstNode.Constant(false)
                }
                TokenType.LPAREN -> {
                    val openPos = current.position
                    consume() // consume '('
                    val expr = parseBiconditional()
                    if (!match(TokenType.RPAREN)) {
                        throw LogicParseException(
                            "Falta un paréntesis de cierre ')' para el abierto en posición ${openPos + 1}.",
                            openPos
                        )
                    }
                    return expr
                }
                TokenType.EOF -> {
                    throw LogicParseException(
                        "Operador lógico incompleto: falta un operando o variable al final de la expresión.",
                        current.position
                    )
                }
                TokenType.RPAREN -> {
                    throw LogicParseException(
                        "Paréntesis de cierre ')' inesperado.",
                        current.position
                    )
                }
                else -> {
                    throw LogicParseException(
                        "Operador inesperado '${current.text}'. Se esperaba una variable o '('.",
                        current.position
                    )
                }
            }
        }
    }
}
