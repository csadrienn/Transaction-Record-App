package com.csontaka.transaction_record_app.entity;

/**
 * Represents fee by selling a product.
 *
 * @author Adrienn Csontak
 */
public class Fee extends Cost {

    /**
     * Calls the super class basic constructor.
     *
     */
    public Fee() {
        super(null, 0);
    }

    /**
     * Calls the super class constructor with specified name and expenditure
     * fields.
     *
     * @param name The name of the fee.
     * @param expenditure The selling expenditure as a percentage.
     */
    public Fee(String name, int expenditure) {
        super(name, expenditure);
    }

    @Override
    public String toString() {
        return super.getName() + " " + super.getExpenditure() + "%";
    }

}
