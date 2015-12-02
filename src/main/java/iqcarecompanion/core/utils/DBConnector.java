/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.utils;

import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.DB_PASSWORD;
import static iqcarecompanion.core.utils.ConstantProperties.DB_USER;
import static iqcarecompanion.core.utils.ConstantProperties.HOST;
import static iqcarecompanion.core.utils.ConstantProperties.INSTANCE;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.PORT;
import static iqcarecompanion.core.utils.ConstantProperties.windowsAuthentication;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy
 */
public class DBConnector {

    final static Logger logger = Logger.getLogger(DBConnector.class.getName());
    private static Connection dbConnection;
    private static String DB_CONNECTION = null;
    
    private DBConnector() {}

    public static Connection connectionInstance() {
        if (dbConnection == null) {
            try {
                StringBuilder sbDbCon = new StringBuilder();
                sbDbCon.append("jdbc:jtds:sqlserver://")
                        .append(HOST)
                        .append(":").append(PORT)
                        .append(";databaseName=")
                        .append(DB_NAME)
                        .append(";instance=")
                        .append(INSTANCE);
                
                DB_CONNECTION = sbDbCon.toString();
                Class.forName(ConstantProperties.DB_DRIVER);
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "{0} {1}", new Object[]{LOG_PREFIX, e});
            }

            try {

                if (windowsAuthentication.equals("true")) {
                    dbConnection = DriverManager.getConnection(
                            DB_CONNECTION);
                    logger.log(Level.FINE,
                            "{0} Connection to IQCare database established. Using Windows Authentication.\n{1}",
                            new Object[]{LOG_PREFIX, DB_CONNECTION});

                } else {
                    dbConnection = DriverManager.getConnection(
                            DB_CONNECTION, DB_USER, DB_PASSWORD);
                    logger.log(Level.FINE,
                            "{0} Connection to IQCare database established. Using SQL Server Authentication.\n{1}",
                            new Object[]{LOG_PREFIX, DB_CONNECTION});
                }
                return dbConnection;

            } catch (SQLException e) {
                logger.log(Level.SEVERE, e.toString(), e);
            }

            return dbConnection;

        } else {
            return dbConnection;
        }
    }
    
}
