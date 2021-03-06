package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.AssetType;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Creates a form for the user to save a new transaction or update an existing
 * one. Also creates a form in a dialog to save a new Product if it is needed.
 *
 * @author Adrienn Csontak
 */
public class TransactionFormPanel extends JPanel implements ItemListener {

    private final String SELECTED_NEW_ASSET_OPTION = "Add new item";
    private JLabel titleLabel;
    private JPanel formPanel;
    private JLabel assetLabel;
    private JComboBox assetCombo;
    private JLabel dateLabel;
    private JComboBox dateCombo;
    private JLabel priceLabel;
    private JTextField priceTextField;
    private JLabel currencyLabel;
    private JLabel amountLabel;
    private JTextField amountTextField;
    private JLabel successLabel;
    private JButton saveButton;
    private JButton leaveButton;
    private final String PATTERN = "yyyy/MM";
    private final DateTimeFormatter DF;
    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);
    private String SEP = DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator() + "";

    private FormListener transactionFormListener;
    private Transaction oldTrans;
    private AssetType assetType;
    private List<Period> periodList;
    private List<Asset> assetList;

    //dialog
    private AssetFormPanel assetFormPanel;
    private JDialog newAssetDialog;
    private JButton okBtn;
    private JButton cancelBtn;
    private JPanel buttonPanel;
    private Asset assetToSave;

    /**
     * Construct a TransactionFormPanel with a specified assetType that define
     * the transactions displayed in the table.
     *
     * @param assetType The <code>AssetType</code> enum that define the type of
     * assets.
     */
    public TransactionFormPanel(AssetType assetType) {
        DECIMAL_FORMAT.applyPattern("##0.00");
        this.assetType = assetType;
        assetFormPanel = new AssetFormPanel(assetType, true);

        periodList = new ArrayList<>();
        assetList = new ArrayList<>();
        DF = DateTimeFormatter.ofPattern(PATTERN);
        formPanel = new JPanel();
        titleLabel = new JLabel("Transaction form");
        assetLabel = new JLabel("Product:");
        dateLabel = new JLabel("Date:");
        priceLabel = new JLabel("Price:");
        priceTextField = new JTextField(10);
        priceTextField.setText("0" + SEP + "00");
        currencyLabel = new JLabel("£");
        amountLabel = new JLabel("Amount:");
        amountTextField = new JTextField(10);
        successLabel = new JLabel("Saving succesful");
        successLabel.setVisible(false);
        saveButton = new JButton("Save");
        leaveButton = new JButton("Cancel");
        oldTrans = null;

        dateCombo = new JComboBox(new String[0]);
        dateCombo.setPrototypeDisplayValue("0000/00");
        assetCombo = new JComboBox(new String[0]);
        assetCombo.setPrototypeDisplayValue("id) very long name here: 000 pc");
        assetCombo.setSelectedIndex(-1);
        assetCombo.addItemListener(this);

        addActionListenersToButtons();
        layoutComponents();
        setUpHiddenDialog();

    }

    /**
     * Prepares the form for updating. Fills the form fields and sets the combo
     * boxes with the received data.
     *
     * @param asset An <code>Asset</code> containing the asset name and id to
     * set the combo box.
     * @param period A <code>Period</code> containing the year-month to set the
     * combo box.
     * @param transaction A <code>Transaction</code> containing the amount and
     * price to fill the text fields.
     */
    public void setUpForUpdate(Asset asset, Period period, Transaction transaction) {
        oldTrans = transaction;
        assetCombo.setSelectedItem(asset.getId() + ") " + asset.getName() + ": " + asset.getStock() + " pc");
        assetCombo.setToolTipText("The equipment can not be changed.");
        if (assetType.equals(AssetType.PRODUCT)) {
            assetCombo.setToolTipText("The product can not be changed.");
        }
        dateCombo.setSelectedItem(DF.format(period.getDate()));
        amountTextField.setText(transaction.getAmount() + "");
        amountTextField.setToolTipText("The amount can not be changed.");
        double price = transaction.getPrice() / 100.0;
        priceTextField.setText(DECIMAL_FORMAT.format(price));
        resetAssetToSave();
    }

    /**
     * Prepares the form for adding new transaction. Clears the text field. Set
     * the asset combo box to the first item and the date combo box to the last
     * item.
     *
     */
    public void setUpForNew() {
        oldTrans = null;
        assetCombo.setToolTipText(null);
        assetCombo.setSelectedIndex(-1);
        int last = dateCombo.getItemCount() - 1;
        dateCombo.setSelectedIndex(last);
        amountTextField.setToolTipText(null);
        amountTextField.setText("");
        priceTextField.setText("0" + SEP + "00");
        resetAssetToSave();
    }

    /**
     * Sets the transactionFormListener class member.
     *
     * @param transactionFormListener A <code>FormListener</code> to set the
     * transactionFormListener member of the class.
     */
    public void setTransactionFormListener(FormListener transactionFormListener) {
        this.transactionFormListener = transactionFormListener;
    }

    /**
     * Sets the title label of the form. Fills the combo boxes with data to
     * create the items.
     *
     * @param periods A List of <code>Period</code> objects to use the
     * year-month values as items.
     * @param assets A List of <code>Asset</code> objects to use the name and
     * the id values as items.
     */
    public void setUpComboBoxesAndTitle(List<Period> periods, List<Asset> assets) {

        if (this.assetType.equals(AssetType.PRODUCT)) {
            titleLabel.setText("Income form");
        }
        if (this.assetType.equals(AssetType.EQUIPMENT)) {
            titleLabel.setText("Expense form");
        }
        setPeriodList(periods);
        setAssetList(assets);
        refreshComboBoxes();
    }

    private void setPeriodList(List<Period> periods) {
        periodList = periods;
    }

    private void setAssetList(List<Asset> assets) {
        assetList = new ArrayList<>();
        if (assetType.equals(AssetType.PRODUCT)) {
            for (Asset asset : assets) {
                if (asset.getType().equals(AssetType.PRODUCT)) {
                    assetList.add(asset);
                }
            }
        } else {
            for (Asset asset : assets) {
                if (asset.getType().equals(AssetType.EQUIPMENT)) {
                    assetList.add(asset);
                }
            }
        }
    }

    private void refreshComboBoxes() {
        if (periodList.size() != dateCombo.getItemCount()) {
            dateCombo.removeAllItems();
            for (Period period : periodList) {
                dateCombo.addItem(DF.format(period.getDate()));
            }
            dateCombo.setSelectedIndex(dateCombo.getItemCount() - 1);
        }
        assetCombo.removeAllItems();
        if (!assetList.isEmpty()) {
            for (Asset asset : assetList) {
                String item = asset.getId() + ") " + asset.getName() + ": " + asset.getStock() + " pc";
                assetCombo.addItem(item);
            }
            assetCombo.addItem(SELECTED_NEW_ASSET_OPTION);
            assetCombo.setSelectedIndex(-1);
        } else {
            assetCombo.addItem("");
            assetCombo.addItem(SELECTED_NEW_ASSET_OPTION);
            assetCombo.setSelectedIndex(-1);
            assetCombo.removeItemAt(0);
        }

    }

    private void resetAssetToSave() {
        assetToSave = null;
    }

    private void addActionListenersToButtons() {
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                doSaveButtonAction();
            }
        });

        leaveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FormEvent event = new FormEvent(e);
                if (transactionFormListener != null) {
                    transactionFormListener.formEventOccured(event);
                }
            }
        });
    }

    private void doSaveButtonAction() {
        FormEvent event = new FormEvent(this);

        YearMonth date = YearMonth.parse((String) dateCombo.getSelectedItem(), DF);
        Period period = periodList.stream()
                .filter(p -> p.getDate().equals(date))
                .findAny()
                .orElse(null);

        Asset asset = null;
        boolean assetIsSelected = checkAssetSelection(assetCombo.getSelectedIndex());
        String amountText = amountTextField.getText();
        String priceText = priceTextField.getText().replace(SEP, "");
        
        if (assetIsSelected && checkNumbersValitidy(amountText) && checkNumbersValitidy(priceText)) {
            asset = chooseAssetToSave();
            if (checkNumbersValitidy(amountText) && checkNumbersValitidy(priceText)) {
                int amount = Integer.parseInt(amountText);
                int price = Integer.parseInt(priceText);
                int assetStock = asset.getStock();

                boolean enoughInStock;
                Transaction changedTrans = new Transaction();
                //if inserting a new transaction
                if (oldTrans == null) {
                    enoughInStock = checkEnoughInStock(amount, assetStock);
                    
                //if updating an existing transaction
                } else {
                    int oldAmount = oldTrans.getAmount();
                    Integer oldAssetId = oldTrans.getAssetId();
                    Asset oldAsset = assetList.stream()
                            .filter(a -> Objects.equals(a.getId(), oldAssetId))
                            .findAny()
                            .orElse(null);
                    if (asset.getId() != null && asset.getId().equals(oldAssetId)) {
                        enoughInStock = checkEnoughInStock(amount, oldAsset.getStock() + oldAmount);
                    } else {
                        enoughInStock = checkEnoughInStock(amount, assetStock);
                    }
                }
                if (enoughInStock) {
                    if(oldTrans == null){
                        asset.setStock(assetStock - amount);
                    }else{
                        changedTrans.setId(oldTrans.getId());
                    }
                    changedTrans.setAmount(amount);
                    changedTrans.setPrice(price);
                    changedTrans.setPeriodId(period.getId());

                    event.setAsset(asset);
                    event.setChangedTransaction(changedTrans);
                    event.setOldTransaction(oldTrans);
                    if (transactionFormListener != null) {
                        transactionFormListener.formEventOccured(event);
                    }
                }   
            }
        }
    }

    private boolean checkAssetSelection(int selectedNum) {
        if (selectedNum == -1) {
            if (assetType.equals(AssetType.PRODUCT)) {
                Utils.showWarningMessage(getRootPane(),"Please choose a product!");
            } else {
                Utils.showWarningMessage(getRootPane(),"Please choose an equipment!");
            }
            return false;
        }
        return true;

    }

    private boolean checkNumbersValitidy(String numText) {
        if (!numText.matches("^[-]?[0-9]+$")) {
            Utils.showWarningMessage(getRootPane(),"Please enter a valid number!");
            return false;
        }
        if (numText.startsWith("-")) {
            Utils.showWarningMessage(getRootPane(),"Please enter a positive number!");
            return false;
        }
        return true;
    }

    private boolean checkEnoughInStock(int amount, int stock) {
        if (amount > stock) {
            Utils.showWarningMessage(getRootPane(),"The amount (" + amount + ") can't be more than the "
                    + "number of assets in stock (" + stock + ")");
            return false;
        }
        return true;
    }

    private Asset chooseAssetToSave() {
        String selectedAssetText = (String) assetCombo.getSelectedItem();
        int selectedNum = assetCombo.getSelectedIndex();
        int comboSize = assetCombo.getItemCount();
        if (selectedNum == comboSize - 1) {
            return assetToSave;

        } else {
            int assetIdEnd = selectedAssetText.indexOf(')');
            int assetId = Integer.parseInt(selectedAssetText.substring(0, assetIdEnd));
            Asset asset = assetList.stream()
                    .filter(a -> a.getId() == assetId)
                    .findAny()
                    .orElse(null);
            return asset;
        }
    }

    private void layoutComponents() {
        titleLabel.setFont(new Font("Lucida Sans Unicode", 0, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        Border innerBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        Border outerBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        formPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        setFonts();
        setFormPanelLayout();

        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    private void setFonts() {
        Font lucida14 = new Font("Lucida Sans Unicode", 0, 14);
        assetLabel.setFont(lucida14);
        assetLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        dateLabel.setFont(lucida14);
        dateLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        priceLabel.setFont(lucida14);
        priceLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        currencyLabel.setFont(lucida14);
        currencyLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        amountLabel.setFont(lucida14);
        amountLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        successLabel.setFont(new Font("Lucida Sans Unicode", 0, 12));
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);

        assetCombo.setFont(lucida14);
        dateCombo.setFont(lucida14);

        Dimension d = new Dimension(80, 35);
        saveButton.setFont(lucida14);
        saveButton.setPreferredSize(d);
        leaveButton.setFont(lucida14);
        leaveButton.setPreferredSize(d);
    }

    private void setFormPanelLayout() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        formPanel.setLayout(gridBagLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        Insets firstColumnInsets = new Insets(0, 0, 15, 30);
        Insets secondColumnInsets = new Insets(0, 0, 15, 0);
        gbc.gridy = 0;

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx = 0;
        gbc.insets = firstColumnInsets;
        formPanel.add(assetLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = secondColumnInsets;
        formPanel.add(assetCombo, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = firstColumnInsets;
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = secondColumnInsets;
        formPanel.add(dateCombo, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = firstColumnInsets;
        formPanel.add(priceLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, -15, 15, 0);
        formPanel.add(currencyLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = secondColumnInsets;
        formPanel.add(priceTextField, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = firstColumnInsets;
        formPanel.add(amountLabel, gbc);

        gbc.gridx = 1;
        gbc.insets = secondColumnInsets;
        formPanel.add(amountTextField, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.LINE_START;
        formPanel.add(successLabel, gbc);

        //next row
        gbc.gridy++;
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        formPanel.add(saveButton, gbc);

        gbc.insets = new Insets(0, 0, 15, 30);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.LINE_END;
        formPanel.add(leaveButton, gbc);
    }

    private void setUpHiddenDialog() {
        newAssetDialog = new JDialog();
        newAssetDialog.setLocationRelativeTo(null);
        newAssetDialog.setTitle("New asset form");
        newAssetDialog.setSize(550, 300);

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        okBtn = new JButton("OK");
        cancelBtn = new JButton("Cancel");
        setUpDialogBtns();

        buttonPanel.add(okBtn);
        buttonPanel.add(cancelBtn);
        setDialogLayout();

    }

    private void setDialogLayout() {
        newAssetDialog.setLayout(new BorderLayout());
        newAssetDialog.add(assetFormPanel, BorderLayout.CENTER);
        newAssetDialog.add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setUpDialogBtns() {
        okBtn.setFont(new Font("Lucida Sans Unicode", 0, 14));
        okBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                boolean save = assetFormPanel.checkInput();
                if (save) {
                    String name = assetFormPanel.getNameFieldText();
                    String description = assetFormPanel.getDescAreaText();
                    String stockText = assetFormPanel.getStockFieldText();
                    int stock = Integer.parseInt(stockText);
                    String priceText = assetFormPanel.getPriceFieldText();
                    String sep = DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator() + "";
                    priceText = priceText.replace(sep, "");
                    int price = Integer.parseInt(priceText);
                    assetToSave = new Asset(name, description, assetType);
                    assetToSave.setStock(stock);
                    assetToSave.setMaterialCost(price);

                    assetCombo.removeItem(SELECTED_NEW_ASSET_OPTION);
                    String createdItem = "New product: " + assetToSave.getName()
                            + ": " + assetToSave.getStock() + " pc";
                    assetCombo.addItem(createdItem);
                    assetCombo.setSelectedItem(createdItem);
                    closeAndClearDialog();
                }
            }
        });
        cancelBtn.setFont(new Font("Lucida Sans Unicode", 0, 14));
        cancelBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                closeAndClearDialog();
            }
        });
    }

    private void closeAndClearDialog() {
        newAssetDialog.setVisible(false);
        assetFormPanel.clearTextFields();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            if (e.getSource() == assetCombo) {
                String selected = (String) assetCombo.getSelectedItem();
                if (selected.equals(SELECTED_NEW_ASSET_OPTION)) {
                    newAssetDialog.setVisible(true);
                }
            }
        }

    }
}
