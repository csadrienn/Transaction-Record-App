package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.AssetType;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

/**
 * Contains a JTable to display information about assets and a form allow the
 * user to add, delete or edit the assets. Implements the
 * <code>ActionListener</code> interface actionPerformed method.
 *
 * @author Adrienn Csontak
 */
public class AssetPanel extends JPanel implements ActionListener {

    private JPanel tablePanel;
    private JPanel formPanel;
    private JTable assetTable;
    JScrollPane tableScrollPane;
    private AssetTableModel tableModel;
    private TableRowSorter<AssetTableModel> sorter;
    private ButtonGroup radioGroup;
    private JRadioButton inStockRadio;
    private JRadioButton outOfStockRadio;
    private JRadioButton allRadio;

    private AssetController assetController;
    private TransactionController transactionController;
    private List<Asset> assets;
    private AssetType type;

    AssetFormPanel assetFormPanel;

    private JPanel buttonSwitchPanel;
    private JPanel optionsButtonPanel;
    private JPanel saveChangeButtonPanel;
    private JButton showAddFormBtn;
    private JButton addToStockBtn;
    private JButton deleteAssetBtn;
    private JButton editAssetBtn;
    private JButton saveAssetBtn;
    private JButton cancelBtn;

    private Locale locale = new Locale("en", "UK");
    private DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
    Integer selectedIdToUpdate;

    /**
     * Constructs an AssetPanel with the specified <code>AssetController</code>,
     * <code>TransactionController</code> and <code>AssetType</code> objects.
     *
     * @param assetController An <code>AssetController</code> object to create a
     * connection with the <code>AssetRepository</code>.
     * @param transactionController A <code>TransactionController</code> object
     * to create a connection with the <code>TransactionRepository</code>.
     * @param type The <code>AssetType</code>s enum that defines the type of
     * assets.
     */
    public AssetPanel(AssetController assetController,
            TransactionController transactionController, AssetType type) {
        this.assetController = assetController;
        this.transactionController = transactionController;
        this.type = type;
        selectedIdToUpdate = -1;
        decimalFormat.applyPattern("##0.00");
        formPanel = new JPanel();
        Border border = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border lineB = BorderFactory.createLineBorder(Color.black);
        formPanel.setBorder(BorderFactory.createCompoundBorder(border, lineB));

        tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanelComponentSetUp();
        formPanelLayout();
        tablePanelComponentSetUp();

        setLayout(new BorderLayout());
        add(tablePanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

    }

    private void formPanelComponentSetUp() {
        buttonSwitchPanel = new JPanel(new CardLayout());
        optionsButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        saveChangeButtonPanel = new JPanel();

        assetFormPanel = new AssetFormPanel(type, false);

        addToStockBtn = new JButton("Add to Stock");
        addToStockBtn.setFont(new Font("Lucida Sans Unicode", 0, 12));
        addToStockBtn.addActionListener(this);

        deleteAssetBtn = new JButton("Delete");
        deleteAssetBtn.setFont(new Font("Lucida Sans Unicode", 0, 12));
        deleteAssetBtn.addActionListener(this);

        editAssetBtn = new JButton("Edit");
        editAssetBtn.setFont(new Font("Lucida Sans Unicode", 0, 12));
        editAssetBtn.addActionListener(this);

        saveAssetBtn = new JButton("Save");
        saveAssetBtn.setFont(new Font("Lucida Sans Unicode", 0, 12));
        saveAssetBtn.addActionListener(this);

        cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Lucida Sans Unicode", 0, 12));
        cancelBtn.addActionListener(this);

        setFormButtonsEnabled(false);
        setSaveAndCancelBtnEnabled(false);

        optionsButtonPanel.add(addToStockBtn);
        optionsButtonPanel.add(deleteAssetBtn);
        optionsButtonPanel.add(editAssetBtn);

        saveChangeButtonPanel.add(saveAssetBtn);
        saveChangeButtonPanel.add(cancelBtn);
        buttonSwitchPanel.add("options", optionsButtonPanel);
        buttonSwitchPanel.add("save", saveChangeButtonPanel);

    }

    private void formPanelLayout() {
        formPanel.setLayout(new BorderLayout());
        formPanel.add(assetFormPanel, BorderLayout.NORTH);
        formPanel.add(buttonSwitchPanel, BorderLayout.CENTER);
    }

    private void tablePanelComponentSetUp() {
        assets = null;
        try {
            if (type.equals(type.PRODUCT)) {
                assets = assetController.findAllProducts();
                showAddFormBtn = new JButton("Add new Product");
            } else {
                assets = assetController.findAllEquipment();
                showAddFormBtn = new JButton("Add new Equipment");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        showAddFormBtn.setFont(new Font("Lucida Sans Unicode", 0, 12));
        showAddFormBtn.addActionListener(this);

        radioGroup = new ButtonGroup();
        inStockRadio = new JRadioButton("show in Stock");
        inStockRadio.setFont(new Font("Lucida Sans Unicode", 0, 10));
        inStockRadio.addActionListener(this);
        outOfStockRadio = new JRadioButton("show out of Stock");
        outOfStockRadio.setFont(new Font("Lucida Sans Unicode", 0, 10));
        outOfStockRadio.addActionListener(this);
        allRadio = new JRadioButton("show all", true);
        allRadio.setFont(new Font("Lucida Sans Unicode", 0, 10));
        allRadio.addActionListener(this);
        radioGroup.add(inStockRadio);
        radioGroup.add(outOfStockRadio);
        radioGroup.add(allRadio);

        tableModel = new AssetTableModel(assets);
        sorter = new TableRowSorter<>(tableModel);
        assetTable = new JTable(tableModel);
        assetTable.setRowSorter(sorter);
        newFilter(0);
        assetTable.getColumnModel().getColumn(0).setMaxWidth(30);
        assetTable.getColumnModel().getColumn(2).setMaxWidth(50);
        tableScrollPane = new JScrollPane(assetTable);

        assetTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    if (assetTable.getSelectedRow() != -1 && !saveAssetBtn.isEnabled()) {
                        showSelectedDetails();
                    }
                }
            }
        });

