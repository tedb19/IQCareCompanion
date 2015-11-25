/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.utils;

import java.io.IOException;
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
    private static String DB_DRIVER = null;
    private static String DB_CONNECTION = null;
    private static String DB_USER = "";
    private static String DB_PASSWORD = "";
    private static String windowsAuthentication = "false";
    private static String HOST = null;
    private static String INSTANCE = null;
    private static String PORT = null;
    private static String DB_NAME = null;
    private static Connection dbConnection;

    private DBConnector() {

    }

    public static Connection connectionInstance() {
        if (dbConnection == null) {
            try {
                DB_DRIVER = ResourceManager.readConfigFile("db_driver", "iqcarecompanion.properties");
                HOST = ResourceManager.readConfigFile("host", "iqcarecompanion.properties");
                PORT = ResourceManager.readConfigFile("port", "iqcarecompanion.properties");
                INSTANCE = ResourceManager.readConfigFile("instance", "iqcarecompanion.properties");
                DB_NAME = ResourceManager.readConfigFile("db_name", "iqcarecompanion.properties");
                DB_CONNECTION = "jdbc:jtds:sqlserver://" + HOST + ":" + PORT + ";databaseName=" + DB_NAME + ";instance=" + INSTANCE;
                DB_USER = ResourceManager.readConfigFile("db_user", "iqcarecompanion.properties");
                DB_PASSWORD = ResourceManager.readConfigFile("db_password", "iqcarecompanion.properties");
                windowsAuthentication = ResourceManager.readConfigFile(
                        "windowsAuthentication", "iqcarecompanion.properties");

                Class.forName(DB_DRIVER);

            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, e.toString(), e);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Oooops! The iqcarecompanion.properties file could not be found at " + ResourceManager.confPath, e);
            }

            try {

                if (windowsAuthentication.equals("true")) {
                    dbConnection = DriverManager.getConnection(
                            DB_CONNECTION);
                    logger.log(Level.FINE,
                            "Connection to IQCare database established. Using Windows Authentication");

                } else {
                    dbConnection = DriverManager.getConnection(
                            DB_CONNECTION, DB_USER, DB_PASSWORD);
                    logger.log(Level.FINE,
                            "Connection to IQCare database established. Using SQL Server Authentication");
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
