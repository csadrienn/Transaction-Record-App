
package com.csontaka.transaction_record_app.exporting;

import javax.swing.JTable;

/** Interface for exporting the values of a <code>JTable</code> object. 
 *  The class that implements it has to to override its export method. 
 *
 * @author Adrienn Csontak
 */
public interface TableExport {

    /** Exports the values of a received <code>JTable</code> object to a file.
     *
     * @param table A <code>JTable</code> object containing the values to export.
     * @param fileName A String object containing fully qualified file name of a file.
     */
    public void export(JTable table, String fileName);
    
}
