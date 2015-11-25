
package iqcarecompanion.core.domain;

import iqcarecompanion.core.utils.DBConnector;
import iqcarecompanion.core.utils.ResourceManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy
 */
public class RegimenFactory {

    final static Logger logger = Logger.getLogger(RegimenFactory.class.getName());

    private static String getCurrentRegimen(int visit_pk, String dbName) {

        PreparedStatement preparedStatement;
        ResultSet rs;
        String regimenType = "";

        try (Connection dbConnection = DBConnector.connectionInstance();) {
            StringBuilder sbSql = new StringBuilder();
            
            sbSql.append("USE [").append(dbName)
                    .append("]\n")
                    .append("SELECT DISTINCT RegimenType")
                    .append(" FROM ").append(dbName)
                    .append(".dbo.dtl_RegimenMap WHERE Visit_Pk = ?");
            preparedStatement = dbConnection.prepareStatement(sbSql.toString());
            preparedStatement.setInt(1, visit_pk);
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                regimenType = rs.getString("RegimenType");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.toString(), e);
        }
        return regimenType;
    }
    
     public static String currentRegimen(int visit_pk){
         String regimen="";
         try {
            String dbName = ResourceManager.readConfigFile("db_name", "iqcarecompanion.properties");
            regimen = getCurrentRegimen(visit_pk, dbName);
         } catch(IOException e){
             logger.log(Level.SEVERE, e.toString(), e);
         }
         return regimen;
     }
}
