
package iqcarecompanion.core.dao;

import iqcarecompanion.core.utils.ConstantProperties;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import iqcarecompanion.core.utils.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class RegimenDao {
    
    private static final Logger LOGGER = Logger.getLogger(RegimenDao.class.getName());
    private final Connection connection;
    
    public RegimenDao(Connection connection){
        this.connection = connection;
    }

    public String getCurrentRegimen(int visitId, String dbName) {

        PreparedStatement preparedStatement;
        ResultSet rs;
        String regimenType = "";
        StringBuilder sbSql = new StringBuilder();
        if(StringUtils.isNotEmpty(dbName)){
            sbSql.append("USE ").append(dbName).append("\n");
        }
        sbSql.append(" SELECT DISTINCT RegimenType")
                .append(" FROM ").append("dtl_RegimenMap WHERE Visit_Pk = ")
                .append(visitId).append(";");
        try{
            preparedStatement = this.connection.prepareStatement(sbSql.toString());
            rs = preparedStatement.executeQuery();
            while (rs.next()) {
                regimenType = rs.getString("RegimenType");
            }
        } catch (SQLException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred during the execution of the following query:\n")
                    .append(sbSql);
            LOGGER.log(Level.SEVERE,sb.toString(),e);
        }
        return regimenType;
    }
}
