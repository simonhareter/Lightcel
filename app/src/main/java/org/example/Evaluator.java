package org.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.example.expressions.BinaryExpression;
import org.example.expressions.CellReference;
import org.example.expressions.ColumnReference;
import org.example.expressions.Expression;
import org.example.expressions.FunctionExpression;
import org.example.expressions.NumberLiteral;
import org.example.expressions.PostfixExpression;
import org.example.expressions.RangeExpression;
import org.example.expressions.Reference;
import org.example.expressions.RowReference;
import org.example.expressions.StringLiteral;
import org.example.expressions.UnaryExpression;
import org.example.util.ErrorValue;
import org.example.util.FunctionType;
import org.example.util.NumberValue;
import org.example.util.RangeValue;
import org.example.util.StringValue;
import org.example.util.Value;
import org.example.util.exceptions.EvaluationException;

public class Evaluator {
    private final Table table;
    private final Tokenizer tokenizer;
    private final Parser parser;
    private final Set<Cell> evaluating = new HashSet<>();

    public Evaluator(Table table, Tokenizer tokenizer, Parser parser) {
        this.table = table;

        this.tokenizer = tokenizer;
        this.parser = parser;
    }

    public Value evaluate(Expression expr, Cell source) {
        return switch (expr) {
            case BinaryExpression be -> {
                Expression leftExpr = be.getLeft();
                Expression rightExpr = be.getRight();
                String operator = be.getOperator().getValue();

                Value left = evaluate(leftExpr, source);
                Value right = evaluate(rightExpr, source);

                if (left instanceof ErrorValue) {
                    yield left;
                }

                if (right instanceof ErrorValue) {
                    yield right;
                }

                yield switch (operator) {
                    case "+", "&" -> add(left, right);
                    case "-" -> subtract(left, right);
                    case "*" -> multiply(left, right);
                    case "/" -> divide(left, right);
                    case "^" -> pow(left, right);
                    default -> {
                        throw new EvaluationException("Unexpected operator for binary expression " + operator);
                    }
                };
            }
            case UnaryExpression ue -> {
                String operator = ue.getOperator().getValue();
                Expression rightExpr = ue.getRight();

                Value right = evaluate(rightExpr, source);

                yield switch (operator) {
                    case "-" -> negate(right);
                    case "+" -> right;
                    default -> throw new EvaluationException("Unexpected unary operator " + operator);
                };
            }
            case PostfixExpression pe -> {
                Expression leftExpr = pe.getLeft();
                String operator = pe.getOperator().getValue();

                Value left = evaluate(leftExpr, source);

                double percent = 100;
                NumberValue denominator = new NumberValue(percent);

                if (operator.equals("%")) {

                    yield divide(left, denominator);
                } else

                {
                    throw new EvaluationException("Unexpected operator " + operator);
                }
            }
            case FunctionExpression fe -> {
                FunctionType funcType = fe.getFunction();
                List<Expression> arguments = fe.getArguments();

                List<NumberValue> values = new ArrayList<>();

                for (int i = 0; i < arguments.size(); i++) {
                    Value result = evaluate(arguments.get(i), source);

                    switch (result) {
                        case StringValue _ -> {
                            NumberValue zero = new NumberValue(0);
                            values.add(zero);
                        }
                        case ErrorValue err -> {
                            yield err;
                        }
                        case NumberValue num -> values.add(num);
                        case RangeValue range -> {
                            for (Cell cell : table.getRange(range.start(), range.end())) {
                                Value evalValue = evaluateCell(cell, source);

                                switch (evalValue) {
                                    case StringValue _ -> {
                                        continue;
                                    }
                                    case ErrorValue err -> {
                                        if (source.equals(cell)) {
                                            yield err;
                                        }

                                        if (funcType.equals(FunctionType.COUNT)) {
                                            continue;
                                        }

                                        yield err;
                                    }
                                    case NumberValue num -> values.add(num);
                                    default -> {
                                        // skip others
                                    }
                                }
                            }
                        }
                    }
                }

                yield switch (funcType) {
                    case SUM -> sum(values);
                    case MAX -> max(values);
                    case MIN -> min(values);
                    case AVERAGE -> average(values);
                    case COUNT -> count(values);
                    default -> {
                        throw new EvaluationException("Unknown function type " + funcType);
                    }
                };
            }
            case RangeExpression re -> {
                Reference startRef = re.getStart();
                Reference endRef = re.getEnd();

                if (startRef instanceof CellReference refS && endRef instanceof CellReference refE) {
                    yield new RangeValue(refS, refE);
                }

                throw new EvaluationException("Range does not consist of 2 Reference types start and end");
            }
            case CellReference cr -> {
                Cell cell = table.getCell(cr.getRow(), cr.getColumn());

                yield evaluateCell(cell, source);
            }
            case RowReference rowR -> new NumberValue(rowR.getRow());
            case ColumnReference colR -> new NumberValue(colR.getColumn());
            case NumberLiteral nl -> new NumberValue(nl.getValue());
            case StringLiteral sl -> new StringValue(sl.getValue());
            default -> {
                throw new EvaluationException("Unexpected expression " + expr.toString());
            }
        };
    }

