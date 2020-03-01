package com.csontaka.transaction_record_app.controller;

import com.csontaka.transaction_record_app.dao.AssetDAOImpl;
import com.csontaka.transaction_record_app.dao.AssetRepository;
import com.csontaka.transaction_record_app.entity.Asset;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Creates connection between {@link com.csontaka.transaction_record_app.dao.AssetRepository}
 * and the classes of the gui package.
 *
 * @author Adrienn Csontak
 */
public class AssetController {

    private AssetRepository daoImp;

    /**
     * Initializes the repository object.
     *
     * @param conn Connection object to make the connection with the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public AssetController(Connection conn) throws SQLException {
        daoImp = new AssetDAOImpl(conn);
    }

    /**
     * Invokes the findById method of the
     * <code>AssetRepository</code>.
     * 
     * @param id An Integer containing the asset's id.
     * @return An Asset object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Asset findById(Integer id) throws SQLException {
        return daoImp.findById(id);
    }

    /**
     * Invokes the findAllProducts method of the
     * <code>AssetRepository</code>.
     *
     * @return return A List of Asset objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Asset> findAllProducts() throws SQLException {
        return daoImp.findAllProducts();
    }

    /**
     * Invokes the findAllEquipment method of the
     * <code>AssetRepository</code>.
     *
     * @return return A List of Asset objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Asset> findAllEquipment() throws SQLException {
        return daoImp.findAllEquipment();
    }

    /**
     * Invokes the findLatest method of the
     * <code>AssetRepository</code>.
     *
     * @return An Integer represents the id of an Asset.
     * @throws SQLException If an SQL exception occurs.
     */
    public Integer findLatest() throws SQLException {
        return daoImp.findLatest();
    }

    /**
     * Invokes the save method of the
     * <code>AssetRepository</code>.
     *
     * @param assetToSave An Asset object to save.
     * @throws SQLException If an SQL exception occurs.
     */
    public void save(Asset assetToSave) throws SQLException {
        daoImp.save(assetToSave);
    }

    /**
     * Invokes the close method of the
     * <code>AssetRepository</code>.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    public void close() throws SQLException {
        daoImp.close();
    }
}
