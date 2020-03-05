package com.csontaka.transaction_record_app.gui;

import com.csontaka.transaction_record_app.entity.Asset;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Provides implementations for methods <code>AbstractTableModel</code> methods
 * to display the required fields in the
 * {@link com.csontaka.transaction_record_app.gui.AssetPanel}'s JTable member.
 *
 * @author Adrienn Csontak
 */
public class AssetTableModel extends AbstractTableModel {

    private List<Asset> assets;
    private final String[] COL_NAMES = {"Id", "Name", "Stock"};

    /**
     * Constructs an AssetTableModel with the specified list of asset.
     *
     * @param assets A List of <code>Asset</code> objects using them as table
     * members.
     */
    public AssetTableModel(List<Asset> assets) {
        this.assets = assets;
    }

    /**
     * Inserts a <code>Asset</code> object to the List class member and invokes
     * the fireTableRowsInserted method of the <code>AbstractTableModel</code>.
     *
     * @param asset
     */
    public void addAsset(Asset asset) {
        assets.add(asset);
        int row = assets.size() - 1;
        fireTableRowsInserted(row, row);
    }

    /**
     * Sets the list of assets to the received list. Invokes the
     * fireTableDataChanged method of the <code>AbstractTableModel</code>.
     *
     * @param assets A List of <code>Asset</code> object containing the updated
     * values.
     */
    public void updateAssets(List<Asset> assets) {
        this.assets = assets;
        fireTableDataChanged();
    }

    /**
     * Removes a <code>Asset</code> object to the List class member and invokes
     * the fireTableRowsDeleted method of the <code>AbstractTableModel</code>.
     *
     * @param assetToDelete A <code>Asset</code> object to remove from the list.
     */
    public void deleteAsset(Asset assetToDelete) {
        int row = -1;
        for (int i = 0; i < assets.size() && row < 0; i++) {
            if (assets.get(i).getId().equals(assetToDelete.getId())) {
                row = i;
            }
        }
        assets.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public String getColumnName(int column) {
        return COL_NAMES[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (assets.isEmpty()) {
            return Object.class;
        }
        return getValueAt(0, columnIndex).getClass();

    }

    @Override
    public int getRowCount() {
        int size;
        if (assets == null) {
            size = 0;
        } else {
            size = assets.size();
        }
        return size;
    }

    @Override
    public int getColumnCount() {
        return COL_NAMES.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Object temp = null;
        Asset a = assets.get(row);
        if (col == 0) {
            temp = a.getId();
        }
        if (col == 1) {
            temp = a.getName();
        }
        if (col == 2) {
            temp = a.getStock();
        }
        return temp;

    }

}
