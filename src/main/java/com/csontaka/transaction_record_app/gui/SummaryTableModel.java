package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

/**
 * Provides implementations for methods AbstractTableModel methods to display
 * the required fields in the
 * {@link com.csontaka.transaction_record_app.gui.SummaryTablePanel}'s JTable
 * member.
 *
 * @author Adrienn Csontak
 */
class SummaryTableModel extends AbstractTableModel {

    private List<Period> periods;
    private TransactionController transController;
    private AssetController assetController;
    private final String[] COL_NAMES = {"Month", "Amount", "Income", "Goal"};
    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);

    /**
     * Construct a <code>SummaryTablePanel</code> with specified list of
     * periods, TransactionController object and AssetController object.
     *
     * @param periods A List of <code>Period</code> objects.
     * @param transController A <code>TransactionController</code> object to
     * create a connection with the <code>TransactionRepository</code>.
     * @param assetController An <code>AssetController</code> object to create a
     * connection with the <code>AssetRepository</code>.
     */
    public SummaryTableModel(List<Period> periods,
            TransactionController transController, AssetController assetController) {
        this.periods = periods;
        this.transController = transController;
        this.assetController = assetController;
        DECIMAL_FORMAT.applyPattern("##0.00");
    }

    /**
     * Sets the List of <code>Period</code> objects to provide the data for
     * displaying. Invokes the fireTableDataChanged method of the
     * <code>AbstractTableModel</code>
     *
     * @param periods List of <code>Period</code> to set the class member.
     */
    public void setData(List<Period> periods) {
        this.periods = periods;
        fireTableDataChanged();
    }

    @Override
    public String getColumnName(int column) {
        return COL_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (periods.isEmpty()) {
            return Object.class;
        }
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public int getRowCount() {
        int size;
        if (periods == null) {
            size = 0;
        } else {
            size = periods.size();
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
        Period p = periods.get(row);
        List<Transaction> incomes = new ArrayList<>();
        try {
            incomes = transController.findAllIncome();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        if (col == 0) {
            temp = p.getDate();
        } else if (col == 1) {
            int toltalSold = 0;
            for (Transaction t : incomes) {
                if (t.getPeriodId().equals(p.getId())) {
                    toltalSold += t.getAmount();
                }
            }
            temp = toltalSold;
        } else if (col == 2) {
            int totalInt = 0;
            for (Transaction t : incomes) {
                if (t.getPeriodId().equals(p.getId())) {
                    totalInt = t.getAmount() * t.getPrice();
                }
            }
            double total = totalInt / 100.0;
            String totalStr = DECIMAL_FORMAT.format(total);
            temp = totalStr;
        } else if (col == 3) {
            double goal = p.getGoal() / 100.0;
            String goalStr = DECIMAL_FORMAT.format(goal);
            temp = goalStr;
        }
        return temp;
    }
}
