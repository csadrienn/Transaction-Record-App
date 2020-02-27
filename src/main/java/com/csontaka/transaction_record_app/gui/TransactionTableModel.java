package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.PeriodController;
import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

/**
 * Provides implementations for methods AbstractTableModel methods to display
 * the required fields in the
 * {@link com.csontaka.transaction_record_app.gui.TransactionTablePanel}'s JTable
 * member.
 *
 * @author Adrienn Csonták
 */
public class TransactionTableModel extends AbstractTableModel {

    private final String[] COL_NAMES = {"Id", "Date", "Name", "Price", "Amount"};
    private final Locale LOCAL = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCAL);
    private PeriodController perController;
    private AssetController assetController;
    private List<Transaction> transactions;

    /**Construct a TransactionTableModel with specified list of transactions,
     * AssetController object and PeriodController object.
     * 
     * @param transactions A List of Transaction objects.
     * @param assetController An AssetController object to create a connection
     * with the AssetRepository.
     * @param perController A PeriodController object to create a connection
     * with the PeriodRepository.
     */
    public TransactionTableModel(List<Transaction> transactions,
            AssetController assetController, PeriodController perController) {
        DECIMAL_FORMAT.applyPattern("##0.00");
        this.assetController = assetController;
        this.perController = perController;
        this.transactions = transactions;
    }

    /**
     * Inserts a Transaction object to the List class member.
     *
     * @param t A Transaction object to add to the List object.
     */
    public void addTransaction(Transaction t) {
        transactions.add(t);
        int row = transactions.size() - 1;
        fireTableRowsInserted(row, row);

    }

    @Override
    public void fireTableRowsInserted(int firstRow, int lastRow) {
        super.fireTableRowsInserted(firstRow, lastRow); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fireTableStructureChanged() {
        super.fireTableDataChanged();
    }

    @Override
    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        transactions.remove(lastRow);
        super.fireTableRowsDeleted(firstRow, lastRow); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getColumnName(int column) {
        return COL_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        } else if (columnIndex == 1) {
            return YearMonth.class;
        } else if (columnIndex == 2) {
            return String.class;
        } else if (columnIndex == 3) {
            return String.class;
        } else {
            return Integer.class;
        }
    }

    @Override
    public int getRowCount() {
        int size;
        if (transactions == null) {
            size = 0;
        } else {
            size = transactions.size();
        }
        return size;
    }

    @Override
    public int getColumnCount() {
        return COL_NAMES.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object temp = null;
        Transaction t = transactions.get(row);
        if (col == 0) {
            temp = t.getId();
        } else if (col == 1) {
            Integer periodId = t.getPeriodId();
            Period p = null;
            try {
                p = perController.findById(periodId);
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
            temp = p.getDate();
        } else if (col == 2) {
            try {
                Integer assetId = t.getAssetId();
                Asset a = assetController.findById(assetId);
                temp = a.getName() + "(" + a.getId() + ")";
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        } else if (col == 3) {
            double price = t.getPrice() / 100.0;
            String priceStr = DECIMAL_FORMAT.format(price);
            temp = priceStr;
        } else if (col == 4) {
            temp = t.getAmount();
        }
        return temp;
    }

}
