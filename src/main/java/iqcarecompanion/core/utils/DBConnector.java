package iqcarecompanion.core.utils;

import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.DB_PASSWORD;
import static iqcarecompanion.core.utils.ConstantProperties.DB_USER;
import static iqcarecompanion.core.utils.ConstantProperties.HOST;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.PORT;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.jtds.jdbcx.JtdsDataSource;

/**
 *
 * @author Teddy Odhiambo
 */
public class DBConnector {

    private static final Logger LOGGER = Logger.getLogger(DBConnector.class.getName());
    private static Connection dbConnection;
    
    private DBConnector(){}
    
    public static Connection connectionInstance() {
        if (dbConnection == null) {
            try {
                // Establish the connection. 
                JtdsDataSource ds = new JtdsDataSource();
                ds.setUser(DB_USER);
                ds.setPassword(DB_PASSWORD);
                ds.setServerName(HOST);
                ds.setPortNumber(Integer.parseInt(PORT)); 
                ds.setDatabaseName(DB_NAME);
                dbConnection = ds.getConnection();
                LOGGER.log(Level.INFO,
                    "{0} Database connection established with the URL: {1}",
                    new Object[]{LOG_PREFIX, dbConnection.getMetaData().getURL()});
                
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "{0} {1}", new Object[]{LOG_PREFIX, e});
            }
        }
        return dbConnection;
    }
    
}
