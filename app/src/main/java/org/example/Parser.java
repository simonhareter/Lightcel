package org.example;

import java.util.ArrayList;
import java.util.List;
import org.example.expressions.CellReference;
import org.example.expressions.Expression;
import org.example.expressions.FunctionExpression;
import org.example.expressions.RangeExpression;
import org.example.util.FunctionType;
import org.example.util.exceptions.ParseException;

public class Parser {
    private Table table;
    private List<Token> tokens;
    private int currIdx;

    public Parser(Table table) {
        this.table = table;
    }

    private void init(List<Token> tokens) {
        this.tokens = tokens;
        this.currIdx = 0;
    }

    public Expression parse(List<Token> tokens) {
        init(tokens);
        consume(TokenType.FORMULA_START);
        return parseExpression();
    }

    private Expression parseExpression() {
        Token token = peek();

        return switch (token.getType()) {
            case IDENTIFIER -> parseFunction();
            case CELL -> parseCell(token.getValue());
            default -> parseExpression();
        };
    }

    private Expression parseFunction() {
        Token token = peek();

        consume(TokenType.IDENTIFIER);
        FunctionType fnType = FunctionType.findByName(token.getValue());
        consume(TokenType.OPEN_PARENTHESES);

        List<Expression> arguments = new ArrayList<>();

        while (!match(TokenType.CLOSING_PARENTHESES)) {
            arguments.add(parseExpression());

            if(!match(TokenType.COMMA)) {
                break;
            }
        }

        consume(TokenType.CLOSING_PARENTHESES);
        return new FunctionExpression(fnType, arguments);
    }

    private CellReference parseCell(String cell) {
        String[] result = cell.splitWithDelimiters("[0-9]+", 2);
        String rowS = result[1];
        String colS = result[0];

        int row = Integer.parseInt(rowS);
        int col = 0;

        for (char c : colS.toCharArray()) {
            col *= 26 + (c - 'A' + 1);
        }

        return new CellReference(row, col - 1);
    }

    // looks at current token
    private Token peek() {
        return this.tokens.get(this.currIdx);
    }

    //
    private void consume(TokenType expected) {
        Token current = peek();
        TokenType currentType = current.getType();

        if (!currentType.equals(expected)) {
            throw new ParseException("Expected: " + expected + " but found " + currentType);
        }

        this.currIdx++;
    }

    private boolean match(TokenType expected) {
        Token current = peek();
        TokenType currentType = current.getType();

        return currentType.equals(expected);
    }

    private boolean isAtEnd() {
        return this.currIdx == tokens.size() - 1;
    }

    private Token lookAhead() {
        if (this.currIdx == this.tokens.size() - 1) {
            return new Token("", TokenType.EOF);
        }
        return this.tokens.get(this.currIdx + 1);
    }

}
