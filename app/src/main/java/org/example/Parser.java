package org.example;

public class Parser {
    private Table table;

    public Parser(Table table) {
        this.table = table;
    }

    void executeFormulas() {
        // SUM AVERAGE COUNT MIN MAX
        // detect circular definitions
        // only parse numbers, ignore string

        IO.println("Executing formulas...");

        for (int row = 0; row <= table.getMaxInsertedRow(); row++) {
            for (int col = 0; col <= table.getMaxInsertedCol(); col++) {
                if (table.getCell(row, col) != null) {
                    if (table.getCell(row, col).getCellType() == CellType.FORMULA) {
                        FunctionType type = extractFunctionType(row, col);
                        String[] cells = extractCells();
                    }
                }
            }
        }

        IO.println();
        IO.println("-------------------------------");
    }

    FunctionType extractFunctionType(int row, int col) {
        int startIdx = 1, openingBracketIdx = 0;
        String value = (String) table.getCell(row, col).getValue();

        for (int i = 0; i < value.length(); i++) {
            if (value.charAt(i) == '(') {
                openingBracketIdx = i;
            }
        }

        // Starting at idx 1 since a function starts with =
        // Example: =SUM(A1,A2);
        String substr = value.substring(startIdx, openingBracketIdx);
        return FunctionType.valueOf(substr);
    }

    // Extracts cells out of formulas.
    // Examples: =SUM(A1,A3) -> [A1,A2]; =AVG(A1:A3) -> [A1,A2,A3];
    String[] extractCells() {
        return new String[10];
    }

}
