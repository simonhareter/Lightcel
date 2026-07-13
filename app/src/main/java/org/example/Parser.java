package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.example.expressions.BinaryExpression;
import org.example.expressions.CellReference;
import org.example.expressions.ColumnReference;
import org.example.expressions.Expression;
import org.example.expressions.FunctionExpression;
import org.example.expressions.NumberLiteral;
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

    // Entry ----------------------------------------------------

    public Expression parse(List<Token> tokens) {
        init(tokens);
        consume(TokenType.FORMULA_START);

        Expression parseResult = parseExpression();

        if (!isAtEnd()) {
            throw new ParseException("Unexpected token: " + peek());
        }

        return parseResult;
    }

    // Recursive Descent ----------------------------------------------------

    private Expression parseExpression() {
        return parseAddition();
    }

    private Expression parseAddition() {
        Expression left = parseMultiplication();

        while (matchMathOperator("+") || matchMathOperator("-") || match(TokenType.CONCATENATION_OPERATOR)) {
            Token operator = peek();

            if (operator.getType().equals(TokenType.CONCATENATION_OPERATOR)) {
                consume(TokenType.CONCATENATION_OPERATOR);
            } else {
                consume(TokenType.MATH_OPERATOR);
            }

            Expression right = parseMultiplication();
            left = new BinaryExpression(left, operator, right);
        }

        return left;
    }

    private Expression parseMultiplication() {
        Expression left = parseUnary();

        while (matchMathOperator("*") || matchMathOperator("/") || matchMathOperator("^")) {
            Token operator = peek();
            consume(TokenType.MATH_OPERATOR);

            Expression right = parseUnary();
            left = new BinaryExpression(left, operator, right);
        }

        return left;
    }

    private Expression parseUnary() {
        if (matchMathOperator("+") || matchMathOperator("-")) {
            Token operator = peek();
            consume(TokenType.MATH_OPERATOR);

            Expression right = parseUnary();
            return new UnaryExpression(operator, right);
        }

        return parsePostFix();
    }

    private Expression parsePostFix() {
        Expression left = parseBase();

        if (matchPostFixOperator()) {
            Token operator = peek();
            consume(TokenType.PERCENTAGE_OPERATOR);

            left = new PostfixExpression(left, operator);
        }

        return left;
    }

    private Expression parseBase() {
        Token token = peek();

        return switch (token.getType()) {
            case IDENTIFIER -> parseFunction();
            case CELL -> parseReference(token.getValue());
            case COLON -> parseRange();
            case NUMBER -> parseNumberLiteral();
            case STRING, EMPTY_STRING -> parseStringLiteral();
            default -> throw new ParseException("Unexpected token");
        };
    }

    // Function ----------------------------------------------------

    private Expression parseFunction() {
        Token token = peek();

        consume(TokenType.IDENTIFIER);
        FunctionType fnType = FunctionType.findByName(token.getValue());
        consume(TokenType.OPEN_PARENTHESES);

        List<Expression> arguments = new ArrayList<>();

        while (!match(TokenType.CLOSING_PARENTHESES)) {
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

            consume(peek().getType());
        }

        consume(TokenType.CLOSING_PARENTHESES);
        return new FunctionExpression(fnType, arguments);
    }

    // Range ----------------------------------------------------

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

    // Reference ----------------------------------------------------

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
            result = result * 26 + (c - 'A' + 1);
        }

        return result - 1;
    }

    // Literals -------------------------------------------------------

    private Expression parseNumberLiteral() {
        Token token = peek();
        consume(TokenType.NUMBER);
        return new NumberLiteral(Double.parseDouble(token.getValue()));
    }

    private Expression parseStringLiteral() {
        Token token = peek();

        if (token.getType().equals(TokenType.STRING)) {
            consume(TokenType.STRING);
        } else {
            consume(TokenType.EMPTY_STRING);
        }

        return new StringLiteral(token.getValue());
    }

    // Parser helpers -----------------------------------------------------

    // looks at current token
    private Token peek() {
        if (this.currIdx >= this.tokens.size()) {
            return new Token("", TokenType.EOF);
        }
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

    private boolean isAtEnd() {
        return this.currIdx >= tokens.size() - 1;
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

    private boolean match(TokenType expected) {
        Token current = peek();
        TokenType currentType = current.getType();

        return currentType.equals(expected);
    }

    private boolean matchMathOperator(String operator) {
        return match(TokenType.MATH_OPERATOR) && peek().getValue().equals(operator);
    }

    private boolean matchPostFixOperator() {
        return match(TokenType.PERCENTAGE_OPERATOR) && peek().getValue().equals("%");
    }

    private boolean matchesAny(TokenType... types) {
        return Arrays.asList(types).stream().anyMatch(tokenType -> match(tokenType));
    }

    public void printExpressionLog(Expression e) {
        IO.println("Parser Result:");
        IO.println(e.toString());
        IO.println("-------------------------------");
    }
}
