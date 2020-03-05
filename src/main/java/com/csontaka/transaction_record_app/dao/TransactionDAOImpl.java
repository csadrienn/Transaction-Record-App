package com.csontaka.transaction_record_app.dao;

import com.csontaka.transaction_record_app.entity.Transaction;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements the methods of
 * {@link com.csontaka.transaction_record_app.dao.TransactionRepository}
 * interface.
 *
 * @author Adrienn Csontak
 */
public class TransactionDAOImpl implements TransactionRepository {

    private final Connection conn;
    private final PreparedStatement findAllTransaction;
    private final PreparedStatement findById;
    private final PreparedStatement findByAssetId;
    private final PreparedStatement findByPeriodId;
    private final PreparedStatement findAllIncome;
    private final PreparedStatement findAllExpense;
    private final PreparedStatement findLatest;
    private final PreparedStatement addTransaction;
    private final PreparedStatement updateTransaction;
    private final PreparedStatement deleteTransaction;

    /**
     * Creates a TransactionDAOImpl object with a specified connection.
     *
     * @param conn Connection object to make the connection with the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public TransactionDAOImpl(Connection conn) throws SQLException {
        this.conn = conn;
        findAllTransaction = conn.prepareStatement("SELECT * FROM transactions");
        findById = conn.prepareStatement("SELECT * FROM transactions WHERE id = ?");
        findByAssetId = conn.prepareStatement("SELECT * FROM transactions WHERE asset_id = ?");
        findByPeriodId = conn.prepareStatement("SELECT * FROM transactions WHERE period_id = ?");
        findAllIncome = conn.prepareStatement("SELECT transactions.id, transactions.period_id, "
                + "transactions.asset_id, transactions.amount, transactions.price FROM transactions "
                + "INNER JOIN assets ON transactions.asset_id = assets.id WHERE assets.type = 1");
        findAllExpense = conn.prepareStatement("SELECT transactions.id, transactions.period_id, "
                + "transactions.asset_id, transactions.amount, transactions.price FROM transactions "
                + "INNER JOIN assets ON transactions.asset_id = assets.id WHERE assets.type = 0");
        findLatest = conn.prepareStatement("SELECT MAX(id) as id FROM transactions");
        addTransaction = conn.prepareStatement("INSERT INTO transactions (period_id, asset_id, amount, price) "
                + "VALUES( ?,  ?,  ?, ?)");
        updateTransaction = conn.prepareStatement("UPDATE transactions SET "
                + "period_id = ?, asset_id = ?, amount = ?, price = ? WHERE id = ?");
        deleteTransaction = conn.prepareStatement("DELETE FROM transactions WHERE id = ?");
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        List<Transaction> transactions;
        try (ResultSet allTransaction = findAllTransaction.executeQuery()) {
            transactions = makeList(allTransaction);
        }
        return transactions;
    }

    @Override
    public Transaction findById(Integer id) throws SQLException {
        findById.setInt(1, id);
        Transaction transaction = null;
        try (ResultSet transById = findById.executeQuery()) {
            transById.beforeFirst();
            if (transById.next()) {
                transaction = makeOne(transById);
            }
        }
        return transaction;
    }

    @Override
    public List<Transaction> findByAssetId(Integer assetId) throws SQLException {
        List<Transaction> transactions;
        findByAssetId.setInt(1, assetId);
        try (ResultSet transAssetById = findByAssetId.executeQuery()) {
            transactions = makeList(transAssetById);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findByPeriodId(Integer periodId) throws SQLException {
        List<Transaction> transactions;
        findByPeriodId.setInt(1, periodId);
        try (ResultSet transPeriodById = findByPeriodId.executeQuery()) {
            transactions = makeList(transPeriodById);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findAllIncome() throws SQLException {
        List<Transaction> transactions;
        try (ResultSet allIncome = findAllIncome.executeQuery()) {
            transactions = makeList(allIncome);
        }
        return transactions;
    }

    @Override
    public List<Transaction> findAllExpense() throws SQLException {
        List<Transaction> transactions;
        try (ResultSet allExpense = findAllExpense.executeQuery()) {
            transactions = makeList(allExpense);
        }
        return transactions;
    }

    @Override
    public Integer findLatest() throws SQLException {
        Integer id = null;
        try (ResultSet latestTransaction = findLatest.executeQuery()) {
            latestTransaction.beforeFirst();
            if (latestTransaction.next()) {
                id = latestTransaction.getInt("id");
            }
        }
        return id;
    }

    @Override
    public void save(Transaction transaction) throws SQLException {

        if (transaction.getId() == null) {

            add(transaction);
        } else {
            update(transaction);
        }
    }

    private void add(Transaction transaction) throws SQLException {
        if (transaction.getId() == null) {
            addTransaction.setInt(1, transaction.getPeriodId());
            addTransaction.setInt(2, transaction.getAssetId());
            addTransaction.setInt(3, transaction.getAmount());
            addTransaction.setDouble(4, transaction.getPrice());
            addTransaction.executeUpdate();
        }
    }

    private void update(Transaction transaction) throws SQLException {
        Integer id = transaction.getId();
        Integer periodId = transaction.getPeriodId();
        Integer assetId = transaction.getAssetId();
        int amount = transaction.getAmount();
        int price = transaction.getPrice();

        updateTransaction.setInt(1, periodId);
        updateTransaction.setInt(2, assetId);
        updateTransaction.setInt(3, amount);
        updateTransaction.setDouble(4, price);
        updateTransaction.setInt(5, id);
        updateTransaction.executeUpdate();
    }

    @Override
    public boolean delete(Integer id) throws SQLException {
        if (id != null) {
            this.deleteTransaction.setInt(1, id);
            this.deleteTransaction.executeUpdate();
            return true;
        }
        return false;
    }

    @Override
    public void close() throws SQLException {
        findAllTransaction.close();
        findById.close();
        findByAssetId.close();
        findByPeriodId.close();
        findAllIncome.close();
        findAllExpense.close();
        findLatest.close();
        addTransaction.close();
        updateTransaction.close();
        deleteTransaction.close();
    }

    private List<Transaction> makeList(ResultSet rs) throws
            SQLException {
        List<Transaction> ret = new ArrayList<>();
        while (rs.next()) {
            ret.add(makeOne(rs));
        }
        return ret;
    }

    private Transaction makeOne(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        int amount = rs.getInt("amount");
        int price = rs.getInt("price");
        Integer assetId = rs.getInt("asset_id");
        Integer periodId = rs.getInt("period_id");

        Transaction transaction = new Transaction(amount, price);
        transaction.setId(id);
        transaction.setAssetId(assetId);
        transaction.setPeriodId(periodId);
        return transaction;
    }

}
