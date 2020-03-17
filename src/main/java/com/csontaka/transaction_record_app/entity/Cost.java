package com.csontaka.transaction_record_app.entity;

/**
 * Represents the production or selling cost of a product.
 *
 * @author Adrienn Csontak
 */
public abstract class Cost {

    private String name;
    private int expenditure;

    /**
     * Construct a Cost object, sets the name field to null and the the
     * expenditure to 0.
     *
     */
    public Cost() {
        this(null, 0);
    }

    /**
     * Construct a Cost object with specified name and expenditure fields.
     *
     * @param name The name of the cost.
     * @param expenditure The production or selling expenditure that can be an
     * amount or a percentage.
     */
    public Cost(String name, int expenditure) {
        this.name = name;
        this.expenditure = expenditure;
    }

    /**
     * Gets the name of the cost.
     *
     * @return A String representing the name of the cost.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the cost.
     *
     * @param name A String containing the name of the cost.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the expenditure of the Cost object.
     *
     * @return An int representing expenditure of the Cost object.
     */
    public int getExpenditure() {
        return expenditure;
    }

    /**
     * Sets the expenditure of the Cost object.
     *
     * @param expenditure An int containing expenditure of the Cost object.
     */
    public void setExpenditure(int expenditure) {
        this.expenditure = expenditure;
    }

}
