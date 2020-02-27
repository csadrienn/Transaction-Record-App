package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.util.EventObject;

/**
 * Stores an Asset, a Product, a Transaction and an int for a row. Helps the
 * data transfer between panels.
 *
 * @author Adrienn Csonták
 */
class FormEvent extends EventObject {

    private Transaction transaction;
    private Asset asset;
    private Period period;
    private int row;

    /**
     * Default constructor calling the parent class constructor with a specified
     * source.
     *
     * @param source The object on which the Event initially occurred.
     */
    public FormEvent(Object source) {
        super(source);
    }

    /**
     * Create a FormEvent with specified source, transaction, asset and period.
     *
     * @param source The object on which the Event initially occurred.
     * @param transaction The Transaction object passed this FormEvent to store.
     * @param asset The Asset object passed this FormEvent to store.
     * @param period The Period object passed this FormEvent to store.
     */
    public FormEvent(Object source, Transaction transaction, Asset asset, Period period) {
        this(source);
        this.transaction = transaction;
        this.asset = asset;
        this.period = period;
    }

    /**
     * Gets the transaction stored in the form event.
     *
     * @return A Transaction object.
     */
    public Transaction getTransaction() {
        return transaction;
    }

    /**
     * Sets the transaction to be stored in the form event.
     *
     * @param transaction A Transaction object the be stored.
     */
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    /**
     * Gets the asset stored in the form event.
     *
     * @return An Asset object.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset to be stored in the form event.
     *
     * @param asset An Asset object the be stored.
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the period stored in the form event.
     *
     * @return A Period object.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Sets the period to be stored in the form event.
     *
     * @param period A Period object the be stored.
     */
    public void setPeriod(Period period) {
        this.period = period;
    }

    /**
     * Gets the row of a table stored in the form event.
     *
     * @return An int representing a row of a table.
     */
    public int getRow() {
        return row;
    }

    /**
     * Sets the row of a table to be stored in the form event.
     *
     * @param row An int containing the row of a table.
     */
    public void setRow(int row) {
        this.row = row;
    }

}
