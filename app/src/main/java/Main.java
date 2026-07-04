import org.example.*;

void main(String[] args) {
    Lightcel lightcel = initializeLightcel();
    lightcel.start(args);
}

Lightcel initializeLightcel() {
    Parser parser = new Parser();
    Table table = new Table();
    return new Lightcel(parser, table);
}
