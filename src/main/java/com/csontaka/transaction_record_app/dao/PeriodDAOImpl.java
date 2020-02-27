package com.csontaka.transaction_record_app.dao;

import com.csontaka.transaction_record_app.entity.Period;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that implements the methods of
 * {@link com.csontaka.transaction_record_app.dao.PeriodRepository} interface.
 *
 * @author Adri
 */
public class PeriodDAOImpl implements PeriodRepository {

    private final Connection conn;
    private final PreparedStatement findAllPeriods;
    private final PreparedStatement findById;
    private final PreparedStatement findByDate;
    private final PreparedStatement findAfter;
    private final PreparedStatement findLatest;
    private final PreparedStatement addPeriod;
    private final PreparedStatement updatePeriod;

    /**
     * Creates a PeriodDAOImpl object with a specified connection.
     *
     * @param conn Connection object to make the connection with the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public PeriodDAOImpl(Connection conn) throws SQLException {
        this.conn = conn;
        findAllPeriods = conn.prepareStatement("SELECT * FROM periods");
        findById = conn.prepareStatement("SELECT * FROM periods WHERE id = ?");
        findByDate = conn.prepareStatement("SELECT * FROM periods WHERE "
                + "year = ? AND month = ?");
        findAfter = conn.prepareStatement("SELECT * FROM periods WHERE id > ?");
        findLatest = conn.prepareStatement("SELECT * FROM periods WHERE "
                + "id = (SELECT MAX(id) FROM periods);");
        addPeriod = conn.prepareStatement("INSERT INTO periods (year, month, goal) "
                + "VALUES( ?,  ?,  ?)");
        updatePeriod = conn.prepareStatement("UPDATE periods SET "
                + "year = ?, month = ?, goal = ? WHERE id = ?");
    }

    @Override
    public List<Period> findAll() throws SQLException {
        List<Period> periods;
        try (ResultSet allPeriods = findAllPeriods.executeQuery()) {
            periods = makeList(allPeriods);
        }
        return periods;
    }

    @Override
    public Period findById(Integer id) throws SQLException {
        findById.setInt(1, id);
        Period period;
        try (ResultSet periodById = findById.executeQuery()) {
            periodById.beforeFirst();
            periodById.next();
            period = makeOne(periodById);
        }
        return period;
    }

    @Override
    public Period findByDate(YearMonth yearMonth) throws SQLException {
        findByDate.setInt(1, yearMonth.getYear());
        findByDate.setInt(2, yearMonth.getMonthValue());
        Period period;
        try (ResultSet periodByDate = findByDate.executeQuery()) {
            periodByDate.beforeFirst();
            periodByDate.next();
            period = makeOne(periodByDate);
        }
        return period;
    }

    @Override
    public List<Period> findAfter(YearMonth date) throws SQLException {
        List<Period> periods;
        Period comparePeriod = findByDate(date);
        Integer compareId = comparePeriod.getId();
        findAfter.setInt(1, compareId);
        try (ResultSet periodsAfterADate = findAfter.executeQuery()) {
            periods = makeList(periodsAfterADate);
        }
        return periods;
    }

    @Override
    public Period findLatest() throws SQLException {
        Integer id;
        Period period;
        try (ResultSet latestPeriod = findLatest.executeQuery()) {
            latestPeriod.beforeFirst();
            latestPeriod.next();
            period = makeOne(latestPeriod);
        }
        return period;
    }

    @Override
    public void save(Period period) throws SQLException {
        if (period.getId() == null) {
            add(period);
        } else {
            update(period);
        }
    }

    private void add(Period period) throws SQLException {
        if (period.getId() == null) {
            addPeriod.setInt(1, period.getDate().getYear());
            addPeriod.setInt(2, period.getDate().getMonthValue());
            addPeriod.setDouble(3, period.getGoal());
            addPeriod.executeUpdate();
        }
    }

    private void update(Period period) throws SQLException {
        Integer id = period.getId();
        YearMonth date = period.getDate();
        int goal = period.getGoal();
        updatePeriod.setInt(1, date.getYear());
        updatePeriod.setInt(2, date.getMonthValue());
        updatePeriod.setDouble(3, goal);
        updatePeriod.setInt(4, id);
        updatePeriod.executeUpdate();
    }

    @Override
    public void close() throws SQLException {
        findAllPeriods.close();
        findById.close();
        findByDate.close();
        findAfter.close();
        findLatest.close();
        addPeriod.close();
        updatePeriod.close();
    }

    private List<Period> makeList(ResultSet rs) throws
            SQLException {
        List<Period> ret = new ArrayList<>();
        while (rs.next()) {
            ret.add(makeOne(rs));
        }
        return ret;
    }

    private Period makeOne(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        int year = rs.getInt("year");
        int month = rs.getInt("month");
        YearMonth yearMonth = YearMonth.of(year, month);
        int goal = rs.getInt("goal");
        Period period = new Period(yearMonth);
        period.setId(id);
        period.setGoal(goal);
        return period;
    }
}
