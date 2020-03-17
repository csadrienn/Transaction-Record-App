package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.Cost;
import com.csontaka.transaction_record_app.entity.Fee;
import com.csontaka.transaction_record_app.entity.Material;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * Displays a form panel for calculating a price for a product based on the the
 * list of material costs,fees and the profit given by the user. The user also
 * can save the material cost list to a chosen product.
 *
 * @author Adrienn Csontak
 */
public class PriceCalculatorPanel extends JPanel {

    private JPanel titlePanel;
    private JLabel title;
    private JPanel formPanel;
    private JPanel listPanel;
    private JPanel resultPanel;
    private JList materialList;
    private CalculatorListModel materialListModel;
    private JList feeList;
    private CalculatorListModel feeListModel;
    private JLabel materialCostTextLb;
    private JLabel materialCostLb;
    private JButton saveToProductBtn;
    private PriceFormPanel materialFormPanel;
    private PriceFormPanel feeFormPanel;
    private JButton calculateBtn;
    private JLabel profitLb;
    private JTextField profitField;
    private JComboBox<String> profitCombo;
    private JLabel priceLb;
    private JLabel finalProfitLb;
    private FormListener formListener;
    private JButton clearMaterialListBtn;
    private JButton clearFeeListBtn;
    private JButton clearBtn;
    private JButton removeMaterialFromListBtn;
    private JButton removeFeeFromListBtn;
    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);
    private final String SEP;
    private int totalMaterialCost;
    private AssetController assetController;

    /**
     * Constructs a PriceCalculatorPanel object with a with specified
     * <code>AssetController</code>.
     *
     * @param assetController An <code>AssetController</code> object to create a
     * connection with the <code>AssetRepository</code>.
     */
    public PriceCalculatorPanel(AssetController assetController) {
        SEP = DECIMAL_FORMAT.getDecimalFormatSymbols().getDecimalSeparator() + "";
        this.assetController = assetController;
        DECIMAL_FORMAT.applyPattern("##0.00");
        totalMaterialCost = 0;
        initComponents();
        setLayout(new BorderLayout());
        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(listPanel, BorderLayout.EAST);
        add(resultPanel, BorderLayout.SOUTH);
        setUpButtonActions();

    }

    private void initComponents() {
        Font labelFont = new Font("Lucida Sans Unicode", 0, 14);
        titlePanelSetUp();
        formPanelSetUp(labelFont);
        listPanelSetUp(labelFont);
        resultPanelSetUp();
    }

    private void titlePanelSetUp() {
        titlePanel = new JPanel();
        title = new JLabel("Price Calculator");
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        title.setFont(new Font("Lucida Sans Unicode", 0, 24));
        titlePanel.add(title);
    }

    private void formPanelSetUp(Font labelFont) {
        formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        materialFormPanel = new PriceFormPanel("Material:", "Value (£):");
        materialFormPanel.setBorder(createSectorBorder("Materials"));
        materialFormPanel.setValueFieldText("0.00");
        feeFormPanel = new PriceFormPanel("Fee:", "Value (%):");
        feeFormPanel.setBorder(createSectorBorder("Fees"));

        JPanel profitPanel = new JPanel(new GridBagLayout());
        profitPanelSetUp(profitPanel, labelFont);
        formPanel.add(materialFormPanel);
        formPanel.add(feeFormPanel);
        formPanel.add(profitPanel);

    }

    private void profitPanelSetUp(JPanel profitPanel, Font labelFont) {

        profitPanel.setBorder(createSectorBorder("Profit"));
        profitLb = new JLabel("Profit:");
        profitLb.setFont(labelFont);
        profitLb.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
        profitField = new JTextField(10);
        String[] comboElements = {"£", "%"};
        profitCombo = new JComboBox<>(comboElements);
        profitCombo.setPrototypeDisplayValue(" % ");
        calculateBtn = new JButton("Calculate");
        Utils.setButtonLook(calculateBtn, Color.GREEN.darker(), 11);

        GridBagConstraints gc = new GridBagConstraints();
        gc.weightx = 0.0;
        gc.fill = GridBagConstraints.VERTICAL;
        Insets ins = new Insets(5, 5, 5, 5);
        gc.insets = ins;
        gc.anchor = GridBagConstraints.WEST;
        gc.gridy = 0;
        profitPanel.add(profitLb, gc);
        profitPanel.add(profitField, gc);
        gc.weightx = 1.0;
        profitPanel.add(profitCombo, gc);
        gc.weightx = 0.0;
        gc.gridy = 1;
        gc.gridx = 1;
        profitPanel.add(calculateBtn, gc);
    }

    private void listPanelSetUp(Font labelFont) {
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setPreferredSize(new Dimension(255, 400));
        Border outer = BorderFactory.createEmptyBorder(10, 0, 7, 10);
        listPanel.setBorder(BorderFactory.createCompoundBorder(outer,
                BorderFactory.createEtchedBorder()));

        materialListModel = new CalculatorListModel();
        materialList = new JList();
        materialList.setModel(materialListModel);
        feeListModel = new CalculatorListModel();
        feeList = new JList();
        feeList.setModel(feeListModel);

        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        totalPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 10, 0));
        totalPanel.setMaximumSize(new Dimension(250, 200));
        
        materialCostTextLb = new JLabel("Total:   £");
        materialCostTextLb.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        materialCostTextLb.setFont(labelFont);
        materialCostLb = new JLabel("0.00");
        materialCostLb.setFont(labelFont);
        materialCostLb.setBorder(BorderFactory.createEmptyBorder(7, 0, 0, 0));
        
        Icon saveToProductIcon = Utils.createIcon("/images/saveTo20.png");
        if(saveToProductIcon != null){
            saveToProductBtn = new JButton(saveToProductIcon);
            saveToProductBtn.setToolTipText("Save to a product.");
        }else{
            saveToProductBtn = new JButton("Save");
        }
        Utils.setButtonLook(saveToProductBtn, Color.GREEN.darker(), 11);
        
        Icon clearIcon = Utils.createIcon("/images/clear20.png");
        if(clearIcon != null){
            clearMaterialListBtn = new JButton(clearIcon);
            clearFeeListBtn = new JButton(clearIcon);
            clearMaterialListBtn.setToolTipText("Clear the list.");
            clearFeeListBtn.setToolTipText("Clear the list.");
        }else{
            clearMaterialListBtn = new JButton("Clear");
            clearFeeListBtn = new JButton("Clear");
            
        }
        Utils.setButtonLook(clearMaterialListBtn, Color.YELLOW.darker(), 11);
        Utils.setButtonLook(clearFeeListBtn, Color.YELLOW.darker(), 11);
        
        Icon removeIcon = Utils.createIcon("/images/delete20.png");
        if(removeIcon != null){
            removeMaterialFromListBtn = new JButton(removeIcon);
            removeFeeFromListBtn = new JButton(removeIcon);
            removeMaterialFromListBtn.setToolTipText("Remove it from the list.");
            removeFeeFromListBtn.setToolTipText("Remove it from the list.");
        }else{
            removeMaterialFromListBtn = new JButton("Remove");
            removeFeeFromListBtn = new JButton("Remove");
        }
        Utils.setButtonLook(removeMaterialFromListBtn, Color.RED.darker(), 11);
        Utils.setButtonLook(removeFeeFromListBtn, Color.RED.darker(), 11);

        JScrollPane materialScrollPane = new JScrollPane(materialList);
        materialScrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        JScrollPane feeScrollPane = new JScrollPane(feeList);
        feeScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
        listPanel.add(materialScrollPane);

        totalPanel.add(materialCostTextLb);
        totalPanel.add(materialCostLb);
        listPanel.add(totalPanel);
        JPanel matBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        matBtnPanel.add(saveToProductBtn);
        matBtnPanel.add(removeMaterialFromListBtn);
        matBtnPanel.add(clearMaterialListBtn);
        listPanel.add(matBtnPanel);
        listPanel.add(Box.createRigidArea(new Dimension(10, 10)));
        listPanel.add(feeScrollPane);
        JPanel feeBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        feeBtnPanel.add(removeFeeFromListBtn);
        feeBtnPanel.add(clearFeeListBtn);
        listPanel.add(Box.createRigidArea(new Dimension(5, 5)));
        listPanel.add(feeBtnPanel);
        listPanel.add(Box.createRigidArea(new Dimension(10, 10)));
    }

    private void resultPanelSetUp() {
        resultPanel = new JPanel(new GridBagLayout());
        resultPanel.setPreferredSize(new Dimension(400, 130));
        Border outer = BorderFactory.createEmptyBorder(5, 11, 10, 10);
        resultPanel.setBorder(BorderFactory.createCompoundBorder(outer,
                BorderFactory.createEtchedBorder()));
        Font resultLabelFont = new Font("Lucida Sans Unicode", Font.BOLD, 16);
        priceLb = new JLabel("");
        priceLb.setFont(resultLabelFont);
        priceLb.setForeground(Color.BLUE.darker());
        JLabel priceTextLb = new JLabel("Price:");
        priceTextLb.setFont(resultLabelFont);

        Border empty = BorderFactory.createEmptyBorder(0, 17, 0, 0);
        priceTextLb.setBorder(empty);
        finalProfitLb = new JLabel("");
        finalProfitLb.setFont(resultLabelFont);
        finalProfitLb.setForeground(Color.BLUE.darker());
        JLabel finalProfitTextLb = new JLabel("Profit:");
        finalProfitTextLb.setFont(resultLabelFont);

        finalProfitTextLb.setBorder(empty);
        Icon clearIcon = Utils.createIcon("/images/clear25.png");
        if(clearIcon != null){
            clearBtn = new JButton(clearIcon);
            clearBtn.setToolTipText("Clear the form.");
        }else{
            clearBtn = new JButton("Clear");
        }
        Utils.setButtonLook(clearBtn, Color.YELLOW.darker(), 14);
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.GRAY.brighter());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setPreferredSize(new Dimension(250, 100));
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.WEST;
        gc.gridy = 0;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.weightx = 0.0;
        panel.add(priceTextLb, gc);
        gc.weightx = 1.0;
        panel.add(priceLb, gc);
        gc.gridy = 1;
        gc.weightx = 0.0;
        panel.add(finalProfitTextLb, gc);
        gc.weightx = 1.0;
        panel.add(finalProfitLb, gc);

        gc.gridy = GridBagConstraints.RELATIVE;
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(5, 5, 5, -10);
        resultPanel.add(panel, gc);
        gc.anchor = GridBagConstraints.SOUTHWEST;
        gc.insets = new Insets(10, 20, 5, -10);
        gc.fill = GridBagConstraints.NONE;
        resultPanel.add(clearBtn, gc);
    }

    private void setUpButtonActions() {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getSource() == materialFormPanel.getAddBtn()) {
                    boolean valid = materialFormPanel.checkValidity();
                    if (valid) {
                        int price = Integer.parseInt(materialFormPanel.getValueFieldText().replace(SEP, ""));
                        Cost material = new Material(materialFormPanel.getNameFieldText(), price);
                        materialListModel.addElement(material);
                        totalMaterialCost += price;
                        double total = totalMaterialCost / 100.0;
                        materialCostLb.setText(DECIMAL_FORMAT.format(total));
                        clearMatForm();
                        setMaterialListButtonsEnabled(true);
                    }
                }
                if (e.getSource() == feeFormPanel.getAddBtn()) {
                    boolean valid = feeFormPanel.checkValidity();
                    if (valid) {
                        Cost fee = new Fee(feeFormPanel.getNameFieldText(),
                                Integer.parseInt(feeFormPanel.getValueFieldText()));
                        feeListModel.addElement(fee);
                        clearFeeForm();
                        setFeeListButtonsEnabled(true);
                    }
                }
                if (e.getSource() == saveToProductBtn) {
                    doSaveToProductBtnAction();
                }
                if (e.getSource() == calculateBtn) {
                    doCalculateBtnAction();
                }
                if (e.getSource() == clearMaterialListBtn) {
                    clearMatList();
                }
                if (e.getSource() == clearFeeListBtn) {
                    clearFeeList();
                }
                if (e.getSource() == clearBtn) {
                    clearFields();
                }
                if (e.getSource() == removeMaterialFromListBtn) {
                    int selected = materialList.getSelectedIndex();
                    if (selected < -1) {
                        materialListModel.remove(selected);
                        if(materialList.getComponentCount() == 0){
                            setMaterialListButtonsEnabled(false);
                        }
                    } else {
                        Utils.showWarningMessage(getRootPane(), "No item selected");
                    }
                }
                if (e.getSource() == removeFeeFromListBtn) {
                    int selected = feeList.getSelectedIndex();
                    if (selected < -1) {
                        feeListModel.remove(selected);
                        if(feeList.getComponentCount() == 0){
                            setFeeListButtonsEnabled(false);
                        }
                    } else {
                        Utils.showWarningMessage(getRootPane(), "No item selected");
                    }
                }
            }
        };

        materialFormPanel.getAddBtn().addActionListener(actionListener);
        feeFormPanel.getAddBtn().addActionListener(actionListener);
        saveToProductBtn.addActionListener(actionListener);
        calculateBtn.addActionListener(actionListener);
        clearMaterialListBtn.addActionListener(actionListener);
        clearFeeListBtn.addActionListener(actionListener);
        clearBtn.addActionListener(actionListener);
        removeMaterialFromListBtn.addActionListener(actionListener);
        removeFeeFromListBtn.addActionListener(actionListener);
    }

    private void doSaveToProductBtnAction() {
        if (materialListModel.getSize() == 0) {
            Utils.showWarningMessage(getRootPane(), "The list of costs is empty!");
        } else {
            try {
                List<Asset> products = assetController.findAllProducts();
                if (!products.isEmpty()) {
                    String firstValue = products.get(0).getId()
                            + ") " + products.get(0).getName();
                    String[] productNames = new String[products.size()];
                    for (int i = 0; i < products.size(); i++) {
                        Asset asset = products.get(i);
                        productNames[i] = asset.getId() + ") " + asset.getName();
                    }
                    String s = (String) JOptionPane.showInputDialog(
                            getRootPane(), "Choose a product!", "",
                            JOptionPane.PLAIN_MESSAGE, null, productNames,
                            firstValue);
                    int idEnd = s.indexOf(")");
                    Integer id = Integer.parseInt(s.substring(0, idEnd));
                    Asset product = null;
                    for (Asset tempProduct : products) {
                        if (tempProduct.getId().equals(id)) {
                            product = tempProduct;
                        }
                    }
                    if (product.getMaterialCost() != 0) {
                        int confirm = JOptionPane.showConfirmDialog(getRootPane(),
                                "The selected product already has a material cost set."
                                + "\nDo you want to overrite it?",
                                "Confirm delete", JOptionPane.YES_NO_OPTION);
                        if (confirm == JOptionPane.NO_OPTION) {
                            return;
                        }
                    }
                    product.setMaterialCost(totalMaterialCost);
                    FormEvent event = new FormEvent(this);
                    event.setAsset(product);
                    if (formListener != null) {
                        formListener.formEventOccured(event);
                    }
                } else {
                    Utils.showWarningMessage(getRootPane(), "No saved products"
                            + "in the database");
                }
            } catch (SQLException ex) {
                Logger.getLogger(PriceCalculatorPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void doCalculateBtnAction() {
        if (materialListModel.getSize() == 0) {
            Utils.showWarningMessage(getRootPane(), "The list of material costs is empty!");
        } else {
            boolean valid = profitFieldCheck((String) profitCombo.getSelectedItem());
            if (valid) {
                int totalFee = 0;
                double profitAmount;
                for (int i = 0; i < feeListModel.getSize(); i++) {
                    Cost fee = (Cost) feeListModel.getElementAt(i);
                    int feeRate = fee.getExpenditure();
                    totalFee += feeRate;
                }
                String profitText = profitField.getText().replace(SEP, "");
                double profit = Double.parseDouble(profitText);
                double netto;
                double materialCost = totalMaterialCost / 100.0;
                if (((String) profitCombo.getSelectedItem()).contains("£")) {
                    profit = profit / 100.0;
                    netto = profit + materialCost;
                    profitAmount = profit;
                } else {
                    profitAmount = materialCost * (profit / 100);
                    netto = profitAmount + materialCost;

                }
                double nettoRate = 100 - totalFee;
                double feeAmount = totalFee * (netto / nettoRate);
                double price = (feeAmount + netto);

                priceLb.setText(DECIMAL_FORMAT.format(price));
                finalProfitLb.setText(DECIMAL_FORMAT.format(profitAmount));
            }
        }
    }

    private boolean profitFieldCheck(String comboOption) {
        String profitText = profitField.getText().trim();
        if (profitText.equals("")) {
            Utils.showWarningMessage(getRootPane(), "The profit field can not be empty!");
            return false;
        }
        String p = profitText;
        String message = "number";
        if (comboOption.contains("£")) {
            p = profitText.replace(SEP, "");
            message = "amount";
        }
        if (!p.matches("^[0-9]+$")) {
            Utils.showWarningMessage(getRootPane(), "Please enter a valid " + message + "!");
            return false;
        }
        int priceInt = Integer.parseInt(p);
        if (priceInt == 0) {
            Utils.showWarningMessage(getRootPane(), "The price can not be 0.");
            return false;
        }
        return true;
    }

    private Border createSectorBorder(String title) {
        Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
        Border lineBorder = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
        Border titledBorder = BorderFactory.createTitledBorder(lineBorder, title,
                TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION,
                new Font("Lucida Sans Unicode", 0, 12));
        return BorderFactory.createCompoundBorder(emptyBorder, titledBorder);

    }


    private void clearMatForm() {
        materialFormPanel.setNameFieldText("");
        materialFormPanel.setValueFieldText("0.00");
    }

    private void clearFeeForm() {
        feeFormPanel.setNameFieldText("");
        feeFormPanel.setValueFieldText("");
        
    }

    private void clearMatList() {
        materialListModel.clearList();
        totalMaterialCost = 0;
        materialCostLb.setText("0.00");
        setMaterialListButtonsEnabled(false);
    }
    
    private void clearFeeList(){
        feeListModel.clearList();
        setFeeListButtonsEnabled(false);
    }

    private void clearProfitForm() {
        profitField.setText("0.00");
        profitCombo.setSelectedIndex(0);
    }
    
    private void setFeeListButtonsEnabled(boolean enabled){   
        clearFeeListBtn.setEnabled(enabled);
        removeFeeFromListBtn.setEnabled(enabled);
        if(enabled){
            clearFeeListBtn.setBackground(Color.YELLOW.darker());
            removeFeeFromListBtn.setBackground(Color.RED.darker());
        }else{
            clearFeeListBtn.setBackground(Color.LIGHT_GRAY);
            removeFeeFromListBtn.setBackground(Color.LIGHT_GRAY);
        }
    }
    
    private void setMaterialListButtonsEnabled(boolean enabled){
        saveToProductBtn.setEnabled(enabled);
        clearMaterialListBtn.setEnabled(enabled);
        removeMaterialFromListBtn.setEnabled(enabled);
        if(enabled){
            saveToProductBtn.setBackground(Color.GREEN.darker());
            clearMaterialListBtn.setBackground(Color.YELLOW.darker());
            removeMaterialFromListBtn.setBackground(Color.RED.darker());
        }else{
            saveToProductBtn.setBackground(Color.LIGHT_GRAY);
            clearMaterialListBtn.setBackground(Color.LIGHT_GRAY);
            removeMaterialFromListBtn.setBackground(Color.LIGHT_GRAY);
        }
    }
    
    
    
    /**
     * Sets the transactionFormListener class member.
     *
     * @param formListener A <code>FormListener</code> to set the
     * transactionFormListener member of the class.
     */
    public void setFormListener(FormListener formListener) {
        this.formListener = formListener;
    }

    /**
     * Clears all the text fields and lists of the object.
     *
     */
    public void clearFields() {
        clearMatForm();
        clearFeeForm();
        clearMatList();
        clearFeeList();
        clearProfitForm();
        priceLb.setText("");
        finalProfitLb.setText("");
        setFeeListButtonsEnabled(false);
        setMaterialListButtonsEnabled(false);
    }
}