    private Value evaluateCell(Cell cell, Cell source) {
        if (evaluating.contains(cell)) {
            return ErrorValue.REF;
        }

        if (cell.getEvaluatedValue() != null) {
            return cell.getEvaluatedValue();
        }

        evaluating.add(cell);

        Value result;

        if (cell.getCellType() == CellType.FORMULA) {
            List<Token> tokens = tokenizer.tokenize(cell.getValue().toString());
            Expression e = parser.parse(tokens);
            result = evaluate(e, source);
        } else {
            result = convertValue(cell.getValue());
        }

        evaluating.remove(cell);
        cell.setEvaluatedValue(result);
        return result;
    }

    private Value convertValue(Object value) {
        if (value instanceof Double d) {
            return new NumberValue(d);
        }

        if (value instanceof String s) {
            return new StringValue(s);
        }

        return ErrorValue.VALUE;
    }

    private Value add(Value left, Value right) {
        if (left instanceof NumberValue l && right instanceof NumberValue r) {
            return new NumberValue(l.value() + r.value());
        }

        if (left instanceof StringValue l && right instanceof StringValue r) {
            return new StringValue(l.value() + r.value());
        }

        return ErrorValue.VALUE;
    }

    private Value subtract(Value left, Value right) {
        if (left instanceof NumberValue l && right instanceof NumberValue r) {
            return new NumberValue(l.value() - r.value());
        }

        return ErrorValue.VALUE;
    }

    private Value multiply(Value left, Value right) {
        if (left instanceof NumberValue l && right instanceof NumberValue r) {
            return new NumberValue(l.value() * r.value());
        }

        return ErrorValue.VALUE;
    }

    private Value divide(Value left, Value right) {
        if (left instanceof NumberValue l && right instanceof NumberValue r) {
            return new NumberValue(l.value() / r.value());
        }

        return ErrorValue.VALUE;
    }

    private Value pow(Value left, Value right) {
        if (left instanceof NumberValue l && right instanceof NumberValue r) {
            return new NumberValue(Math.pow(l.value(), r.value()));
        }

        return ErrorValue.VALUE;
    }

    private Value negate(Value right) {
        if (right instanceof NumberValue r) {
            return new NumberValue(-r.value());
        }

        return ErrorValue.VALUE;
    }

    private NumberValue sum(List<NumberValue> values) {
        double sum = 0;
        for (NumberValue value : values) {
            sum += value.value();
        }
        return new NumberValue(sum);
    }

    private Value average(List<NumberValue> values) {
        NumberValue sum = sum(values);
        return new NumberValue(sum.value() / values.size());
    }

    private NumberValue min(List<NumberValue> values) {
        double min = values.getFirst().value();

        for (NumberValue value : values) {
            if (value.value() < min) {
                min = value.value();
            }
        }
        return new NumberValue(min);
    }

    private Value max(List<NumberValue> values) {
        double max = values.getFirst().value();

        for (NumberValue value : values) {
            if (value.value() > max) {
                max = value.value();
            }
        }

        return new NumberValue(max);
    }

    private NumberValue count(List<NumberValue> values) {
        return new NumberValue(values.size());
    }

}
