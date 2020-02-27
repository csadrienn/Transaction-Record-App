package com.csontaka.transaction_record_app.entity;

import java.time.YearMonth;

/**
 * Represents a one-month period
 *
 * @author Adri
 */
public class Period {

    private Integer id;
    private YearMonth date;
    private int goal;

    /**
     * Default constructor to create a period.
     */
    public Period() {
    }

    /**
     * Creates a period with specified year-month.
     *
     * @param date The period's date in years and months.
     */
    public Period(YearMonth date) {
        this();
        this.date = date;
    }

    /**
     * Gets the period’s date in years and months.
     *
     * @return A YearMonth representing the period’s date.
     */
    public YearMonth getDate() {
        return date;
    }

    /**
     * Sets the period’s date in years and months.
     *
     * @param date A YearMonth containing the period’s date.
     */
    public void setDate(YearMonth date) {
        this.date = date;
    }

    /**
     * Gets the goal of incomes for this period.
     *
     * @return An int representing the goal of incomes for this period.
     */
    public int getGoal() {
        return goal;
    }

    /**
     * Sets the goal of incomes for this period.
     *
     * @param goal An int containing the goal of incomes for this period.
     */
    public void setGoal(int goal) {
        this.goal = goal;
    }

    /**
     * Gets the period's unique id.
     *
     * @return An Integer representing the period's unique id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Sets the period's unique id.
     *
     * @param id An Integer containing the period's unique id.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return date.getYear() + "/" + date.getMonthValue();
    }
}
