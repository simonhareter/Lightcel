package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.expressions.CellReference;
import org.example.expressions.ColumnReference;
import org.example.expressions.Expression;
import org.example.expressions.FunctionExpression;
import org.example.expressions.NumberLiteral;
import org.example.expressions.RangeBounds;
import org.example.expressions.RangeExpression;
import org.example.expressions.Reference;
import org.example.expressions.RowReference;
import org.example.expressions.StringLiteral;
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
        String value = token.getValue();

        return switch (token.getType()) {
            case IDENTIFIER -> parseFunction();
            case CELL -> parseReference(value);
            case COLON -> parseRange();
            case NUMBER -> new NumberLiteral(Double.parseDouble(value));
            case STRING -> new StringLiteral(value);
            default -> throw new ParseException("Unknown tokentype");
        };
    }

    private Expression parseFunction() {
        Token token = peek();

        consume(TokenType.IDENTIFIER);
        FunctionType fnType = FunctionType.findByName(token.getValue());
        consume(TokenType.OPEN_PARENTHESES);

        List<Expression> arguments = new ArrayList<>();

        while (!match(TokenType.CLOSING_PARENTHESES)) {
            TokenType tknType = peek().getType();

            if (match(TokenType.COMMA)) {
                consume(TokenType.COMMA);
                continue;
            }

            // Check for range expression
            Token lookAhead = lookAhead();
            TokenType lAhType = lookAhead.getType();

            if (!lAhType.equals(TokenType.EOF) && lAhType.equals(TokenType.COLON)) {
                if (!isInvalidTokenType(TokenType.CELL, TokenType.NUMBER, TokenType.STRING)) {
                    consume(tknType);
                    continue;
                }
            }

            arguments.add(parseExpression());

            if (isInvalidTokenType(TokenType.OPEN_PARENTHESES, TokenType.CLOSING_PARENTHESES)) {
                break;
            }
            consume(tknType);
        }

        consume(TokenType.CLOSING_PARENTHESES);
        return new FunctionExpression(fnType, arguments);
    }

    private RangeExpression parseRange() {
        Token left = lookBack();
        Token right = lookAhead();

        Reference leftRef = parseReference(left.getValue());
        Reference rightRef = parseReference(right.getValue());

        return normalizeRange(leftRef, rightRef);
    }

    private RangeExpression normalizeRange(Reference left, Reference right) {
        int leftMaxInsertedRowOrCol, rightMaxInsertedRowOrCol;

        if (left instanceof RowReference) {
            leftMaxInsertedRowOrCol = table.getMaxInsertedCol();
        } else {
            leftMaxInsertedRowOrCol = table.getMaxInsertedRow();
        }

        if (right instanceof RowReference) {
            rightMaxInsertedRowOrCol = table.getMaxInsertedCol();
        } else {
            rightMaxInsertedRowOrCol = table.getMaxInsertedRow();
        }

        RangeBounds leftBounds = left.toBounds(leftMaxInsertedRowOrCol);
        RangeBounds rightBounds = right.toBounds(rightMaxInsertedRowOrCol);

        
    }

    private Reference parseReference(String input) {
        if (input.matches("[0-9]+")) {
            int row = Integer.parseInt(input);
            return new RowReference(row);
        }

        if (input.matches("[A-Za-z]+")) {
            int col = columnToIndex(input);
            return new ColumnReference(col);
        }

        String[] result = input.splitWithDelimiters("[0-9]+", 2);
        String rowS = result[1];
        String colS = result[0];

        int row = Integer.parseInt(rowS);
        int col = columnToIndex(colS);

        return new CellReference(row, col - 1);
    }

    private int columnToIndex(String col) {
        int result = 0;

        for (char c : col.toUpperCase().toCharArray()) {
            result *= 26 + (c - 'A' + 1);
        }
        return result;
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

    private Token lookBack() {
        if (this.currIdx == 0) {
            return new Token("", TokenType.EOF);
        }
        return this.tokens.get(this.currIdx - 1);
    }

    private boolean isInvalidTokenType(TokenType... types) {
        return Arrays.asList(types).stream().anyMatch(tokenType -> match(tokenType));
    }
}
