
package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.PeriodController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.Period;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.table.TableRowSorter;

/**Contains a JTable to display information about incomes in one-month periods.
 *
 * @author Adrienn Csonták
 */
public class SummaryTablePanel extends JPanel implements ItemListener{

    private AssetController assetController;
    private PeriodController perController;
    private TransactionController transController;
    
    private JLabel title;
    private JPanel titlePanel;
    private JTable table;
    private SummaryTableModel tableModel;
    private JComboBox timeCombo;
    private TableRowSorter<SummaryTableModel> sorter;
    private TableColorCellRenderer renderer;
    private final String[] TIME_COMBO_OPTIONS = {"all", "past 2 years", "past 1 year",
        "past 6 months", "past 3 months", "this month"};

    /**
     * Constructs a SummaryTablePanel with specified AssetController, PeriodController and
     * TransactionController.
     * @param assetController An AssetController object to create a connection
     * with the AssetRepository.
     * @param perController A PeriodController object to create a connection
     * with the PeriodRepository.
     * @param transController A TransactionController object to create a
     * connection with the TransactionRepository.
     */
    public SummaryTablePanel(AssetController assetController, 
            PeriodController perController, TransactionController transController) {
        this.assetController = assetController;
        this.perController = perController;
        this.transController = transController;
        List<Period> periods = new ArrayList<>();
        try {
            periods = perController.findAfterADate(YearMonth.now());
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        
        setLayout(new BorderLayout());

        titlePanel = new JPanel(new GridBagLayout());
        title = new JLabel("Summary");
        title.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        timeCombo = new JComboBox(TIME_COMBO_OPTIONS);
        timeCombo.setSelectedIndex(4);
        timeCombo.addItemListener(this);

        tableModel = new SummaryTableModel(periods, 
                transController, assetController);
        sorter = new TableRowSorter<>(tableModel);
        renderer = new TableColorCellRenderer();
        table = new JTable(tableModel);
        table.setRowSorter(sorter);
        table.setDefaultRenderer(Object.class, renderer);
        newFilter(YearMonth.now().minusMonths(4));

        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(10, 0, 0, 0);
        titlePanel.add(title, gc);
        gc.gridy = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.insets = new Insets(30, 13, 0, 0);
        titlePanel.add(timeCombo, gc);

        add(titlePanel, BorderLayout.NORTH);
        add(scrollpane, BorderLayout.CENTER);
    }
    
    private void newFilter(YearMonth startDate) {
        RowFilter<SummaryTableModel, Integer> dateFilter = new RowFilter<SummaryTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends SummaryTableModel, ? extends Integer> entry) {
                SummaryTableModel tableModel = entry.getModel();
                int i = entry.getIdentifier();
                YearMonth ym = (YearMonth) tableModel.getValueAt(i, 0);
                if (ym.isAfter(startDate)) {
                    return true;
                }
                return false;
            }
        };
        sorter.setRowFilter(dateFilter);
    }

    private void noFilter() {
        RowFilter<SummaryTableModel, Integer> dateFilter = new RowFilter<SummaryTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends SummaryTableModel, ? extends Integer> entry) {
                return true;
            }
        };
        sorter.setRowFilter(dateFilter);
    }

    /**
     * Invokes the fireTableDataChanged method of the
     * {@link com.csontaka.transaction_record_app.gui.SummaryTableModel}.
     */
    public void refresh() {
        tableModel.fireTableDataChanged();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == timeCombo) {
            YearMonth now = YearMonth.now();
            YearMonth startDate = null;
            int index = timeCombo.getSelectedIndex();
            String regex = null;

            switch (index) {
                case 1:
                    startDate = now.minusYears(2).minusMonths(1);
                    newFilter(startDate);
                    break;
                case 2:
                    startDate = now.minusYears(1).minusMonths(1);
                    newFilter(startDate);
                    break;
                case 3:
                    startDate = now.minusMonths(7);
                    newFilter(startDate);
                    break;
                case 4:
                    startDate = now.minusMonths(4);
                    newFilter(startDate);
                    break;
                case 5:
                    startDate = now.minusMonths(1);
                    newFilter(startDate);
                    break;

                default:
                    noFilter();
            }

        }
    }
}
