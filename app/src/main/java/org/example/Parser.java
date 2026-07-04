package org.example;

public class Parser {

    void parseInputFile(String file) {
        Path fp = Paths.get(file).toAbsolutePath().normalize();
        IO.println("Parsing input.csv...\n");
        try (BufferedReader br = Files.newBufferedReader(fp)) {
            String line;
            int row = 0;
            int col = 0;
            while ((line = br.readLine()) != null) {
                String[] elements = line.split(",");

                for (String element : elements) {
                    if (element != "") {
                        table.setCell(row, col, element, CellType.getTypeFromString(element));
                        if (row > table.getMaxInsertedRow()) {
                            table.setMaxInsertedRow(row);
                        }
                        if (col > table.getMaxInsertedCol()) {
                            table.setMaxInsertedCol(col);
                        }
                    }
                    col++;
                }
                row++;
                col = 0;
            }

            IO.println("Max inserted row = " + table.getMaxInsertedRow());
            IO.println("Max inserted col = " + table.getMaxInsertedCol());
            IO.println("-------------------------------");

        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
