package com.csontaka.transaction_record_app.dao;

import com.csontaka.transaction_record_app.entity.Asset;
import java.sql.SQLException;
import java.util.List;

/**
 * Collects, inserts, updates and deletes {@link com.csontaka.transaction_records.entity.Asset} 
 * objects form the database.
 *
 * @author Adrienn Csontak
 */
public interface AssetRepository {

    /**
     * Retrieves all assets from the database and gets a List object with the
     * assets.
     *
     * @return A List of <code>Asset</code>.
     * objects
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Asset> findAll() throws SQLException;

    /**
     * Retrieves all assets from the database with the type product(1) and gets
     * a List object with the assets.
     *
     * @return A List of <code>Asset</code> objects
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Asset> findAllProducts() throws SQLException;

    /**
     * Retrieves all assets from the database with the type equipment(0) and
     * gets a List object with the assets.
     *
     * @return A List of <code>Asset</code> objects
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Asset> findAllEquipment() throws SQLException;

    /**
     * Gets an <code>Asset</code> object from
     * the database with a specified id.
     *
     * @param id An Integer containing the asset's id.
     * @return An <code>Asset</code> object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Asset findById(Integer id) throws SQLException;

    /**
     * Gets an <code>Asset</code> object with
     * the highest id value.
     *
     * @return An <code>Asset</code> object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Integer findLatest() throws SQLException;

    /**
     * Saves the given <code>Asset</code> object to the database.
     *
     * @param asset An <code>Asset</code> object to save.
     * @throws SQLException If an SQL exception occurs.
     */
    public void save(Asset asset) throws SQLException;

    /**
     * Deletes the given <code>Asset</code> object to from database.
     *
     * @param asset An <code>Asset</code> object to delete.
     * @return True if the deleting successful, false if not.
     * @throws SQLException If an SQL exception occurs.
     */
    public boolean delete(Asset asset) throws SQLException;

    /**
     * Close the <code>PreparedStatement</code> objects of the class.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    public void close() throws SQLException;
}
