package com.csontaka.transaction_record_app.controller;

import com.csontaka.transaction_record_app.dao.TransactionDAOImpl;
import com.csontaka.transaction_record_app.dao.TransactionRepository;
import com.csontaka.transaction_record_app.entity.Transaction;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Creates connection between {@link com.csontaka.transaction_record_app.dao.TransactionRepository}
 * and the classes of the gui package.
 *
 * @author Adrienn Csontak
 */
public class TransactionController {

    private TransactionRepository daoImpl;

    /**
     * Initializes the repository object.
     *
     * @param conn Connection object to make the connection with the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public TransactionController(Connection conn) throws SQLException {
        daoImpl = new TransactionDAOImpl(conn);
    }

    /**
     * Invokes the findAllTransaction method of the
     * <code>TransactionRepository</code>.
     *
     * @return A List of Transaction objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findAllTransaction() throws SQLException {
        return daoImpl.findAll();
    }

    /**
     * Invokes the findByPeriodId method of the
     * <code>TransactionRepository</code>.
     *
     * @param periodId An Integer containing the id of the period when the
     * transaction happened.
     * @return A List of Transaction object.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findByPeriodId(Integer periodId) throws SQLException {
        return daoImpl.findByPeriodId(periodId);
    }

    /**
     * Invokes the findAllIncome method of the
     * <code>TransactionRepository</code>.
     *
     * @return A List of Transaction object.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findAllIncome() throws SQLException {
        return daoImpl.findAllIncome();
    }

    /**
     * Invokes the findAllExpense method of the
     * <code>TransactionRepository</code>.
     *
     * @return A List of Transaction object.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Transaction> findAllExpense() throws SQLException {
        return daoImpl.findAllExpense();
    }

    /**
     * Invokes the save method of the
     * <code>TransactionRepository</code>.
     *
     * @param transToSave A Transaction object to save.
     * @throws SQLException If an SQL exception occurs.
     */
    public void save(Transaction transToSave) throws SQLException {
        daoImpl.save(transToSave);
    }

    /**
     * Invokes the delete method of the
     * <code>TransactionRepository</code>.
     *
     * @param id An Integer containing the id of the Transaction object to
     * delete.
     * @return True if the deleting successful, false if not.
     * @throws SQLException If an SQL exception occurs.
     */
    public boolean delete(Integer id) throws SQLException {
        return daoImpl.delete(id);
    }

    /**
     * Invokes the close method of the
     * <code>TransactionRepository</code>.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    public void close() throws SQLException {
        daoImpl.close();
    }

}
