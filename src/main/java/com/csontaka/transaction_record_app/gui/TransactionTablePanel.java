package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.PeriodController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.AssetType;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import com.csontaka.transaction_record_app.exporting.TableExport;
import com.csontaka.transaction_record_app.exporting.ExportFactory;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableRowSorter;

/**
 * Contains a JTable to display information about transactions and buttons to
 * delete transactions and to redirect to the transaction form to save or update
 * transactions. Implements the <code>ItemListener</code> interface
 * itemStateChanged method.
 *
 * @author Adrienn Csontak
 */
public class TransactionTablePanel extends JPanel implements ItemListener {

    private AssetController assetController;
    private PeriodController perController;
    private TransactionController transController;
    private AssetType assetType;

    private JLabel title;
    private JPanel titlePanel;
    private JTable table;
    private TransactionTableModel tableModel;
    private TableRowSorter<TransactionTableModel> sorter;
    private JPanel buttonPanel;
    private JButton addBtn;
    private JButton updateBtn;
    private JButton deleteBtn;
    private FormListener saveFormListener;
    private FormListener deleteFormListener;
    private JComboBox timeCombo;
    private JComboBox exportCombo;
    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);
    private final String[] TIME_COMBO_OPTIONS = {"all", "past 1 year",
        "past 6 months", "past 3 months", "past 2 months", "this month"};
    private final String[] EXPORT_COMBO_OPTIONS = {"Export", "csv", "pdf"};

    /**
     * Constructs a SummaryTablePanel with specified
     * <code>AssetController</code>, <code>PeriodController</code>,
     * <code>TransactionController</code> and <code>AssetType</code> objects.
     *
     * @param assetController An <code>AssetController</code> object to create a
     * connection with the <code>AssetRepository</code>.
     * @param perController A <code>PeriodController</code> object to create a
     * connection with the <code>PeriodRepository</code>.
     * @param transController A <code>TransactionController</code> object to
     * create a connection with the <code>TransactionRepository</code>.
     * @param assetType The <code>AssetType</code>s enum that defines the type
     * of assets.
     */
    public TransactionTablePanel(AssetController assetController,
            PeriodController perController,
            TransactionController transController,
            AssetType assetType) {
        this.assetController = assetController;
        this.perController = perController;
        this.transController = transController;
        this.assetType = assetType;

        List<Transaction> trans = null;
        try {
            if (assetType.equals(assetType.PRODUCT)) {
                trans = transController.findAllIncome();
            } else {
                trans = transController.findAllExpense();
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        DECIMAL_FORMAT.applyPattern("##0.00");
        setLayout(new BorderLayout());
        setUpComponents(trans);
    }

    private void newFilter(YearMonth startDate) {
        RowFilter<TransactionTableModel, Integer> dateFilter = new RowFilter<TransactionTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends TransactionTableModel, ? extends Integer> entry) {
                TransactionTableModel tableModel = entry.getModel();
                int i = entry.getIdentifier();
                YearMonth ym = (YearMonth) tableModel.getValueAt(i, 1);
                if (ym.isAfter(startDate)) {
                    return true;
                }
                return false;
            }
        };
        sorter.setRowFilter(dateFilter);
    }

    private void noFilter() {
        RowFilter<TransactionTableModel, Integer> dateFilter = new RowFilter<TransactionTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends TransactionTableModel, ? extends Integer> entry) {
                return true;
            }
        };
        sorter.setRowFilter(dateFilter);
    }

    private void setUpComponents(List<Transaction> trans) {
        title = new JLabel("");
        title.setFont(new Font("Lucida Sans Unicode", 0, 22));
        title.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        timeCombo = new JComboBox(TIME_COMBO_OPTIONS);
        timeCombo.setSelectedIndex(5);
        timeCombo.addItemListener(this);

        exportCombo = new JComboBox(EXPORT_COMBO_OPTIONS);
        exportCombo.setSelectedIndex(0);
        exportCombo.addItemListener(this);

        tableModel = new TransactionTableModel(trans,
                assetController, perController);
        sorter = new TableRowSorter<>(tableModel);
        table = new JTable(tableModel);
        table.setRowSorter(sorter);
        newFilter(YearMonth.now().minusMonths(1));
        table.getColumnModel().getColumn(0).setMaxWidth(50);
        table.getColumnModel().getColumn(2).setMinWidth(120);

        JScrollPane scrollpane = new JScrollPane(table);
        scrollpane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        addBtn = new JButton("Insert");
        addBtn.setFont(new Font("Lucida Sans Unicode", 0, 14));
        updateBtn = new JButton("Update");
        updateBtn.setFont(new Font("Lucida Sans Unicode", 0, 14));
        deleteBtn = new JButton("Delete");
        deleteBtn.setFont(new Font("Lucida Sans Unicode", 0, 14));

        addActionListenerToBtns();

        setUpButtonPanel();
        setUpTitlePanel();

        add(titlePanel, BorderLayout.NORTH);
        add(scrollpane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

    }

    private void setUpButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 20));
        Border emptyBorder = BorderFactory.createEmptyBorder(0, 0, 0, 20);
        Border outerBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, emptyBorder));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
    }

    private void setUpTitlePanel() {
        titlePanel = new JPanel(new GridBagLayout());
        titlePanel.add(title);
        titlePanel.add(timeCombo);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.CENTER;
        gc.insets = new Insets(10, 0, 0, 0);
        titlePanel.add(title, gc);

        JPanel panel = new JPanel(new FlowLayout());
        panel.add(timeCombo);
        panel.add(exportCombo);

        gc.gridy = 1;
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.insets = new Insets(30, 13, 0, 0);
        titlePanel.add(panel, gc);
    }

    /**
     * Invokes the updateTransactions method of the
     * <code>TransactionTableModel</code>.
     *
     * @param trans A List of <code>Transaction</code> object to refresh the
     * table with.
     */
    public void refresh(List<Transaction> trans) {
        tableModel.updateTransactions(trans);
    }

    /**
     * Invokes the deleteFromModel method of the
     * <code>TransactionTableModel</code>.
     *
     * @param transaction An <code>Transaction</code> object to delete.
     */
    private void deleteFromTable(Transaction transaction) {
        tableModel.deleteFromModel(transaction);
    }

    /**
     * Invokes the addTransaction method of the
     * <code>TransactionTableModel</code>.
     *
     * @param transaction The <code>Transaction</code> object to insert to the
     * table.
     */
    public void insertToTable(Transaction transaction) {
        tableModel.addTransaction(transaction);
    }

    /**
     * Sets the title label of the panel.
     *
     * @param title A String containing the title label of the panel.
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * Sets the saveFormListener member of the class.
     *
     * @param saveFormListener A FormListener to set the saveFormListener member
     * of the class.
     */
    public void setSaveFormListener(FormListener saveFormListener) {
        this.saveFormListener = saveFormListener;
    }

    /**
     * Sets the deleteFormListener member of the class.
     *
     * @param deleteFormListener A FormListener to set the deleteFormListener
     * member of the class.
     */
    public void setDeleteFormListener(FormListener deleteFormListener) {
        this.deleteFormListener = deleteFormListener;
    }

    private void addActionListenerToBtns() {
        addBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FormEvent formEvent = new FormEvent(this);
                if (saveFormListener != null) {
                    saveFormListener.formEventOccured(formEvent);
                }
            }
        });

        updateBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) {
                    showWarning();
                } else {
                    FormEvent event = getSelectedRowValues(row);
                    if (saveFormListener != null) {
                        saveFormListener.formEventOccured(event);
                    }
                }
            }
        });

        deleteBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int row = table.getSelectedRow();
                if (row < 0) {
                    showWarning();
                } else {
                    int deleteConfirm = JOptionPane.showConfirmDialog(getRootPane(),
                            "Are you sure you want to delete this item?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION);
                    if (deleteConfirm == JOptionPane.OK_OPTION) {
                        Integer transId = (Integer) table.getValueAt(row, 0);
                        Asset asset = new Asset();
                        try {
                            Transaction trans = transController.findById(transId);
                            boolean success = transController.delete(transId);
                            if (success) {
                                deleteFromTable(trans);
                                Integer assetId = trans.getAssetId();
                                asset = assetController.findById(assetId);
                                asset.setId(assetId);
                                asset.setStock(asset.getStock() + trans.getAmount());
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(TransactionTablePanel.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        FormEvent event = new FormEvent(this);
                        event.setAsset(asset);
                        if (deleteFormListener != null) {
                            deleteFormListener.formEventOccured(event);
                        }
                    }
                }
            }
        });
    }

    private void showWarning() {
        JOptionPane.showMessageDialog(getRootPane(),
                "No row selected!",
                "Error!", JOptionPane.WARNING_MESSAGE);
    }

    private FormEvent getSelectedRowValues(int row) {
        FormEvent formEvent = new FormEvent(this);

        Integer transId = (Integer) table.getModel().getValueAt(row, 0);
        YearMonth date = (YearMonth) table.getModel().getValueAt(row, 1);
        String assetNameAndId = (String) table.getModel().getValueAt(row, 2);
        int idStart = assetNameAndId.indexOf("(") + 1;
        String assetName = assetNameAndId.substring(0, idStart - 1);
        Integer assetId = Integer.parseInt(assetNameAndId.substring(idStart,
                assetNameAndId.length() - 1));

        String sep = DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator() + "";
        String priceStr = (String) table.getModel().getValueAt(row, 3);
        priceStr = priceStr.replace(sep, "");

        int price = Integer.parseInt(priceStr);
        int amount = (int) table.getModel().getValueAt(row, 4);

        Asset tempAsset = new Asset();
        tempAsset.setId(assetId);
        tempAsset.setName(assetName);

        Period tempPeriod = new Period(date);
        Transaction tempTransaction = new Transaction(amount, price);
        tempTransaction.setId(transId);

        formEvent.setAsset(tempAsset);
        formEvent.setPeriod(tempPeriod);
        formEvent.setTransaction(tempTransaction);

        return formEvent;
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
                startDate = now.minusYears(1).minusMonths(1);
                newFilter(startDate);
                break;
            case 2:
                startDate = now.minusMonths(7);
                newFilter(startDate);
                break;
            case 3:
                startDate = now.minusMonths(4);
                newFilter(startDate);
                break;
            case 4:
                startDate = now.minusMonths(3);
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

    private void doExportComboEvent() {
        String choosen = (String) exportCombo.getSelectedItem();
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
                    file = new File(path + extension);
                }
            }
        }
        return file;
    }
}
