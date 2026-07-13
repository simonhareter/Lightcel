package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {
    private String formula;
    private int idx;

    private void init(String formula) {
        this.formula = formula;
        this.idx = 0;
    }

    public List<Token> tokenize(String formula) {
        init(formula);
        List<Token> tokens = new ArrayList<Token>();

        while (hasNextToken()) {
            Token token = getNextToken();
            tokens.add(token);
        }

        return tokens;
    }

    private boolean hasNextToken() {
        return this.idx < this.formula.length();
    }

    private Token getNextToken() {
        Token token = new Token();

        char c = this.formula.charAt(this.idx);

        switch (c) {
            // Delimiters ,:() and MATH_OPERATORS +-*/
            case '=' -> {
                if (this.idx != 0) {
                    token.fillToken("=", TokenType.EQUALS);
                } else {
                    token.fillToken("=", TokenType.FORMULA_START);
                }
            }
            case '(' -> token.fillToken("(", TokenType.OPEN_PARENTHESES);
            case ')' -> token.fillToken(")", TokenType.CLOSING_PARENTHESES);
            case ',' -> token.fillToken(",", TokenType.COMMA);
            case ':' -> token.fillToken(":", TokenType.COLON);
            case '+' -> token.fillToken("+", TokenType.MATH_OPERATOR);
            case '-' -> token.fillToken("-", TokenType.MATH_OPERATOR);
            case '*' -> token.fillToken("*", TokenType.MATH_OPERATOR);
            case '/' -> token.fillToken("/", TokenType.MATH_OPERATOR);
            case '^' -> token.fillToken("^", TokenType.MATH_OPERATOR);
            case '%' -> token.fillToken("%", TokenType.PERCENTAGE_OPERATOR);
            case '<' -> {
                if (this.idx + 1 < this.formula.length() && this.formula.charAt(this.idx + 1) == '=') {
                    token.fillToken("<=", TokenType.COMPARISON_OPERATOR);
                    this.idx++;
                } else if (this.idx + 1 < this.formula.length() && this.formula.charAt(this.idx + 1) == '>') {
                    token.fillToken("<>", TokenType.COMPARISON_OPERATOR);
                    this.idx++;
                } else {
                    token.fillToken("<", TokenType.COMPARISON_OPERATOR);
                }
            }
            case '>' -> {
                if (this.idx + 1 < this.formula.length() && this.formula.charAt(this.idx + 1) == '=') {
                    token.fillToken(">=", TokenType.COMPARISON_OPERATOR);
                    this.idx++;
                } else {
                    token.fillToken(">", TokenType.COMPARISON_OPERATOR);
                }
            }
            case '&' -> token.fillToken("&", TokenType.CONCATENATION_OPERATOR);
            default -> {
                StringBuilder sb = new StringBuilder();
                boolean startedWithQuote = false, endedWithQuote = false;

                char start = formula.charAt(this.idx);

                // Check for STRINGS
                if (start == '"') {
                    if (!startedWithQuote) {
                        startedWithQuote = true;
                        this.idx++;
                    }

                    while (this.idx < formula.length()) {
                        c = formula.charAt(this.idx);

                        if (startedWithQuote) {
                            if (c == '"' && this.idx + 1 < formula.length() && formula.charAt(this.idx + 1) == '"') {
                                sb.append('"');
                                this.idx++;
                            } else if (c == '"') {
                                endedWithQuote = true;
                                this.idx++;
                                break;
                            } else {
                                sb.append(c);
                            }
                        }

                        this.idx++;
                    }
                }

                // Check for IDENTIFIERS, CELLS or NUMBERS
                if (Character.isLetterOrDigit(start)) {
                    while (this.idx < formula.length()) {
                        c = formula.charAt(this.idx);
                        sb.append(c);

                        if (this.idx + 1 < formula.length() && formula.charAt(this.idx + 1) == '.') {
                            this.idx++;
                            sb.append(formula.charAt(this.idx));
                        }

                        if (this.idx + 1 < formula.length()
                                && !Character.isLetterOrDigit(formula.charAt(this.idx + 1))) {
                            this.idx++;
                            break;
                        }
                        this.idx++;
                    }
                }

                TokenType type = classify(sb.toString(), startedWithQuote, endedWithQuote);
                token.fillToken(sb.toString(), type);
                return token;
            }
        }
        this.idx++;
        return token;
    }

    private TokenType classify(String string, boolean startedWithQuote, boolean endedWithQuote) {
        final Pattern identifier = Pattern.compile("[A-Za-z]+", Pattern.CASE_INSENSITIVE);
        final Matcher matcherIdentifier = identifier.matcher(string);
        final Pattern cell = Pattern.compile("[A-Za-z][0-9]+", Pattern.CASE_INSENSITIVE);
        final Matcher matcherCell = cell.matcher(string);
        final Pattern number = Pattern.compile("([+-]?(?=\\.\\d|\\d)(?:\\d+)?(?:\\.?\\d*))(?:[Ee]([+-]?\\d+))?",
                Pattern.CASE_INSENSITIVE);
        final Matcher matcherNumber = number.matcher(string);

        if (startedWithQuote) {
            if (!endedWithQuote) {
                return TokenType.ERROR_UNTERMINATED_STRING;
            }

            if (string.isEmpty()) {
                return TokenType.EMPTY_STRING;
            }

            return TokenType.STRING;
        }

        if (matcherIdentifier.matches()) {
            return TokenType.IDENTIFIER;
        } else if (matcherCell.matches()) {
            return TokenType.CELL;
        } else if (matcherNumber.matches()) {
            return TokenType.NUMBER;
        }

        return TokenType.STRING;
    }

    public void printTokenList(List<Token> list) { 
        IO.println("Tokenizer Result: ");
        for (Token tok : list) {
            IO.println(tok.toString());
        }
        IO.println("-------------------------------");
    }
}
