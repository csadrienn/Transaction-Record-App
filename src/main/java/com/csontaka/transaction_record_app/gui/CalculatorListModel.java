package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.entity.Cost;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 * Provides implementations for methods AbstractListModel methods to display the
 * required elements of the list.
 *
 * @author Adrienn Csontak
 */
public class CalculatorListModel extends AbstractListModel {

    List<Cost> costs;

    /**
     * Constructs a CalculatorListModel with an empty ArrayList of costs.
     *
     */
    public CalculatorListModel() {
        costs = new ArrayList<>();
    }

    /**
     * Adds a new element to the ArrayList and invokes the fireIntervalAdded of
     * the <code>AbstractListModel</code>.
     *
     * @param cost The Cost object to add to the list.
     */
    public void addElement(Cost cost) {
        costs.add(cost);
        fireIntervalAdded(this, costs.size() - 1, costs.size() - 1);
    }

    /**
     * Removes an element from the ArrayList and invokes the fireIntervalRemoved
     * of the <code>AbstractListModel</code>.
     *
     * @param index
     */
    public void remove(int index) {
        costs.remove(index);
        fireIntervalRemoved(this, index, index);
    }

    /**
     * Clears the ArrayList and invokes the fireIntervalRemoved of the
     * <code>AbstractListModel</code> on all of the elements.
     *
     */
    public void clearList() {
        int size = costs.size();
        costs.clear();
        if (size != 0) {
            fireIntervalRemoved(this, 0, size - 1);
        }
    }

    @Override
    public int getSize() {
        return costs.size();
    }

    @Override
    public Object getElementAt(int index) {
        return costs.get(index);
    }

}