        tablePanelLayout();

    }

    private void tablePanelLayout() {
        tablePanel.setLayout(new BorderLayout());
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel showAddFormBtnPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.FIRST_LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 15, 5);
        showAddFormBtnPanel.add(showAddFormBtn, gc);
        JPanel radioGroupPanel = new JPanel();
        radioGroupPanel.setLayout(new BoxLayout(radioGroupPanel, BoxLayout.X_AXIS));
        radioGroupPanel.add(allRadio);
        radioGroupPanel.add(inStockRadio);
        radioGroupPanel.add(outOfStockRadio);
        tablePanel.add(showAddFormBtnPanel, BorderLayout.SOUTH);
        tablePanel.add(radioGroupPanel, BorderLayout.NORTH);
    }

    private void newFilter(int radioOption) {
        RowFilter<AssetTableModel, Integer> dateFilter = new RowFilter<AssetTableModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends AssetTableModel, ? extends Integer> entry) {
                AssetTableModel tableModel = entry.getModel();
                int i = entry.getIdentifier();
                int stock = (int) tableModel.getValueAt(i, 2);
                if (radioOption == 0) {
                    return true;
                } else if (radioOption == 1) {
                    if (stock > 0) {
                        return true;
                    }
                } else {
                    if (stock == 0) {
                        return true;
                    }
                }
                return false;
            }
        };
        sorter.setRowFilter(dateFilter);
    }

    private void showSelectedDetails() {
        setFormButtonsEnabled(true);
        int row = assetTable.getSelectedRow();
        Integer selectedId = (Integer) assetTable.getValueAt(row, 0);
        Asset asset = null;
        try {
            asset = assetController.findById(selectedId);

        } catch (SQLException ex) {
            Logger.getLogger(AssetPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
        assetFormPanel.setNameFieldText(asset.getName());
        assetFormPanel.setDescAreaText(asset.getFeature());
        assetFormPanel.setStockFieldText(asset.getStock() + "");
        double price = asset.getPlannedPrice() / 100.0;
        assetFormPanel.setPriceFieldText(decimalFormat.format(price));
    }

    private void showSaveForm(boolean empty) {
        CardLayout cl = (CardLayout) buttonSwitchPanel.getLayout();
        cl.show(buttonSwitchPanel, "save");

        if (empty) {
            assetFormPanel.clearTextFields();
        } else {
            selectedIdToUpdate = (Integer) assetTable.getValueAt(assetTable.getSelectedRow(), 0);
        }
        assetTable.clearSelection();
        setFormButtonsEnabled(false);
        setSaveAndCancelBtnEnabled(true);
        assetFormPanel.setTextFieldsEditable(true);

    }

    private void doCancelBtnAction() {
        CardLayout cl = (CardLayout) buttonSwitchPanel.getLayout();
        cl.show(buttonSwitchPanel, "options");
        assetFormPanel.clearTextFields();
        setFormButtonsEnabled(false);
        setSaveAndCancelBtnEnabled(false);
        assetTable.clearSelection();
        assetFormPanel.setTextFieldsEditable(false);
    }

    private void doSaveAssetBtnAction() {
        if (selectedIdToUpdate.equals(-1)) {
            Asset asset = new Asset();
            asset = saveAsset(asset.getId());

            if (asset != null) {
                insertToTable(asset);
                backToOptions();
            }
        } else {
            try {
                Asset assetToUpdate = assetController.findById(selectedIdToUpdate);
                assetToUpdate = saveAsset(assetToUpdate.getId());
                if (assetToUpdate != null) {
                    assetController.save(assetToUpdate);
                    if (type.equals(AssetType.PRODUCT)) {
                        assets = assetController.findAllProducts();
                    } else {
                        assets = assetController.findAllEquipment();
                    }
                    updateAssetsInModel(assets);
                    backToOptions();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AssetPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void backToOptions() {
        CardLayout cl = (CardLayout) buttonSwitchPanel.getLayout();
        cl.show(buttonSwitchPanel, "options");
        setSaveAndCancelBtnEnabled(false);
        assetFormPanel.setTextFieldsEditable(false);
        assetFormPanel.clearTextFields();
    }

    private Asset saveAsset(Integer assetId) {
        boolean save = assetFormPanel.checkInput();
        Asset asset = null;
        if (save) {
            asset = new Asset();
            String name = assetFormPanel.getNameFieldText();
            String description = assetFormPanel.getDescAreaText();
            String stockText = assetFormPanel.getStockFieldText();
            if (assetId != null) {
                asset.setId(assetId);
            }
            String sep = decimalFormat.getDecimalFormatSymbols().getDecimalSeparator() + "";
            String priceText = assetFormPanel.getPriceFieldText();
            priceText = priceText.replace(sep, "");

            int stock = Integer.parseInt(stockText);
            int price = Integer.parseInt(priceText);

            asset.setName(name);
            asset.setFeature(description);
            asset.setType(type);
            asset.setStock(stock);
            asset.setPlannedPrice(price);
            try {
                assetController.save(asset);
                if (asset.getId() == null) {
                    Integer id = assetController.findLatest();
                    asset.setId(id);
                }
            } catch (SQLException ex) {
                Logger.getLogger(AssetPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return asset;
    }

    private void doAddToStockBtnAction() {
        JPanel panel = new JPanel();
        JTextField numberField = new JTextField(6);
        String response = JOptionPane.showInputDialog(getRootPane(), "Enter the number of items: ");
        int num = 0;
        String message = null;
        if (response != null) {
            if (response.matches("^[0-9]+$")) {
                num = Integer.parseInt(response);
                if (num >= 0) {
                    int row = assetTable.getSelectedRow();
                    Integer id = (Integer) assetTable.getValueAt(row, 0);
                    Asset asset;
                    try {
                        asset = assetController.findById(id);
                        int increasedStock = asset.getStock() + num;
                        asset.setStock(increasedStock);
                        assetController.save(asset);
                        List<Asset> refreshedAssets;
                        if (type.equals(AssetType.PRODUCT)) {
                            refreshedAssets = assetController.findAllProducts();
                        } else {
                            refreshedAssets = assetController.findAllEquipment();
                        }
                        updateAssetsInModel(refreshedAssets);
                        assetTable.setRowSelectionInterval(row, row);

                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }

                } else {
                    popErrorDialog("Saving failed. Please enter a pozitive number!");
                }
            } else {
                popErrorDialog("Saving failed. Please enter a valid number!");
            }
        }
    }

    private void doDeleteAssetBtnAction() {
        int row = assetTable.getSelectedRow();
        Integer id = (Integer) assetTable.getValueAt(row, 0);
        int deleteConfirm = JOptionPane.showConfirmDialog(getRootPane(),
                "Are you sure you want to delete this item?", "Confirm delete", JOptionPane.OK_CANCEL_OPTION);
        if (deleteConfirm == JOptionPane.OK_OPTION) {
            try {
                Asset asset = assetController.findById(id);
                List<Transaction> trans = transactionController.findByAssetId(asset.getId());
                if (trans.isEmpty()) {
                    boolean success = assetController.delete(asset);
                    if (success) {
                        tableModel.deleteAsset(asset);
                        assetFormPanel.clearTextFields();
                        assetTable.clearSelection();
                    }
                } else {
                    popErrorDialog("Not able to delete."
                            + "\nThere is saved transaction with this asset.");
                }

            } catch (SQLException ex) {
                Logger.getLogger(AssetPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void setFormButtonsEnabled(boolean enabled) {
        addToStockBtn.setEnabled(enabled);
        deleteAssetBtn.setEnabled(enabled);
        editAssetBtn.setEnabled(enabled);

        String toolTip = null;
        if (!enabled) {
            toolTip = "Select a row to use.";

        }
        addToStockBtn.setToolTipText(toolTip);
        deleteAssetBtn.setToolTipText(toolTip);
        editAssetBtn.setToolTipText(toolTip);
    }

    private void setSaveAndCancelBtnEnabled(boolean enabled) {
        saveAssetBtn.setEnabled(enabled);
        cancelBtn.setEnabled(enabled);
    }

    private void popErrorDialog(String message) {
        JOptionPane.showMessageDialog(getRootPane(),
                message,
                "Error", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == showAddFormBtn) {
            showSaveForm(true);
        }
        if (e.getSource() == cancelBtn) {
            doCancelBtnAction();
        }
        if (e.getSource() == saveAssetBtn) {
            doSaveAssetBtnAction();
        }
        if (e.getSource() == addToStockBtn) {
            doAddToStockBtnAction();
        }
        if (e.getSource() == deleteAssetBtn) {
            doDeleteAssetBtnAction();
        }
        if (e.getSource() == editAssetBtn) {
            showSaveForm(false);
        }
        if (e.getSource() == allRadio) {
            newFilter(0);
        }
        if (e.getSource() == inStockRadio) {
            newFilter(1);
        }
        if (e.getSource() == outOfStockRadio) {
            newFilter(2);
        }

    }

    /**
     * Invokes the <code>AssetTableModel</code> insertToTable method.
     *
     * @param asset An <code>Asset</code> object to add to the asset list of the
     * table model.
     */
    public void insertToTable(Asset asset) {
        tableModel.addAsset(asset);
    }

    /**
     * Invokes the <code>AssetTableModel</code> updateAssets method.
     *
     * @param assets A List of <code>Asset</code> objects the refresh the list
     * of the table model with.
     */
    public void updateAssetsInModel(List<Asset> assets) {
        tableModel.updateAssets(assets);
    }

}
