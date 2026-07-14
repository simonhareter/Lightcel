package org.example;

import java.util.List;
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
import org.example.util.Functions;
import org.example.util.Range;
import org.example.util.exceptions.EvaluationException;

public class Evaluator {
    private final Table table;

    public Evaluator(Table table) {
        this.table = table;
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

                Object[] values = new Object[arguments.size()];

                for (int i = 0; i < arguments.size(); i++) {
                    Object result = evaluate(arguments.get(i));

                    if (result == null) {
                        values[i] = 0;
                    } else {
                        values[i] = evaluate(arguments.get(i));
                    }
                }

                yield switch (funcType) {
                    case SUM -> Functions.sum(values);
                    case MAX -> Functions.max(values);
                    case MIN -> Functions.min(values);
                    case AVERAGE -> Functions.average(values);
                    case COUNT -> Functions.count(values);
                    default -> {
                        throw new EvaluationException("Unknown function type " + funcType);
                    }
                };
            }
            case RangeExpression re -> {
                Reference startRef = re.getStart();
                Reference endRef = re.getEnd();

                Object start = evaluate(startRef);
                Object end = evaluate(endRef);

                yield new Range(start, end);
            }
            case CellReference cr -> {
                int row = cr.getRow();
                int col = cr.getColumn();

                yield table.getCell(row, col).getValue();
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
}
