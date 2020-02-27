package com.csontaka.transaction_record_app.entity;

/**
 * Represent a transaction.
 *
 * @author Adrienn Csonták
 */
public class Transaction {

    private Integer id;
    private int amount;
    private Integer periodId;
    private Integer assetId;
    private int price;

    /**
     * Default constructor.
     *
     */
    public Transaction() {
    }

    /**
     * Creates a transaction with specified amount and price.
     *
     * @param amount The amount of assets sold or bought through this
     * transaction
     * @param price The price of the asset in this transaction.
     */
    public Transaction(int amount, int price) {
        this();
        this.amount = amount;
        this.price = price;
    }

    /**
     * Gets the price of the asset in this transaction.
     *
     * @return An int representing the price of the asset in this transaction.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Sets the price of the asset in this transaction.
     *
     * @param price An int containing the price of the asset in this
     * transaction.
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Gets the period's id that the transaction happened in.
     *
     * @return A {@link csontaka.transaction_records.entity.Period} represents
     * the period's id  that the transaction happened in.
     */
    public Integer getPeriodId() {
        return periodId;
    }

    /**
     * Sets the period's id that the transaction happened in.
     *
     * @param periodId A {@link csontaka.transaction_records.entity.Period}
     * represents the period's id  that the transaction happened in.
     */
    public void setPeriodId(Integer periodId) {
        this.periodId = periodId;
    }

    /**
     * Gets the asset's id  sold or bought through the transaction.
     *
     * @return An {@link csontaka.transaction_records.entity.Asset} represents
     * the asset's id  sold or bought through the transaction.
     */
    public Integer getAssetId() {
        return assetId;
    }

    /**
     * Sets the asset's id sold or bought through the transaction.
     *
     * @param assetId An {@link csontaka.transaction_records.entity.Asset}
     * represents the asset's id sold or bought through the transaction.
     */
    public void setAssetId(Integer assetId) {
        this.assetId = assetId;
    }

    /**
     * Sets the transaction's unique id.
     *
     * @return An Integer representing the transaction's unique id.
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the transaction's unique id.
     *
     * @param id An Integer containing the transaction's unique id.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the amount of assets sold or bought through the transaction.
     *
     * @return An int representing the amount of assets sold or bought through
     * the transaction.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of assets sold or bought through the transaction.
     *
     * @param amount An int containing the amount of assets sold or bought
     * through the transaction.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    
    
}
