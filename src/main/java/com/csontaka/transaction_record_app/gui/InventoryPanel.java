package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.AssetType;
import java.awt.BorderLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Displays a tabbed pane with two panels: a product and an equipment panel
 * showing a table the assets and a form for manipulating the assets.
 *
 * @author Adrienn Csontak
 */
public class InventoryPanel extends JPanel {

    private AssetPanel productPanel;
    private AssetPanel equipmentPanel;
    private JLabel inventoryLabel;
    private JTabbedPane tabbedPane;

    /**
     * Constructs an InventoryPanel with the <code>AssetController</code>, and
     * <code>TransactionController</code>.
     *
     * @param assetController An <code>AssetController</code> object to create a
     * connection with the <code>AssetRepository</code>.
     * @param transactionController A <code>TransactionController</code> object
     * to create a connection with the <code>TransactionRepository</code>.
     */
    public InventoryPanel(AssetController assetController,
            TransactionController transactionController) {

        inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setFont(new Font("Lucida Sans Unicode", 0, 22));
        inventoryLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        productPanel = new AssetPanel(assetController, transactionController, AssetType.PRODUCT);
        equipmentPanel = new AssetPanel(assetController, transactionController, AssetType.EQUIPMENT);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBounds(50, 50, 200, 200);
        tabbedPane.add("Product", productPanel);
        tabbedPane.add("Equipment", equipmentPanel);
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.add(inventoryLabel);
        add(titlePanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Gets the <code>AssetPanel</code> object of the inventory panel that
     * displays a table of products and a form for manipulating products.
     *
     * @return An <code>AssetPanel</code> object for handling products.
     */
    public AssetPanel getProductPanel() {
        return productPanel;
    }

    /**
     * Gets the <code>AssetPanel</code> object of the inventory panel that
     * displays a table of equipment and a form for manipulating equipment.
     *
     * @return An <code>AssetPanel</code> object for handling equipment.
     */
    public AssetPanel getEquipmentPanel() {
        return equipmentPanel;
    }

}
