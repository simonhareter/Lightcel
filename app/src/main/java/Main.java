import org.example.CsvReader;
import org.example.Evaluator;
import org.example.Lightcel;
import org.example.Parser;
import org.example.Table;
import org.example.Token;
import org.example.Tokenizer;

/**
 * Entry point of Lightcel
 *
 * @param args command-line arguments passed to Lightcel
 */
void main(String[] args) {
    // Lightcel lightcel = initializeLightcel();
    // lightcel.start(args);

    Tokenizer tokenizer = new Tokenizer();
    String input = "=2.5032^3";
    List<Token> tokens = tokenizer.tokenize(input);
    tokenizer.printTokenList(tokens);
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
