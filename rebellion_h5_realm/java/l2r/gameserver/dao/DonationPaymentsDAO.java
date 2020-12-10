package l2r.gameserver.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import l2r.commons.dbutils.DbUtils;
import l2r.gameserver.database.DatabaseFactory;
import l2r.gameserver.model.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * @author netvirus
 */

public class DonationPaymentsDAO {

    private static final Logger _log = LoggerFactory.getLogger(DonationPaymentsDAO.class);
    private static final DonationPaymentsDAO _instance = new DonationPaymentsDAO();

    // SQL
    public static final String SELECT_SQL_QUERY = "SELECT * FROM character_payments WHERE unit_status = 1 AND status <> 1";
    public static final String UPDATE_SQL_QUERY = "UPDATE character_payments SET status = 1 WHERE char_name=? AND id=?";
    public static final String INSERT_SQL_QUERY = "INSERT INTO character_payments(char_name, amount, pay_data) VALUES (?,?,?)";

    public ArrayList<Payment> getPayments()
    {
        ArrayList<Payment> payments = new ArrayList<>();

        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT_SQL_QUERY);
            rset = statement.executeQuery();
            if (rset != null)
            {
                while (rset.next())
                {
                    payments.add(new Payment(rset.getInt("id"), rset.getString("char_name"), rset.getInt("amount"), rset.getInt("pay_data"), rset.getInt("status")));
                }
            }
        }
        catch (Exception e)
        {
            _log.error("DonationPaymentsDAO.getPayments(): " + e, e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return payments;
    }

    public void changeStatusToReceived(Payment payment)
    {
        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(UPDATE_SQL_QUERY);
            statement.setString(1, payment.getRecipient());
            statement.setInt(2, payment.getId());
            statement.execute();
        }
        catch (Exception e)
        {
            _log.error("DonationPaymentsDAO.changeStatusToReceived(String charName, int paymentId): " + e, e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void addPayment(String charName, int amount, int payData)
    {
        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT_SQL_QUERY);
            statement.setString(1, charName);
            statement.setInt(2, amount);
            statement.setInt(3, payData);
            statement.execute();
        }
        catch (Exception e)
        {
            _log.error("DonationPaymentsDAO.addPayment(String charName, int amount, int payData): " + e, e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public static DonationPaymentsDAO getInstance()
    {
        return _instance;
    }
}
