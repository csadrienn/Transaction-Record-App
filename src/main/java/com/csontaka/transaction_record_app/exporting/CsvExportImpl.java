package com.csontaka.transaction_record_app.exporting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * Exports the values shown in the table to a csv file. Implements the
 * {@link com.csontaka.transaction_record_app.exporting.TableExport} interface export
 * method.
 *
 * @author Adrienn Csontak
 */
public class CsvExportImpl implements TableExport {

    /**
     * The sign that separates the values.
     */
    public static final String separator = ";";

    /**
     * Constructs a new CsvExportImpl.
     */
    public CsvExportImpl() {

    }

    @Override
    public void export(JTable table, String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            bw.write(makeHeader(table));
            bw.newLine();
            bw.write(makeLines(table));
            bw.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String makeHeader(JTable table) {
        TableModel tableModel = table.getModel();
        int columns = tableModel.getColumnCount();

        StringBuilder header = new StringBuilder();
        for (int column = 0; column < columns; column++) {
            header.append(tableModel.getColumnName(column));
            header.append(separator);
        }
        header.deleteCharAt(header.lastIndexOf(separator));
        return header.toString();
    }

    private StringBuilder makeLine(JTable table, int row) {
        int columns = table.getColumnCount();

        StringBuilder line = new StringBuilder();
        for (int column = 0; column < columns; column++) {
            line.append(table.getValueAt(row, column));
            line.append(separator);
        }
        line.deleteCharAt(line.lastIndexOf(separator));
        return line;
    }

    private String makeLines(JTable table) throws IOException {
        int rows = table.getRowCount();
        StringBuilder lines = new StringBuilder();
        for (int row = 0; row < rows; row++) {
            lines.append(makeLine(table, row));
            if (row != rows - 1) {
                lines.append("\n");
            }
        }
        return lines.toString();
    }

}
