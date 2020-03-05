package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.PeriodController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.exporting.TableExport;
import com.csontaka.transaction_record_app.exporting.ExportFactory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableRowSorter;

/**
 * Contains a JTable to display information about incomes in one-month periods.
 *
 * @author Adrienn Csontak
 */
public class SummaryTablePanel extends JPanel implements ItemListener {

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
    private JComboBox exportCombo;
    private final String[] TIME_COMBO_OPTIONS = {"all", "past 2 years", "past 1 year",
        "past 6 months", "past 3 months", "this month"};
    private final String[] EXPORT_COMBO_OPTIONS = {"Export", "csv", "pdf"};

    /**
     * Constructs a SummaryTablePanel with specified
     * <code>AssetController</code>, <code>PeriodController</code> and
     * <code>TransactionController</code>.
     *
     * @param assetController An <code>AssetController</code> object to create a
     * connection with the <code>AssetRepository</code>.
     * @param perController A <code>PeriodController</code> object to create a
     * connection with the <code>PeriodRepository</code>.
     * @param transController A <code>TransactionController</code> object to
     * create a connection with the <code>TransactionRepository</code>.
     */
    public SummaryTablePanel(AssetController assetController,
            PeriodController perController, TransactionController transController) {
        this.assetController = assetController;
        this.perController = perController;
        this.transController = transController;
        List<Period> periods = new ArrayList<>();
        try {
            periods = perController.findBeforeADate(YearMonth.now().plusMonths(1));
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

        setLayout(new BorderLayout());

        titlePanel = new JPanel(new GridBagLayout());
        title = new JLabel("Summary");
        title.setFont(new Font("Lucida Sans Unicode", 0, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        exportCombo = new JComboBox(EXPORT_COMBO_OPTIONS);
        exportCombo.addItemListener(this);

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

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(timeCombo);
        panel.add(exportCombo);

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(10, 0, 0, 0);
        titlePanel.add(title, gc);
        gc.gridy = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.insets = new Insets(30, 13, 0, 0);
        titlePanel.add(panel, gc);

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
     * Invokes the setData method of the <code>SummaryTableModel</code>.
     *
     * @param periods A List of <code>Period</code> objects to show in the
     * table.
     */
    public void refresh(List<Period> periods) {
        tableModel.setData(periods);
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getSource() == timeCombo) {
                doTimeComboEvent();
            }
            if (e.getSource() == exportCombo) {
                doExportComboEvent();
            }
        }

    }

    private void doTimeComboEvent() {
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
                System.out.println("startDate5: " + startDate);
                newFilter(startDate);
                break;

            default:
                noFilter();
        }
    }

    private void doExportComboEvent() {
        String choosen = (String) exportCombo.getSelectedItem();
        System.out.println("choosen: " + choosen);
        choosen = choosen.toLowerCase();

        if (!choosen.equalsIgnoreCase(EXPORT_COMBO_OPTIONS[0])) {
            JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
            jfc.setDialogTitle("Export");
            jfc.setAcceptAllFileFilterUsed(false);
            String extension = "." + choosen;
            String description = null;
            if (choosen.equals(EXPORT_COMBO_OPTIONS[1])) {
                description = "CSV(*.csv)";
            }
            if (choosen.equals(EXPORT_COMBO_OPTIONS[2])) {
                description = "PDF(*.pdf)";
            }

            FileNameExtensionFilter filter = new FileNameExtensionFilter(description, choosen);
            jfc.addChoosableFileFilter(filter);
            File file = getFileFromFileChooser(jfc, extension);
            if (file != null) {
                TableExport export = ExportFactory.getExport(choosen);
                export.export(table, file.getAbsolutePath());
            }
            exportCombo.setSelectedIndex(0);
        }

    }

    private File getFileFromFileChooser(JFileChooser jfc, String extension) {
        File file = null;
        int option = jfc.showOpenDialog(getRootPane());
        if (option == JFileChooser.APPROVE_OPTION) {
            file = jfc.getSelectedFile();
            boolean exists = file.exists();
            String message = "The file \"" + file.getName()
                    + "\" already exists. Do you want to overwrite it?";
            if (exists) {
                int overWriteConfirm = JOptionPane.showConfirmDialog(getRootPane(),
                        message, "Confirm", JOptionPane.YES_NO_OPTION);
                if (overWriteConfirm == JOptionPane.NO_OPTION) {
                    exportCombo.setSelectedIndex(0);
                    return null;
                }
            } else {
                String fileName = file.getName();
                String path = file.getAbsolutePath();
                if (!fileName.endsWith(extension)) {
                    fileName += extension;
                    file = new File(path + extension);
                }
            }
        }
        return file;
    }
}
