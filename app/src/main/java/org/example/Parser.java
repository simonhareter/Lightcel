package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.expressions.CellReference;
import org.example.expressions.ColumnReference;
import org.example.expressions.Expression;
import org.example.expressions.FunctionExpression;
import org.example.expressions.PostfixExpression;
import org.example.expressions.RangeBounds;
import org.example.expressions.RangeExpression;
import org.example.expressions.Reference;
import org.example.expressions.RowReference;
import org.example.expressions.StringLiteral;
import org.example.expressions.UnaryExpression;
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
        TokenType type = token.getType();
        String value = token.getValue();

        return switch (type) {
            case IDENTIFIER -> parseFunction();
            case CELL -> parseReference(value);
            case COLON -> parseRange();
            case NUMBER, MATH_OPERATOR -> parseNumber();
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
                if (matchesAny(TokenType.CELL, TokenType.NUMBER, TokenType.STRING)) {
                    consume(TokenType.CELL);
                    continue;
                }
            }

            arguments.add(parseExpression());

            if (matchesAny(TokenType.OPEN_PARENTHESES, TokenType.CLOSING_PARENTHESES)) {
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

        consume(TokenType.COLON);
        consume(TokenType.CELL);
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

        int leftCellRow = Math.min(leftBounds.getStartRow(), rightBounds.getStartRow());
        int leftCellCol = Math.min(leftBounds.getStartCol(), rightBounds.getStartCol());
        int rightCellRow = Math.max(leftBounds.getEndRow(), rightBounds.getEndRow());
        int rightCellCol = Math.max(leftBounds.getEndCol(), rightBounds.getEndCol());

        CellReference leftCell = new CellReference(leftCellRow, leftCellCol);
        CellReference rightCell = new CellReference(rightCellRow, rightCellCol);

        return new RangeExpression(leftCell, rightCell);
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

        int row = rowToIndex(rowS);
        int col = columnToIndex(colS);

        return new CellReference(row, col);
    }

    private int rowToIndex(String row) {
        // table is 0-indexed. B2: (row = 1, col = 1)
        int indexOffset = 1;
        return Integer.parseInt(row) - indexOffset;
    }

    private int columnToIndex(String col) {
        int result = 0;

        for (char c : col.toUpperCase().toCharArray()) {
            result *= 26 + (c - 'A' + 1);
        }
        return result;
    }

    private Expression parseNumber() {
        Token token = peek();
        TokenType type = token.getType();
        String value = token.getValue();

        if (isUnaryMathOperator(value)) {
            parseUnary();
        }

        return null;
    }

    private UnaryExpression parseUnary() {
        Token operator = peek();

        consume(TokenType.MATH_OPERATOR);

        if (!matchesAny(TokenType.NUMBER, TokenType.IDENTIFIER, TokenType.CELL)) {
            throw new ParseException(
                    "Invalid token type " + peek().getType() + " after MATH_OPERATOR " + operator.getValue());
        }

        Expression right = parseExpression();
        return new UnaryExpression(operator, right);
    }

    private PostfixExpression parsePostfix() {
        return null;
    }

    private boolean isUnaryMathOperator(String operator) {
        if (!operator.equals("+") || !operator.equals("-")) {
            return false;
        }

        Token left = lookBack();
        Token right = lookAhead();

        if (left.getType().equals(TokenType.OPEN_PARENTHESES)) {
            return true;
        }

        if (left.getType().matches(TokenType.NUMBER, TokenType.CELL, TokenType.IDENTIFIER)) {
            return false;
        }

        if (!right.getType().matches(TokenType.NUMBER, TokenType.CELL, TokenType.IDENTIFIER)) {
            return false;
        }

        return true;
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

    private boolean matchesAny(TokenType... types) {
        return Arrays.asList(types).stream().anyMatch(tokenType -> match(tokenType));
    }

}
