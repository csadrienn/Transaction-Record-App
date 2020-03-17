package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.PeriodController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.AssetType;
import com.csontaka.transaction_record_app.entity.Period;
import java.awt.CardLayout;
import java.util.List;
import javax.swing.JPanel;

/**
 * Contains and displays form and table panel objects.
 *
 * @author Adrienn Csontak
 */
public class ContentPanel extends JPanel {

    private SummaryTablePanel summaryTablePanel;
    private TransactionTablePanel incomeTablePanel;
    private TransactionTablePanel expenseTablePanel;
    private GoalFormPanel goalFormPanel;
    private TransactionFormPanel incomeFormPanel;
    private TransactionFormPanel expenseFormPanel;
    private InventoryPanel inventoryPanel;
    private PriceCalculatorPanel calculatorPanel;

    private AssetController assetController;
    private PeriodController perController;
    private TransactionController transController;

    /**
     * Creates a ContentPanel with the specified <code>AssetController</code>,
     * <code>PeriodController</code> and <code>TransactionController</code>.
     *
     * @param assetController An <code>AssetController</code> object to create a
     * connection with the <code>AssetRepository</code>.
     * @param perController A <code>PeriodController</code> object to create a
     * connection with the <code>PeriodRepository</code>.
     * @param transController A <code>TransactionController</code> object to
     * create a connection with the <code>TransactionRepository</code>.
     */
    public ContentPanel(AssetController assetController, PeriodController perController, TransactionController transController) {
        this.assetController = assetController;
        this.perController = perController;
        this.transController = transController;

        summaryTablePanel = new SummaryTablePanel(assetController,
                perController, transController);
        incomeTablePanel = new TransactionTablePanel(assetController,
                perController, transController, AssetType.PRODUCT);
        expenseTablePanel = new TransactionTablePanel(assetController,
                perController, transController, AssetType.EQUIPMENT);
        goalFormPanel = new GoalFormPanel();
        incomeFormPanel = new TransactionFormPanel(AssetType.PRODUCT);
        expenseFormPanel = new TransactionFormPanel(AssetType.EQUIPMENT);
        inventoryPanel = new InventoryPanel(assetController, transController);
        calculatorPanel = new PriceCalculatorPanel(assetController);

        setLayout(new CardLayout());

        add("incomeFormPanel", incomeFormPanel);
        add("expenseFormPanel", expenseFormPanel);
        add("summaryTablePanel", summaryTablePanel);
        add("incomeTablePanel", incomeTablePanel);
        add("expenseTablePanel", expenseTablePanel);
        add("goalFormPanel", goalFormPanel);
        add("inventoryPanel", inventoryPanel);
        add("calculatorPanel", calculatorPanel);

    }

    /**
     * Gets the content panel's SummaryTablePanel object that display the table
     * of the summary of the transactions.
     *
     * @return A SummaryTablePanel object.
     */
    public SummaryTablePanel getSummaryTablePanel() {
        return summaryTablePanel;
    }

    /**
     * Gets the content panel's TransactionTablePanel object that display the
     * table of incomes.
     *
     * @return A TransactionTablePanel object.
     */
    public TransactionTablePanel getIncomeTablePanel() {
        return incomeTablePanel;
    }

    /**
     * Gets the content panel's TransactionTablePanel object that display the
     * table of expenses.
     *
     * @return A TransactionTablePanel object.
     */
    public TransactionTablePanel getExpenseTablePanel() {
        return expenseTablePanel;
    }

    /**
     * Gets the content panel's GoalFormPanel object that display a form for
     * goal setting.
     *
     * @return A GoalFormPanel object.
     */
    public GoalFormPanel getGoalFormPanel() {
        return goalFormPanel;
    }

    /**
     * Gets the content panel's TransactionFormPanel object that display a form
     * for saving or updating incomes.
     *
     * @return A TransactionFormPanel object.
     */
    public TransactionFormPanel getIncomeFormPanel() {
        return incomeFormPanel;
    }

    /**
     * Gets the content panel's TransactionFormPanel object that display a form
     * for saving or updating expenses.
     *
     * @return A TransactionFormPanel object.
     */
    public TransactionFormPanel getExpenseFormPanel() {
        return expenseFormPanel;
    }

    /**
     * Sets the periods that are shown on the goal setting form so the user can
     * save or update these period's goal.
     *
     * @param periods A List object containing the Period to show on the goal
     * setting form.
     */
    public void setGoalFormData(List<Period> periods) {
        goalFormPanel.setPeriods(periods);
    }

    /**
     * Gets the content panel's InventoryPanel object that display a form for
     * saving or updating incomes.
     *
     * @return An InventoryPanel object.
     */
    public InventoryPanel getInventoryPanel() {
        return inventoryPanel;
    }

    /**
     * Gets the content panel's PriceCalculatorPanel object that display a form
     * for calculating a price for a product.
     *
     * @return A getPriceCalculatorPanel object.
     */
    public PriceCalculatorPanel getPriceCalculatorPanel() {
        return calculatorPanel;
    }

    /**
     * Clears the fields in the AssetPanel objects.
     *
     */
    public void clearAssetPanels() {
        inventoryPanel.getProductPanel().clearTextAndSelection();
        inventoryPanel.getEquipmentPanel().clearTextAndSelection();
    }
}
