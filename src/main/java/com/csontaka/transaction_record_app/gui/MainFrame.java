package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.controller.AssetController;
import com.csontaka.transaction_record_app.controller.PeriodController;
import com.csontaka.transaction_record_app.controller.TransactionController;
import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

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
    private JButton incomeBtn;
    private JButton expenseBtn;
    private JButton summaryBtn;
    private JButton goalBtn;
    private JButton inventoryBtn;
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
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setLocationRelativeTo(null);

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
            if(lastPeriod != null){
               lastPeriodDate = lastPeriod.getDate();
            }else{
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
        initComponents();
        setGoalPanelFormListener();
        setIncomePanelFormListener();
        setExpensePanelFormListener();
        setIncomeTablePanelSaveFormListener();
        setExpenseTablePanelSaveFormListener();
        setIncomeTablePanelDeleteFormListener();
        setExpenseTablePanelDeleteFormListener();
        incomeBtnActionPerformed(null);
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
                if (e.getTransaction() != null) {
                    Transaction transToSave = e.getTransaction();
                    Asset assetToSave = e.getAsset();
                    try {
                        assetController.save(assetToSave);
                        if (assetToSave.getId() == null) {
                            Integer assetToSaveId = assetController.findLatest();
                            assetToSave.setId(assetToSaveId);
                            contentPanel.getInventoryPanel().getProductPanel().insertToTable(assetToSave);
                            transToSave.setAssetId(assetToSaveId);
                        } else {
                            List<Asset> assets = assetController.findAllProducts();
                            contentPanel.getInventoryPanel().getProductPanel().updateAssetsInModel(assets);
                        }
                        transController.save(transToSave);
                        if (transToSave.getId() == null) {
                            Integer id = transController.findLatest();
                            transToSave.setId(id);
                            contentPanel.getIncomeTablePanel().insertToTable(transToSave);
                        } else {
                            List<Transaction> trans = transController.findAllIncome();
                            contentPanel.getIncomeTablePanel().refresh(trans);
                        }

                    } catch (SQLException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "incomeTablePanel");
            }
        });
    }

    private void setExpensePanelFormListener() {
        contentPanel.getExpenseFormPanel().setTransactionFormListener(new FormListener() {

            @Override
            public void formEventOccured(FormEvent e) {
                Transaction transToSave = e.getTransaction();
                if (transToSave != null) {
                    Asset assetToSave = e.getAsset();
                    try {
                        assetController.save(assetToSave);
                        if (assetToSave.getId() == null) {
                            Integer assetToSaveId = assetController.findLatest();
                            assetToSave.setId(assetToSaveId);
                            contentPanel.getInventoryPanel().getEquipmentPanel().insertToTable(assetToSave);
                            transToSave.setAssetId(assetToSaveId);
                        } else {
                            List<Asset> assets = assetController.findAllEquipment();
                            contentPanel.getInventoryPanel().getEquipmentPanel().updateAssetsInModel(assets);
                        }
                        transController.save(transToSave);

                        if (transToSave.getId() == null) {
                            Integer id = transController.findLatest();
                            transToSave.setId(id);
                            contentPanel.getExpenseTablePanel().insertToTable(transToSave);
                        } else {
                            List<Transaction> trans = transController.findAllExpense();
                            contentPanel.getExpenseTablePanel().refresh(trans);
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                cl.show(contentPanel, "expenseTablePanel");
            }
        });
    }

    private void setIncomeTablePanelSaveFormListener() {
        contentPanel.getIncomeTablePanel().setSaveFormListener(new FormListener() {

            @Override
            public void formEventOccured(FormEvent e) {
                Asset tempAsset = e.getAsset();
                Period tempPeriod = e.getPeriod();
                Transaction tempTransaction = e.getTransaction();
                CardLayout cl = (CardLayout) contentPanel.getLayout();

                List<Period> periods = null;
                List<Asset> products = null;
                try {
                    periods = periodController.findBeforeADate(YearMonth.now().plusMonths(2));
                    products = assetController.findAllProducts();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                contentPanel.getIncomeFormPanel().setUpComboBoxesAndTitle(periods, products);
                cl.show(contentPanel, "incomeFormPanel");
                if (tempTransaction == null) {
                    contentPanel.getIncomeFormPanel().setUpForNew();
                } else {
                    contentPanel.getIncomeFormPanel().setUpForUpdate(tempAsset,
                            tempPeriod, tempTransaction);
                }
            }
        });
    }

    private void setExpenseTablePanelSaveFormListener() {
        contentPanel.getExpenseTablePanel().setSaveFormListener(new FormListener() {

            @Override
            public void formEventOccured(FormEvent e) {
                Asset tempAsset = e.getAsset();
                Period tempPeriod = e.getPeriod();
                Transaction tempTransaction = e.getTransaction();
                CardLayout cl = (CardLayout) contentPanel.getLayout();

                List<Period> periods = null;
                List<Asset> equipment = null;
                try {
                    periods = periodController.findBeforeADate(YearMonth.now().plusMonths(2));
                    equipment = assetController.findAllEquipment();
                } catch (SQLException ex) {
                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                }

                contentPanel.getExpenseFormPanel().setUpComboBoxesAndTitle(periods, equipment);
                cl.show(contentPanel, "expenseFormPanel");
                if (tempTransaction == null) {
                    contentPanel.getExpenseFormPanel().setUpForNew();
                } else {
                    contentPanel.getExpenseFormPanel().setUpForUpdate(tempAsset,
                            tempPeriod, tempTransaction);
                }
            }
        });
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

    private void incomeBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "incomeTablePanel");
        contentPanel.getIncomeTablePanel().setTitle("Income");
    }

    private void expenseBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "expenseTablePanel");
        contentPanel.getExpenseTablePanel().setTitle("Expense");
    }

    private void summaryBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "summaryTablePanel");
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
    }

    private void inventoryBtnActionPerformed(ActionEvent evt) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, "inventoryPanel");
    }

    private void initComponents() {
        contentPanel = new ContentPanel(assetController, periodController, transController);
        dataLabel = new JLabel("Transactions");
        dataLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        dataLabel.setHorizontalAlignment(SwingConstants.CENTER);

        goalLabel = new JLabel("Goals");
        goalLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        goalLabel.setHorizontalAlignment(SwingConstants.CENTER);

        inventoryLabel = new JLabel("Inventory");
        inventoryLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        inventoryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        incomeBtn = new JButton("Income");
        incomeBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 11));
        incomeBtn.setHorizontalAlignment(SwingConstants.CENTER);
        incomeBtn.setPreferredSize(new Dimension(90, 30));
        incomeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                incomeBtnActionPerformed(evt);
            }
        });

        expenseBtn = new JButton("Expense");
        expenseBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 11));
        expenseBtn.setHorizontalAlignment(SwingConstants.CENTER);
        expenseBtn.setPreferredSize(new Dimension(90, 30));
        expenseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                expenseBtnActionPerformed(evt);
            }
        });

        summaryBtn = new JButton("Summary");
        summaryBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 11));
        summaryBtn.setHorizontalAlignment(SwingConstants.CENTER);
        summaryBtn.setPreferredSize(new Dimension(90, 30));
        summaryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                summaryBtnActionPerformed(evt);
            }
        });

        goalBtn = new JButton("Set a goal");
        goalBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 11));
        goalBtn.setHorizontalAlignment(SwingConstants.CENTER);
        goalBtn.setPreferredSize(new Dimension(90, 30));
        goalBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                setGoalsBtnActionPerformed(evt);
            }
        });

        inventoryBtn = new JButton("Show");
        inventoryBtn.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 11));
        inventoryBtn.setHorizontalAlignment(SwingConstants.CENTER);
        inventoryBtn.setPreferredSize(new Dimension(90, 30));
        inventoryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                inventoryBtnActionPerformed(evt);
            }
        });

        menuPanel = new JPanel();
        menuPanel.setPreferredSize(new Dimension(110, 600));
        menuPanel.setBackground(new Color(0, 204, 172));
        menuPanel.setLayout(new GridBagLayout());
        setMenuPanelLayout();

        setLayout(new BorderLayout());
        add(menuPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

    }

    private void setMenuPanelLayout() {
        GridBagConstraints gc = new GridBagConstraints();
        Insets btnInsets = new Insets(0, 0, 10, 0);

        gc.gridy = 0;
        gc.insets = new Insets(-20, 0, 20, 0);
        menuPanel.add(dataLabel, gc);
        gc.gridy++;
        gc.insets = btnInsets;
        menuPanel.add(incomeBtn, gc);
        gc.gridy++;
        menuPanel.add(expenseBtn, gc);
        gc.gridy++;
        menuPanel.add(summaryBtn, gc);
        gc.gridy++;
        gc.insets = new Insets(20, 0, 20, 0);
        menuPanel.add(goalLabel, gc);
        gc.gridy++;
        gc.insets = btnInsets;
        menuPanel.add(goalBtn, gc);
        gc.gridy++;
        gc.insets = new Insets(20, 0, 20, 0);
        menuPanel.add(inventoryLabel, gc);
        gc.gridy++;
        gc.insets = btnInsets;
        menuPanel.add(inventoryBtn, gc);

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
