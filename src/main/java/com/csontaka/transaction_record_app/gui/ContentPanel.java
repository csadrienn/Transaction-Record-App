package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.PeriodController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.AssetType;
import com.csontaka.transaction_record_app.entity.Period;
import java.awt.CardLayout;
import java.util.List;
import javax.swing.JPanel;

/**
 * Contains and displays form and table panel objects.
 *
 * @author Adri
 */
public class ContentPanel extends JPanel {

    private SummaryTablePanel summaryTablePanel;
    private TransactionTablePanel incomeTablePanel;
    private TransactionTablePanel expenseTablePanel;
    private GoalFormPanel goalFormPanel;
    private TransactionFormPanel incomeFormPanel;
    private TransactionFormPanel expenseFormPanel;

    private AssetController assetController;
    private PeriodController perController;
    private TransactionController transController;

    /**
     * Creates a ContentPanel with the specified AssetController,
     * PeriodController and TransactionController.
     *
     * @param assetController An AssetController object to create a connection
     * with the AssetRepository.
     * @param perController A PeriodController object to create a connection
     * with the PeriodRepository.
     * @param transController A TransactionController object to create a
     * connection with the TransactionRepository.
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

        setLayout(new CardLayout());
        add("incomeFormPanel", incomeFormPanel);
        add("expenseFormPanel", expenseFormPanel);
        add("summaryTablePanel", summaryTablePanel);
        add("incomeTablePanel", incomeTablePanel);
        add("expenseTablePanel", expenseTablePanel);
        add("goalFormPanel", goalFormPanel);
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
     * Gets the content panel's getGoalFormPanel object that display a form for
     * goal setting.
     *
     * @return A getGoalFormPanel object.
     */
    public GoalFormPanel getGoalFormPanel() {
        return goalFormPanel;
    }

    /**
     * Gets the content panel's getIncomeFormPanel object that display a form
     * for saving or updating incomes.
     *
     * @return A getIncomeFormPanel object.
     */
    public TransactionFormPanel getIncomeFormPanel() {
        return incomeFormPanel;
    }

    /**
     * Gets the content panel's getIncomeFormPanel object that display a form
     * for saving or updating expenses.
     *
     * @return A getExpenseFormPanel object.
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
     * Sets the periods and assets that are shown on a transaction form panel so
     * the user can save or update a transaction connected with a chosen period
     * and asset.
     *
     * @param periods A List object containing the Period to show on the
     * transaction setting form.
     * @param assets A List object containing the Asset to show on the
     * transaction setting form.
     */
    public void setTransactionFormPanelsData(List<Period> periods, List<Asset> assets) {
        incomeFormPanel.setUpComboBoxesAndTitle(periods, assets);
        expenseFormPanel.setUpComboBoxesAndTitle(periods, assets);

    }
}
