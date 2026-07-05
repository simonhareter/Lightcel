package org.example;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvReader {
    private final Table table;

    public CsvReader(Table table) {
        this.table = table;
    }

    void importCsv(String inputFile) {
        Path fp = Paths.get(inputFile).toAbsolutePath().normalize();
        IO.println("Parsing input.csv...\n");
        try (BufferedReader br = Files.newBufferedReader(fp)) {
            String line;
            int row = 0;
            int col = 0;
            while ((line = br.readLine()) != null) {
                String[] elements = line.split(",");

                for (String element : elements) {
                    fillTableCell(element, row, col);
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

    void fillTableCell(String element, int row, int col) {
        table.setCell(row, col, element, CellType.getTypeFromString(element));
        if (row > table.getMaxInsertedRow()) {
            table.setMaxInsertedRow(row);
        }
        if (col > table.getMaxInsertedCol()) {
            table.setMaxInsertedCol(col);
        }
    }
}
