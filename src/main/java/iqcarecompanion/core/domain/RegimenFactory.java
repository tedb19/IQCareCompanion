
package iqcarecompanion.core.domain;

import iqcarecompanion.core.utils.ConstantProperties;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import iqcarecompanion.core.utils.DBConnector;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class RegimenFactory {

    final static Logger logger = Logger.getLogger(RegimenFactory.class.getName());

    public static String getCurrentRegimen(int visit_pk) {

        PreparedStatement preparedStatement;
        ResultSet rs;
        String regimenType = "";
        StringBuilder sbSql = new StringBuilder();

        sbSql.append("USE [").append(ConstantProperties.DB_NAME)
                .append("]\n")
                .append("SELECT DISTINCT RegimenType")
                .append(" FROM ").append(ConstantProperties.DB_NAME)
                .append(".dbo.dtl_RegimenMap WHERE Visit_Pk = ?");
        
        try (Connection dbConnection = DBConnector.connectionInstance();) {

            preparedStatement = dbConnection.prepareStatement(sbSql.toString());
            preparedStatement.setInt(1, visit_pk);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                regimenType = rs.getString("RegimenType");
            }
        } catch (SQLException e) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred during the execution of the following query:\n")
                    .append(sbSql);
            logger.log(Level.SEVERE,sb.toString(),e);
        }
        return regimenType;
    }
}
