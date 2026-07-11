import org.example.CsvReader;
import org.example.Evaluator;
import org.example.Lightcel;
import org.example.Parser;
import org.example.Table;
import org.example.Token;
import org.example.Tokenizer;
import org.example.expressions.Expression;
import org.example.expressions.FunctionExpression;

/**
 * Entry point of Lightcel
 *
 * @param args command-line arguments passed to Lightcel
 */
void main(String[] args) {
    // Lightcel lightcel = initializeLightcel();
    // lightcel.start(args);

    Table table = new Table();
    Tokenizer tokenizer = new Tokenizer();
    Parser parser = new Parser(table);

    String s1 = "=SUM(A1,A2,A3)";
    String s2 = "=AVERAGE(A3:B10)";
    String s3 = "=3+2*3";

    List<Token> tokens = tokenizer.tokenize(s3);
    Expression e = parser.parse(tokens);

    if (e instanceof FunctionExpression fe) {
        IO.println(fe.getFunction());
        List<Expression> es = fe.getArguments();
        for (Expression exp : es) {
            IO.println(exp.getClass().getSimpleName());
        }
    }

    IO.println(e.toString());
}

/**
 * Provides the required objects for Lightcel.
 *
 */
Lightcel initializeLightcel() {
    Table table = new Table();
    CsvReader csvr = new CsvReader(table);
    Tokenizer tokenizer = new Tokenizer();
    Parser parser = new Parser(table);
    Evaluator evaluator = new Evaluator();
    return new Lightcel(csvr, table, tokenizer, parser, evaluator);
}
