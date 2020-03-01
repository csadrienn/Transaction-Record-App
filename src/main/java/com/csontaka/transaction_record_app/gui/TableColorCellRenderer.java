package com.csontaka.transaction_record_app.gui;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

/**
 * Override the TableCellRenderer interface getTableCellRendererComponent method
 * to set the colours of the table of
 * {@link com.csontaka.transaction_records.gui.SummaryTablePanel}. If the goal
 * value is lower then the income value, than the cell of the goal is red, else
 * green.
 *
 * @author Adrienn Csontak
 */
public class TableColorCellRenderer implements TableCellRenderer {

    private static final TableCellRenderer RENDERER = new DefaultTableCellRenderer();
    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = RENDERER.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            if (column == 3) {

                DECIMAL_FORMAT.applyPattern("##0.00");
                String sep = DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator() + "";
                String goalStr = (String) table.getModel().getValueAt(row, column);
                String incomeStr = (String) table.getModel().getValueAt(row, 2);
                incomeStr = incomeStr.replace(sep, "");
                goalStr = goalStr.replace(sep, "");
                int income = Integer.parseInt(incomeStr);
                int goal = Integer.parseInt(goalStr);
                Color color = null;

                if (income >= goal) {
                    color = new Color(173, 255, 47);
                } else {
                    color = new Color(255, 99, 71);
                }
                c.setBackground(color);
            } else {
                c.setBackground(Color.white);
            }
        } else {
            c.setBackground(new Color(57, 105, 138));
        }

        return c;
    }
}
