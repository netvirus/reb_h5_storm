package l2r.gameserver.dao;

import l2r.commons.dbutils.DbUtils;
import l2r.gameserver.data.xml.parser.PremiumSystemOptionsData;
import l2r.gameserver.database.DatabaseFactory;
import l2r.gameserver.model.actor.instances.player.PremiumBonus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Premium system DAO MySQL implementation
 * @author netvirus
 */

public class PremiumSystemDAO {

    private static final Logger LOG = LoggerFactory.getLogger(PremiumSystemDAO.class);

    private final Map<Boolean, PremiumBonus> premiumType = new HashMap<>();

    private static final String SELECT = "SELECT * FROM premium_system WHERE char_id=? AND active=1";
    private static final String INSERT = "INSERT INTO premium_system (char_id, bonus_id, bonus_expire) VALUES (?,?,?)";
    private static final String DISABLE = "UPDATE premium_system SET active=0 WHERE id=?";

    public Map load(int objectId) {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(SELECT);
            statement.setInt(1, objectId);
            rset = statement.executeQuery();
            while(rset.next()) {
                if (rset.getLong("bonus_expire") > (System.currentTimeMillis() / 1000L)) {
                    PremiumBonus premium = new PremiumBonus(PremiumSystemOptionsData.getInstance().findById(rset.getInt("bonus_id")));
                    premium.setBonusDuration(rset.getLong("bonus_expire"));
                    premiumType.put(premium.isBonusMain(), premium);
                } else {
                    disable(rset.getInt("id"));
                }
            }
        }
        catch(SQLException e)
        {
            LOG.error("Error while restore premium for owner ID: " + objectId, e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement, rset);
        }
        return premiumType;
    }

    public void insert(int objectId, int bonusId, long duration) {
        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(INSERT);
            statement.setInt(1, objectId);
            statement.setInt(2, bonusId);
            statement.setLong(3, duration);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Failed insert premium data for owner ID: " + objectId, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void disable(int id) {
        Connection con = null;
        PreparedStatement statement = null;
        try {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement(DISABLE);
            statement.setInt(1, id);
            statement.execute();
        } catch (SQLException e) {
            LOG.error("Failed disable premium data for ID: " + id, e);
        } finally {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public static PremiumSystemDAO getInstance() {
        return PremiumSystemDAO.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final PremiumSystemDAO INSTANCE = new PremiumSystemDAO();
    }
}