package com.csontaka.transaction_record_app.entity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Represents the material cost of a product.
 *
 * @author Adrienn Csontak
 */
public class Material extends Cost {

    private final Locale LOCALE = new Locale("en", "UK");
    private final DecimalFormat DECIMAL_FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(LOCALE);

    /**
     * Calls the super class basic constructor.
     *
     */
    public Material() {
        super();
        DECIMAL_FORMAT.applyPattern("##0.00");
    }

    /**
     * Calls the super class constructor with specified name and expenditure
     * fields.
     *
     * @param name The name of the material cost.
     * @param expenditure The expenditure of the production.
     */
    public Material(String name, int expenditure) {
        super(name, expenditure);
        DECIMAL_FORMAT.applyPattern("##0.00");
    }

    @Override
    public String toString() {
        return super.getName() + "  £" + DECIMAL_FORMAT.format(super.getExpenditure() / 100.0);
    }

}
