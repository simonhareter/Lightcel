import org.example.*;

/**
 * Entry point of Lightcel
 *
 * @param args command-line arguments passed to Lightcel
 */
void main(String[] args) {
    Lightcel lightcel = initializeLightcel();
    lightcel.start(args);
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
