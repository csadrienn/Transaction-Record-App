package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.entity.Asset;
import com.csontaka.transaction_record_app.entity.Period;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.util.EventObject;

/**
 * Stores an <code>Asset</code>, a <code>Product</code> and a <code>Transaction</code>. 
 * Helps the data transfer between panels.
 *
 * @author Adrienn Csontak
 */
class FormEvent extends EventObject {

    private Transaction changedTransaction;
    private Asset asset;
    private Transaction oldTransaction;
    private Period period;

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
     * Create a FormEvent with specified source, changedTransaction, asset and period.
     *
     * @param source The object on which the Event initially occurred.
     * @param transaction The <code>Transaction</code> object passed this FormEvent to store.
     * @param asset The <code>Asset</code> object passed this FormEvent to store.
     * @param period The <code>Product</code> object passed this FormEvent to store.
     */
    public FormEvent(Object source, Transaction transaction, Asset asset, Period period) {
        this(source);
        this.changedTransaction = transaction;
        this.asset = asset;
        this.period = period;
    }

    /**
     * Gets the asset stored in the form event.
     *
     * @return An <code>Asset</code> object.
     */
    public Asset getAsset() {
        return asset;
    }

    /**
     * Sets the asset to be stored in the form event.
     *
     * @param asset An <code>Asset</code> object the be stored.
     */
    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    /**
     * Gets the period stored in the form event.
     *
     * @return A <code>Product</code> object.
     */
    public Period getPeriod() {
        return period;
    }

    /**
     * Sets the period to be stored in the form event.
     *
     * @param period A <code>Product</code> object the be stored.
     */
    public void setPeriod(Period period) {
        this.period = period;
    }

    public Transaction getChangedTransaction() {
        return changedTransaction;
    }

    public void setChangedTransaction(Transaction changedTransaction) {
        this.changedTransaction = changedTransaction;
    }

    public Transaction getOldTransaction() {
        return oldTransaction;
    }

    public void setOldTransaction(Transaction oldTransaction) {
        this.oldTransaction = oldTransaction;
    }

    
    
}
