package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.entity.Period;
import java.awt.BorderLayout;
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
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * Creates a form for the user to set a goal for an one-month period.
 *
 * @author Adrienn Csonták
 */
public class GoalFormPanel extends JPanel implements ItemListener {

    /**
     * The number of period options to choose.
     */
    public static final int MAX_PERIOD_DATE = 6;
    private static final String PATTERN = "yyyy/MM";
    private JLabel titleLabel;
    private JPanel formPanel;

    private final String SUCCES = "Goal setting succesful";

    private List<Period> periods;
    private JLabel dateLabel;
    private JLabel goalLabel;
    private JLabel successLabel;
    private JComboBox dateCombo;
    private JTextField goalTextField;
    private JLabel currencyLabel;
    private JButton confirmButton;

    private DateTimeFormatter df;
    private Locale locale = new Locale("en", "UK");
    private DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
    private FormListener goalFormListener;

    /**
     * Default constructor. Initializes the class members and displays the
     * components.
     */
    public GoalFormPanel() {
        decimalFormat.applyPattern("##0.00");
        df = DateTimeFormatter.ofPattern(PATTERN);
        this.periods = new ArrayList<>();
        titleLabel = new JLabel("Goal setting");
        formPanel = new JPanel();
        goalLabel = new JLabel("Goal:");
        successLabel = new JLabel("");
        goalTextField = new JTextField(7);
        dateLabel = new JLabel("Date:");
        currencyLabel = new JLabel("£");
        confirmButton = new JButton("Save");

        YearMonth ym = YearMonth.now();
        String[] dates = new String[MAX_PERIOD_DATE];
        for (int i = 0; i < dates.length; i++) {
            dates[i] = df.format(ym);
            ym = ym.plusMonths(1);
        }
        dateCombo = new JComboBox(dates);

        confirmButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String message = null;
                String sep = decimalFormat.getDecimalFormatSymbols().getDecimalSeparator() + "";
                String goalStr = goalTextField.getText();
                String date = (String) dateCombo.getSelectedItem();
                goalStr = goalStr.replace(sep, "");
                System.out.println(goalStr);
                if (date != null) {
                    try {
                        int goal = Integer.parseInt(goalStr);
                        System.out.println(goal);
                        Period period = new Period(YearMonth.parse(date, df));
                        period.setGoal(goal);
                        FormEvent formEvent = new FormEvent(this);
                        formEvent.setPeriod(period);
                        successLabel.setText(SUCCES);
                        goalTextField.setText("");
                        if (goalFormListener != null) {
                            goalFormListener.formEventOccured(formEvent);
                        }

                    } catch (NumberFormatException ex) {
                        message = "Please enter a valid number!";
                        JOptionPane.showMessageDialog(getRootPane(), message, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    message = "Please select a date!";
                    JOptionPane.showMessageDialog(getRootPane(), message, "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        layoutComponents();

    }

    /**
     * Filters the received List of Period objects keeping only periods of the
     * current month or the months after. Sets the class member List using the
     * filtered List. The Period objects are used in the combo box as items.
     *
     * @param periods List of Period objects to set the class member.
     */
    public void setPeriods(List<Period> periods) {
        this.periods = periods;
    }

    /**
     * Sets the text of the label empty which informs the user that the goal
     * setting was successful.
     *
     */
    public void emptySuccessLabel() {
        successLabel.setText("");
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == dateCombo) {
            int periodIndex = dateCombo.getSelectedIndex();
            Period period = periods.get(periodIndex);

            if (period.getGoal() > 0) {
                double goal = period.getGoal() / 100.0;
                goalTextField.setText(decimalFormat.format(goal));
            } else {
                goalTextField.setText("");
            }
        }
    }

    private void layoutComponents() {
        titleLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 24)); // NOI18N
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 20));

        Border innerBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        Border outerBorder = BorderFactory.createEmptyBorder(20, 20, 20, 20);
        formPanel.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        goalLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        goalLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        successLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 12));
        successLabel.setHorizontalAlignment(SwingConstants.CENTER);

        dateCombo.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        dateCombo.setSelectedIndex(-1);
        dateCombo.addItemListener(this);

        dateLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        dateLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        currencyLabel.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        currencyLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        confirmButton.setFont(new java.awt.Font("Lucida Sans Unicode", 0, 14));
        setFormPanelLayout();
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
    }

    private void setFormPanelLayout() {
        GridBagLayout gridBagLayout = new GridBagLayout();
        formPanel.setLayout(gridBagLayout);

        GridBagConstraints gbc = new GridBagConstraints();
        Insets firstColumnInsets = new Insets(0, 0, 30, 30);
        Insets secondColumnInsets = new Insets(0, 0, 30, 0);

        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = firstColumnInsets;
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        formPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.insets = secondColumnInsets;
        formPanel.add(dateCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = firstColumnInsets;
        formPanel.add(goalLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, -10, 30, 0);
        gbc.anchor = GridBagConstraints.FIRST_LINE_START;
        formPanel.add(currencyLabel, gbc);

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.insets = new Insets(-3, -92, 30, 0);
        formPanel.add(goalTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = secondColumnInsets;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(successLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(confirmButton, gbc);
    }

    /**
     * Sets the goalFormListener class member.
     *
     * @param goalFormListener A FormListener to set the goalFormListener member
     * of the class.
     */
    public void setGoalFormListener(FormListener goalFormListener) {
        this.goalFormListener = goalFormListener;
    }
}
