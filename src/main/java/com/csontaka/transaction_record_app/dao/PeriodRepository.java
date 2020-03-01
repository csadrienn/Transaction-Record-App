package com.csontaka.transaction_record_app.dao;

import com.csontaka.transaction_record_app.entity.Period;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

/**
 * Collects, inserts, updates and deletes {@link com.csontaka.transaction_records.entity.Period}
 * objects form the database.
 *
 * @author Adrienn Csontak
 */
public interface PeriodRepository {

    /**
     * Retrieves all periods from the database and gets a List object with the
     * periods.
     *
     * @return A List of <code>Period</code> objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Period> findAll() throws SQLException;

    /**
     * Gets a <code>Period</code> object from
     * the database with a specified id.
     *
     * @param id An Integer containing the period's id.
     * @return A <code>Period</code> object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Period findById(Integer id) throws SQLException;

    /**
     * Gets a <code>Period</code> object from
     * the database a specified year-month.
     *
     * @param yearMonth A YearMonth object containing the date of the Period.
     * @return A <code>Period</code> object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Period findByDate(YearMonth yearMonth) throws SQLException;

    /**
     * Gets a List of <code>Period</code>
     * objects from the database with year-month after a specified year-month.
     *
     * @param date A YearMonth object containing the date that the searched
     * period compare with.
     * @return A List of <code>Period</code> objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Period> findAfter(YearMonth date) throws SQLException;
    
    /**
     * Gets a List of <code>Period</code>
     * objects from the database with year-month before a specified year-month.
     *
     * @param date A YearMonth object containing the date that the searched
     * period compare with.
     * @return A List of <code>Period</code> objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Period> findBefore(YearMonth date) throws SQLException;

    /**
     * Gets an <code>Period</code> object
     * with the highest id value.
     *
     * @return A <code>Period</code> object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Period findLatest() throws SQLException;

    /**
     * Saves the given <code>Period</code> object to the database.
     *
     * @param period A <code>Period</code> object to save.
     * @throws SQLException If an SQL exception occurs.
     */
    public void save(Period period) throws SQLException;

    /**
     * Close the <code>PreparedStatement</code> objects of the class.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    public void close() throws SQLException;

}
