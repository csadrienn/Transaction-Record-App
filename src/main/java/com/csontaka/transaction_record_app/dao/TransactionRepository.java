package com.csontaka.transaction_record_app.dao;

import com.csontaka.transaction_record_app.entity.Transaction;
import java.sql.SQLException;
import java.util.List;

/**
 * Collects, inserts, updates and deletes {@link com.csontaka.transaction_records.entity.Transaction}
 * objects form the database.
 *
 * @author Adrienn Csontak
 */
public interface TransactionRepository {

    /**
     * Retrieves all transactions from the database and gets a List object with
     * the transactions.
     *
     * @return A List of <code>Transaction</code> objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findAll() throws SQLException;

    /**
     * Gets a <code>Transaction</code> object
     * from the database with a specified id.
     *
     * @param id An Integer containing the transaction's id.
     * @return A <code>Transaction</code> object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Transaction findById(Integer id) throws SQLException;

    /**
     * Gets a <code>Transaction</code> object
     * from the database with a specified assetId.
     *
     * @param assetId An Integer containing the assetId in a Transaction object.
     * @return A <code>Transaction</code> object.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findByAssetId(Integer assetId) throws SQLException;

    /**
     * Gets a <code>Transaction</code> object
     * from the database with a specified periodId.
     *
     * @param periodId An Integer containing the periodId in a Transaction
     * object.
     * @return A <code>Transaction</code> object.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findByPeriodId(Integer periodId) throws SQLException;

    /**
     * Retrieves all transactions from the database where the connected asset's
     * type is product(1) and gets a List object with the transactions.
     *
     * @return A List of <code>Transaction</code> objects
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findAllIncome() throws SQLException;

    /**
     * Retrieves all transactions from the database where the connected asset's
     * type is equipment(0) and gets a List object with the transactions.
     *
     * @return A List of <code>Transaction</code> objects
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findAllExpense() throws SQLException;

    /**
     * Gets an <code>Transaction</code>
     * object with the highest id value.
     *
     * @return A Transaction object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Integer findLatest() throws SQLException;

    /**
     * Saves the given <code>Transaction</code> object to the database.
     *
     * @param transaction A <code>Transaction</code> object to save.
     * @throws SQLException If an SQL exception occurs.
     */
    public void save(Transaction transaction) throws SQLException;

    /**
     * Deletes the object <code>Transaction</code> specified by id from the database.
     *
     * @param id An Integer containing the id of the Transaction object to
     * delete.
     * @return True if the deleting successful, false if not.
     * @throws SQLException If an SQL exception occurs.
     */
    public boolean delete(Integer id) throws SQLException;

    /**
     * Close the <code>PreparedStatement</code> objects of the class.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    public void close() throws SQLException;
}
