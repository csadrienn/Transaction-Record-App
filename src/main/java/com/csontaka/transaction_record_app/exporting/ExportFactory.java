
package com.csontaka.transaction_record_app.exporting;

/** * Factory class providing <code>TableExport</code> objects.
 *
 * @author Adrienn Csontak
 */
public class ExportFactory {

    /**Provides a specific object of class that implements the TableExport interface
 based on the received String.
     *
     * @param exportType A String object containing the type of the export.
     * @return An <code>TableExport</code> object based on the received type.
     */
    public static TableExport getExport(String exportType){
        if(exportType.equalsIgnoreCase("csv")){
            return new CsvExportImpl();
        }
        if(exportType.equalsIgnoreCase("pdf")){
            return new PdfExportImp();
        }
        return null;
    }
}
