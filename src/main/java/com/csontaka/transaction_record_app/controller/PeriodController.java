package com.csontaka.transaction_record_app.controller;

import com.csontaka.transaction_record_app.dao.PeriodDAOImpl;
import com.csontaka.transaction_record_app.dao.PeriodRepository;
import com.csontaka.transaction_record_app.entity.Period;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.YearMonth;
import java.util.List;

/**
 * Creates connection between PeriodRepository and the classes of the gui
 * package
 *
 * @author Adri
 */
public class PeriodController {

    private PeriodRepository daoImp;

    /**
     * Initializes the repository object.
     *
     * @param conn Connection object to make the connection with the database.
     * @throws SQLException If an SQL exception occurs.
     */
    public PeriodController(Connection conn) throws SQLException {
        daoImp = new PeriodDAOImpl(conn);
    }

    /**
     * Invokes the findAllPeriods method of the
     * {@link com.csontaka.transaction_record_app.dao.PeriodRepository}.
     *
     * @return A List of Period objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Period> findAllPeriods() throws SQLException {
        return daoImp.findAll();
    }

    /**
     * Invokes the findById method of the
     * {@link com.csontaka.transaction_record_app.dao.PeriodRepository}.
     *
     * @param id An Integer containing the period's id.
     * @return A Period object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Period findById(Integer id) throws SQLException {
        return daoImp.findById(id);
    }

    /**
     * Invokes the findPeriodByDate method of the
     * {@link com.csontaka.transaction_record_app.dao.PeriodRepository}.
     *
     * @param date A YearMonth object containing the date of the period.
     * @return A Period object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Period findPeriodByDate(YearMonth date) throws SQLException {
        return daoImp.findByDate(date);
    }

    /**
     * Invokes the findAfterADate method of the
     * {@link com.csontaka.transaction_record_app.dao.PeriodRepository}.
     *
     * @param date A YearMonth object containing the specified date using for
     * the comparison.
     * @return A List of Period objects.
     * @throws SQLException If an SQL exception occurs.
     */
    public List<Period> findAfterADate(YearMonth date) throws SQLException {
        return daoImp.findAfter(date);
    }

    /**
     * Invokes the findLatest method of the
     * {@link com.csontaka.transaction_record_app.dao.PeriodRepository}.
     *
     * @return A Period object.
     * @throws SQLException If an SQL exception occurs.
     */
    public Period findLatest() throws SQLException {
        return daoImp.findLatest();
    }

    /**
     * Invokes the save method of the
     * {@link com.csontaka.transaction_record_app.dao.PeriodRepository}.
     *
     * @param newPeriod A Period object to save.
     * @throws SQLException If an SQL exception occurs.
     */
    public void save(Period newPeriod) throws SQLException {
        daoImp.save(newPeriod);
    }

    /**
     * Invokes the close method of the
     * {@link com.csontaka.transaction_record_app.dao.PeriodRepository}.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    public void close() throws SQLException {
        daoImp.close();
    }

}
