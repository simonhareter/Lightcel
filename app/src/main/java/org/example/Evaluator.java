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
import org.example.util.FunctionType;
import org.example.util.Range;
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

    public Object evaluate(Expression expr) {
        return switch (expr) {
            case BinaryExpression be -> {
                Expression leftExpr = be.getLeft();
                Expression rightExpr = be.getRight();
                String operator = be.getOperator().getValue();

                Object left = evaluate(leftExpr);
                Object right = evaluate(rightExpr);

                yield switch (operator) {
                    case "+" -> (double) left + (double) right;
                    case "-" -> (double) left - (double) right;
                    case "*" -> (double) left * (double) right;
                    case "/" -> (double) left / (double) right;
                    case "&" -> (String) left + (String) right;
                    case "^" -> Math.pow((double) left, (double) right);
                    default -> {
                        throw new EvaluationException("Unexpected operator for binary expression " + operator);
                    }
                };
            }
            case UnaryExpression ue -> {
                String operator = ue.getOperator().getValue();
                Expression rightExpr = ue.getRight();

                double right = (double) evaluate(rightExpr);

                yield switch (operator) {
                    case "-" -> -right;
                    case "+" -> right;
                    default -> throw new EvaluationException("Unexpected unary operator " + operator);
                };
            }
            case PostfixExpression pe -> {
                Expression leftExpr = pe.getLeft();
                String operator = pe.getOperator().getValue();

                double left = (double) evaluate(leftExpr);

                if (operator.equals("%")) {
                    yield left / 100;
                } else {
                    throw new EvaluationException("Unexpected operator " + operator);
                }
            }
            case FunctionExpression fe -> {
                FunctionType funcType = fe.getFunction();
                List<Expression> arguments = fe.getArguments();

                List<Object> values = new ArrayList<>();

                for (int i = 0; i < arguments.size(); i++) {
                    Object result = evaluate(arguments.get(i));

                    if (result instanceof Range range) {
                        for (Cell cell : table.getRange(range.start(), range.end())) {
                            values.add(evaluateCell(cell));
                        }
                    } else {
                        values.add(result);
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
                    yield new Range(refS, refE);
                }

                throw new EvaluationException("Range does not consist of 2 Reference types start and end");
            }
            case CellReference cr -> {
                Cell cell = table.getCell(cr.getRow(), cr.getColumn());
                yield evaluateCell(cell);
            }
            case RowReference rowR -> rowR.getRow();
            case ColumnReference colR -> colR.getColumn();
            case NumberLiteral nl -> nl.getValue();
            case StringLiteral sl -> sl.getValue();
            default -> {
                throw new EvaluationException("Unexpected expression " + expr.toString());
            }
        };
    }

    private Object evaluateCell(Cell cell) {
        if (cell.getEvaluatedValue() != null) {
            return cell.getEvaluatedValue();
        }

        if (evaluating.contains(cell)) {
            Object circularRef = "#REF!";
            return circularRef;
        }

        evaluating.add(cell);

        Object result;

        if (cell.getCellType() == CellType.FORMULA) {
            List<Token> tokens = tokenizer.tokenize(cell.getValue().toString());
            Expression e = parser.parse(tokens);
            result = evaluate(e);
        } else {
            result = cell.getValue();
        }

        evaluating.remove(cell);

        cell.setEvaluatedValue(result);
        return result;
    }

    private double sum(List<Object> values) {
        double sum = 0;
        for (Object value : values) {
            if (value instanceof Range range) {
                for (Cell cell : table.getRange(range.start(), range.end())) {
                    Object evaluatedValue = cell.getEvaluatedValue();

                    if (evaluatedValue == null) {
                        evaluatedValue = this.evaluateCell(cell);
                    }

                    sum += (double) evaluatedValue;
                }
            } else {
                sum += (double) value;
            }
        }
        return sum;
    }

    private double average(List<Object> values) {
        return sum(values) / values.size();
    }

    private double min(List<Object> values) {
        double min = (double) values.getFirst();
        for (Object value : values) {
            if (value instanceof Range range) {
                for (Cell cell : table.getRange(range.start(), range.end())) {
                    Object evaluatedValue = cell.getEvaluatedValue();

                    if (evaluatedValue == null) {
                        evaluatedValue = this.evaluateCell(cell);
                    }

                    if ((double) evaluatedValue < min) {
                        min = (double) evaluatedValue;
                    }
                }
            } else {
                if ((double) value < min) {
                    min = (double) value;
                }
            }
        }
        return min;
    }

    private double max(List<Object> values) {
        double max = (double) values.getFirst();
        for (Object value : values) {
            if (value instanceof Range range) {
                for (Cell cell : table.getRange(range.start(), range.end())) {
                    Object evaluatedValue = cell.getEvaluatedValue();

                    if (evaluatedValue == null) {
                        evaluatedValue = this.evaluateCell(cell);
                    }

                    if ((double) evaluatedValue > max) {
                        max = (double) evaluatedValue;
                    }
                }
            } else {
                if ((double) value > max) {
                    max = (double) value;
                }
            }
        }
        return max;
    }

    private double count(List<Object> values) {
        return values.size();
    }

}
