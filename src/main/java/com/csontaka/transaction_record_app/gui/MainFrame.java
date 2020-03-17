package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.PeriodController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.AssetType;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * The main window of the application. Inherits the javax.swing.JFrame class.
 *
 * @author Adrienn Csontak
 */
public class MainFrame extends JFrame {

    private JPanel menuPanel;
    private JLabel dataLabel;
    private JLabel goalLabel;
    private JLabel inventoryLabel;
    private JLabel calculatorLabel;
    private JButton incomeBtn;
    private JButton expenseBtn;
    private JButton summaryBtn;
    private JButton goalBtn;
    private JButton inventoryBtn;
    private JButton calculatorBtn;
    private ContentPanel contentPanel;
    private Connection conn;
    private AssetController assetController;
    private PeriodController periodController;
    private TransactionController transController;
    private static final Properties PROP = new Properties();

    /**
     * Creates a new frame, sets the size, the visibility, the closing
     * operation, its component and the database connection.
     */
    public MainFrame() {

        
        setPreferredSize(new Dimension(850, 650));

        try {
            PROP.load(new FileReader("config.ini"));
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            String server = PROP.getProperty("server");
            String portNumber = PROP.getProperty("port_number");
            String database = PROP.getProperty("database");
            String user = PROP.getProperty("user");
            String password = PROP.getProperty("password");
            conn = DriverManager.getConnection("jdbc:mysql://"
                    + server + ":" + portNumber + "/" + database, user, password);
            assetController = new AssetController(conn);
            periodController = new PeriodController(conn);
            transController = new TransactionController(conn);
            contentPanel = new ContentPanel(assetController,
                    periodController, transController);

            Period lastPeriod = periodController.findLatest();
            YearMonth lastPeriodDate;
            if (lastPeriod != null) {
                lastPeriodDate = lastPeriod.getDate();
            } else {
                lastPeriodDate = YearMonth.now().minusMonths(2);
            }

            YearMonth monthsFromNow = YearMonth.now()
                    .plusMonths(GoalFormPanel.MAX_PERIOD_DATE - 1);

            while (!lastPeriodDate.equals(monthsFromNow)) {
                YearMonth plusOneMonth = lastPeriodDate.plusMonths(1);
                Period newPeriod = new Period(plusOneMonth);
                periodController.save(newPeriod);
                lastPeriodDate = plusOneMonth;
            }

        } catch (SQLException e) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, e);
        }

        initComponents();
        setGoalPanelFormListener();
        setIncomePanelFormListener();
        setExpensePanelFormListener();
        setIncomeTablePanelSaveFormListener();
        setExpenseTablePanelSaveFormListener();
        setIncomeTablePanelDeleteFormListener();
        setExpenseTablePanelDeleteFormListener();
        setCalculatorListener();
        incomeBtnActionPerformed(null);
        
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                try {
                    transController.close();
                    periodController.close();
                    assetController.close();
                    conn.close();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);
            }
        });
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void setGoalPanelFormListener() {
        contentPanel.getGoalFormPanel().setGoalFormListener(new FormListener() {

            @Override
            public void formEventOccured(FormEvent e) {
                try {
                    int newGoal = e.getPeriod().getGoal();
                    YearMonth yearMonth = e.getPeriod().getDate();

                    Period periodToChange = periodController.findPeriodByDate(yearMonth);
                    periodToChange.setGoal(newGoal);

                    periodController.save(periodToChange);
                    List<Period> periodsToGoals = periodController
                            .findAfterADate(YearMonth.now().minusMonths(1));
                    List<Period> periodsToSummaryTable = periodController.
                            findBeforeADate(YearMonth.now().plusMonths(1));
                    contentPanel.getGoalFormPanel().setPeriods(periodsToGoals);
                    contentPanel.getSummaryTablePanel().refresh(periodsToSummaryTable);

                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void setIncomePanelFormListener() {
        contentPanel.getIncomeFormPanel().setTransactionFormListener(new FormListener() {

            @Override
            public void formEventOccured(FormEvent e) {
                doPanelFormListenerAction(contentPanel.getIncomeTablePanel(),
                        contentPanel.getInventoryPanel().getProductPanel(), AssetType.PRODUCT, e);
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "incomeTablePanel");
            }
        });
    }

    private void setExpensePanelFormListener() {
        contentPanel.getExpenseFormPanel().setTransactionFormListener(new FormListener() {

            @Override
            public void formEventOccured(FormEvent e) {
                doPanelFormListenerAction(contentPanel.getExpenseTablePanel(),
                        contentPanel.getInventoryPanel().getEquipmentPanel(), AssetType.EQUIPMENT, e);
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "expenseTablePanel");
            }
        });
    }

    private void doPanelFormListenerAction(TransactionTablePanel tablePanel,
            AssetPanel assetPanel, AssetType type, FormEvent e) {
        if (e.getChangedTransaction() != null) {
            Transaction transToSave = e.getChangedTransaction();
            Asset assetToSave = e.getAsset();
            try {
                //transaction to insert
                if (transToSave.getId() == null) {
                    assetController.save(assetToSave);
                    if (assetToSave.getId() == null) {
                        insertNewAssetToTableAndTrans(assetToSave,
                                assetPanel, transToSave);
                    } else {
                        refreshAssetTableAndAddToTrans(assetToSave,
                                assetPanel, transToSave, type);
                    }
                    transController.save(transToSave);
                    Integer id = transController.findLatest();
                    transToSave.setId(id);
                    tablePanel.insertToTable(transToSave);

                } else {
                    Transaction oldTransaction = e.getOldTransaction();
                    Integer oldAssetId = oldTransaction.getAssetId();
                    Integer newAssetId = assetToSave.getId();

                    if (newAssetId != null && newAssetId.equals(oldAssetId)) {
                        int stock = assetToSave.getStock();
                        stock = stock + oldTransaction.getAmount() - transToSave.getAmount();
                        assetToSave.setStock(stock);
                    } else {
                        Asset oldAsset = assetController.findById(oldAssetId);
                        oldAsset.setStock(oldAsset.getStock() + oldTransaction.getAmount());
                        assetController.save(oldAsset);
                        assetToSave.setStock(assetToSave.getStock() - transToSave.getAmount());
                    }

                    assetController.save(assetToSave);
                    if (assetToSave.getId() == null) {
                        insertNewAssetToTableAndTrans(assetToSave,
                                assetPanel, transToSave);
                    }
                    refreshAssetTableAndAddToTrans(assetToSave,
                            assetPanel, transToSave, type);
                    transController.save(transToSave);
                    List<Transaction> trans;
                    if (type.equals(AssetType.EQUIPMENT)) {
                        trans = transController.findAllExpense();
                    } else {
                        trans = transController.findAllIncome();
                    }
                    tablePanel.refresh(trans);
                }
            } catch (SQLException ex) {
                Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void insertNewAssetToTableAndTrans(Asset assetToSave,
            AssetPanel assetPanel, Transaction trans) throws SQLException {
        Integer assetToSaveId = assetController.findLatest();
        assetToSave.setId(assetToSaveId);
        assetPanel.insertToTable(assetToSave);
        trans.setAssetId(assetToSaveId);
    }

    private void refreshAssetTableAndAddToTrans(Asset asset,
            AssetPanel assetPanel, Transaction trans, AssetType type) throws SQLException {
        List<Asset> assets;
        if (type.equals(AssetType.EQUIPMENT)) {
            assets = assetController.findAllEquipment();
        } else {
            assets = assetController.findAllProducts();
        }

        assetPanel.updateAssetsInModel(assets);
        trans.setAssetId(asset.getId());
    }

    private void setIncomeTablePanelSaveFormListener() {
        contentPanel.getIncomeTablePanel().setSaveFormListener(new FormListener() {
            @Override
            public void formEventOccured(FormEvent e) {
                doTablePanelAction(contentPanel.getIncomeFormPanel(), AssetType.PRODUCT, e);
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "incomeFormPanel");
            }
        });
    }

    private void setExpenseTablePanelSaveFormListener() {
        contentPanel.getExpenseTablePanel().setSaveFormListener(new FormListener() {
            @Override
            public void formEventOccured(FormEvent e) {
                doTablePanelAction(contentPanel.getExpenseFormPanel(), AssetType.EQUIPMENT, e);
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "expenseFormPanel");
            }
        });
    }

    private void doTablePanelAction(TransactionFormPanel formPanel, AssetType type, FormEvent e) {
        Period tempPeriod = e.getPeriod();
        Transaction tempTransaction = e.getChangedTransaction();
        Asset tempAsset = null;

        List<Period> periods = null;
        List<Asset> equipment = null;
        try {
            periods = periodController.findBeforeADate(YearMonth.now().plusMonths(1));
            if (type.equals(AssetType.PRODUCT)) {
                equipment = assetController.findAllProducts();
            } else {
                equipment = assetController.findAllEquipment();
            }
            if (e.getAsset() != null) {
                tempAsset = assetController.findById(e.getAsset().getId());
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        formPanel.setUpComboBoxesAndTitle(periods, equipment);
        if (tempTransaction == null) {
            formPanel.setUpForNew();
        } else {
            formPanel.setUpForUpdate(tempAsset,
                    tempPeriod, tempTransaction);
        }

    }

    private void setIncomeTablePanelDeleteFormListener() {
        contentPanel.getIncomeTablePanel().setDeleteFormListener(new FormListener() {

            @Override
            public void formEventOccured(FormEvent e) {
                Asset asset = e.getAsset();
                try {
                    assetController.save(asset);
                    List<Asset> assets = assetController.findAllProducts();
                    contentPanel.getInventoryPanel().getProductPanel().updateAssetsInModel(assets);

                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    private void setExpenseTablePanelDeleteFormListener() {
        contentPanel.getExpenseTablePanel().setDeleteFormListener(new FormListener() {

            @Override
            public void formEventOccured(FormEvent e) {
                Asset asset = e.getAsset();
                try {
                    assetController.save(asset);
                    List<Asset> assets = assetController.findAllEquipment();
                    contentPanel.getInventoryPanel().getEquipmentPanel().updateAssetsInModel(assets);
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private void setCalculatorListener(){
        contentPanel.getPriceCalculatorPanel().setFormListener(new FormListener() {
            @Override
            public void formEventOccured(FormEvent e) {
                Asset asset = e.getAsset();
                if(asset != null){
                    try {
                        assetController.save(asset);
                        List<Asset> assets = assetController.findAllProducts();
                        contentPanel.getInventoryPanel().getProductPanel().updateAssetsInModel(assets);
                    } catch (SQLException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
    }
    
    private void incomeBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "incomeTablePanel");
        contentPanel.getIncomeTablePanel().setTitle("Income");
        clearPages();
    }

    private void expenseBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "expenseTablePanel");
        contentPanel.getExpenseTablePanel().setTitle("Expense");
        clearPages();
    }

    private void summaryBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "summaryTablePanel");
        clearPages();
    }

    private void setGoalsBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "goalFormPanel");
        contentPanel.getGoalFormPanel().emptySuccessLabel();
        List<Period> periods = null;
        try {
            periods = periodController
                    .findAfterADate(YearMonth.now().minusMonths(1));
        } catch (SQLException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        contentPanel.getGoalFormPanel().setPeriods(periods);
        clearPages();
    }

    private void inventoryBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "inventoryPanel");
       clearPages();
    }
    
    private void calculatorBtnActionPerformed(ActionEvent evt){
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "calculatorPanel");
       clearPages();
        
        
    }

    private void clearPages(){
        contentPanel.clearAssetPanels();
        contentPanel.getPriceCalculatorPanel().clearFields();
    }
    
    private void initComponents() {
        contentPanel = new ContentPanel(assetController, periodController, transController);
        Font labelFont = new Font("Lucida Sans Unicode", 0, 16);
        dataLabel = new JLabel("Transactions");
        dataLabel.setFont(labelFont);
        dataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        goalLabel = new JLabel("Goals");
        goalLabel.setFont(labelFont);
        goalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setFont(labelFont);
        inventoryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        calculatorLabel = new JLabel("Price calculator");
        calculatorLabel.setFont(labelFont);
        calculatorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension btnDimension = new Dimension(95, 30);
        incomeBtn = new JButton("Income");
        Utils.setButtonLook(incomeBtn, 11);
        incomeBtn.setMaximumSize(btnDimension);
        incomeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                incomeBtnActionPerformed(evt);
            }
        });

        expenseBtn = new JButton("Expense");
        Utils.setButtonLook(expenseBtn, 11);
        expenseBtn.setMaximumSize(btnDimension);
        expenseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                expenseBtnActionPerformed(evt);
            }
        });

        summaryBtn = new JButton("Summary");
        Utils.setButtonLook(summaryBtn, 11);
        summaryBtn.setMaximumSize(btnDimension);
        summaryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                summaryBtnActionPerformed(evt);
            }
        });

        goalBtn = new JButton("Set a goal");
        Utils.setButtonLook(goalBtn, 11);
        goalBtn.setMaximumSize(btnDimension);
        goalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setGoalsBtnActionPerformed(evt);
            }
        });

        inventoryBtn = new JButton("Show");
        Utils.setButtonLook(inventoryBtn, 11);
        inventoryBtn.setMaximumSize(btnDimension);
        inventoryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                inventoryBtnActionPerformed(evt);
            }
        });
        
        calculatorBtn = new JButton("Calculate");
        Utils.setButtonLook(calculatorBtn, 11);
        calculatorBtn.setMaximumSize(btnDimension);
        calculatorBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                calculatorBtnActionPerformed(evt);
            }
        });

        menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(130, 600));
        menuPanel.setBackground(new Color(159, 223, 188));
        setMenuPanelLayout();

        setLayout(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

    }

    private void setMenuPanelLayout() {
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        dataLabel.setBorder(BorderFactory.createEmptyBorder(103, 0, 0, 0));
        addToMenu(dataLabel, 15);
        addToMenu(incomeBtn, 12);
        addToMenu(expenseBtn, 12);
        addToMenu(summaryBtn, 22);
        addToMenu(goalLabel, 15);
        addToMenu(goalBtn, 20);
        addToMenu(inventoryLabel, 15);
        addToMenu(inventoryBtn, 20);
        addToMenu(calculatorLabel, 15);
        addToMenu(calculatorBtn, 15);
    }

    private void addToMenu(Component component, int space) {
        menuPanel.add(component);
        menuPanel.add(Box.createRigidArea(new Dimension(0, space)));
    }

    /**
     * Starts the application, creates a MainFrame object.
     *
     * @param args
     */
    public static void main(String args[]) {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame();
            }
        });
    }

}
