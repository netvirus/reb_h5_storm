package l2r.gameserver.dao;

import l2r.commons.dbutils.DbUtils;
import l2r.gameserver.data.xml.parser.PremiumSystemOptionsData;
import l2r.gameserver.database.DatabaseFactory;
import l2r.gameserver.model.L2PremiumBonus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Premium system DAO MySQL implementation
 * @author netvirus
 */

public class PremiumSystemDAO {

    private static final Logger LOG = LoggerFactory.getLogger(PremiumSystemDAO.class);

    private final Map<Boolean, L2PremiumBonus> premiumType = new HashMap<>();

    private static final String SELECT = "SELECT * FROM premium_system WHERE char_id=? AND active=1";
    private static final String INSERT = "INSERT INTO premium_system (char_id, bonus_id, bonus_expire) VALUES (?,?,?)";
    private static final String DISABLE = "UPDATE premium_system SET active=0 WHERE id=?";

    public static void loadPremiumAccounts()
    {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        PreparedStatement deleteStatement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT * FROM premium_accounts");
            rs = statement.executeQuery();

            while (rs.next())
            {
                String accountName = rs.getString("accountName");
                int templateId = rs.getInt("templateId");
                long premiumEndTime = rs.getLong("endTime");

                // If the premium account is expired, delete it from the table.
                if (premiumEndTime < System.currentTimeMillis())
                {
                    deleteStatement = con.prepareStatement("DELETE FROM premium_accounts WHERE accountName=?");
                    deleteStatement.setString(1, accountName);
                    deleteStatement.execute();
                }
//                else
//                {
//                    if (_premiumTemplates.containsKey(templateId))
//                        _premiumAccounts.put(accountName.hashCode(), new PremiumAccountsTable.PremiumAccount(templateId, premiumEndTime));
//                    else
//                        LOG.warn("PremiumAccountsTable: Premium Template not found for ID " + templateId);
//                }
            }
        }
        catch (Exception e)
        {
            LOG.error("PremiumAccountTable: Failed loading data.", e);
        }
        finally
        {
            DbUtils.closeQuietly(deleteStatement);
            DbUtils.closeQuietly(con, statement, rs);
        }
    }

//    public Map load(int objectId) {
//        try (var con = ConnectionFactory.getInstance().getConnection();
//             var ps = con.prepareStatement(SELECT)) {
//            ps.setInt(1, objectId);
//            try (var rset = ps.executeQuery()) {
//                if (rset.next()) {
//                    if (rset.getLong("bonus_expire") > (System.currentTimeMillis() / 1000L))
//                    {
//                        L2PremiumBonus premium = new L2PremiumBonus(PremiumSystemOptionsData.getInstance().findById(rset.getInt("bonus_id")));
//                        premium.setBonusDuration(rset.getLong("bonus_expire"));
//                        premiumType.put(premium.isBonusMain(), premium);
//                    }
//                    else
//                    {
//                        disable(rset.getInt("id"));
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LOG.error("Failed loading premium data. {}", e);
//        }
//        return premiumType;
//    }
//
//    @Override
//    public void insert(int charId, int bonusId, long duration) {
//        try (var con = ConnectionFactory.getInstance().getConnection();
//             var ps = con.prepareStatement(INSERT)) {
//            ps.setInt(1, charId);
//            ps.setInt(2, bonusId);
//            ps.setLong(3, duration);
//            ps.executeQuery();
//        } catch (Exception e) {
//            LOG.error("Failed insert premium data. {}", e);
//        }
//    }

//    @Override
//    public void disable(int id) {
//        try (var con = ConnectionFactory.getInstance().getConnection();
//             var ps = con.prepareStatement(DISABLE)) {
//            ps.setInt(1, id);
//            ps.executeQuery();
//        } catch (Exception e) {
//            LOG.error("Failed disable premium data. {}", e);
//        }
//    }
}
