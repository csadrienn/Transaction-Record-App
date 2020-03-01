package com.csontaka.transaction_record_app.exporting;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import javax.swing.JTable;

/**
 * Exports the values shown in the table to a pdf file. Implements the
 * {@link com.csontaka.transaction_record_app.exporting.TableExport} interface export
 * method.
 *
 * @author Adrienn Csontak
 */
public class PdfExportImp implements TableExport {

    /**
     * Constructs a new PdfExportImp.
     */
    public PdfExportImp() {

    }

    @Override
    public void export(JTable table, String fileName) {
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(fileName));
            doc.open();
            PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
            printHeader(table, pdfTable);
            printRows(table, pdfTable);
            doc.add(pdfTable);
            doc.close();
        } catch (DocumentException | FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }
    
    private void printHeader(JTable table, PdfPTable pdfTable){
            for (int column = 0; column < table.getColumnCount(); column++) {
                PdfPCell pdfCell = new PdfPCell(new Phrase(table.getColumnName(column)));
                pdfCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                pdfTable.addCell(pdfCell);
            }
    }
    private void printRows(JTable table, PdfPTable pdfTable){
            for (int row = 0; row < table.getRowCount(); row++) {
                System.out.println("rows: " + row);
                for (int column = 0; column < table.getColumnCount(); column++) {
                    pdfTable.addCell(table.getValueAt(row, column).toString());
                }
            }
    }
}
