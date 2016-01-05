
package iqcarecompanion.core.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import static org.h2.engine.Constants.UTF8;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;

/**
 *
 * @author Teddy Odhiambo
 */
public class AbstractDaoTest {
    protected static final Logger dbUnit = Logger.getLogger("org.dbunit");
    private static final String JDBC_DRIVER = org.h2.Driver.class.getName();
    private static final String JDBC_URL = "jdbc:h2:mem:IQCare;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String RESOURCES_PATH = "src/test/resources/";
    
    public static void prepareDb() throws Exception{
        dbUnit.setLevel(Level.SEVERE);
        createSchema();
        importDataSet();
    }
    
    public static void createSchema() throws Exception{
        RunScript.execute(JDBC_URL, USER,PASSWORD,
                          RESOURCES_PATH+"schema.sql",
                          UTF8, false);
    }
    public static void importDataSet() throws Exception {
	cleanlyInsertDataset(readDataSet());
    }
    
    private static IDataSet readDataSet() throws Exception {
	return new FlatXmlDataSetBuilder().build(new File(RESOURCES_PATH + "dataset.xml"));
    }
    
    private static void cleanlyInsertDataset(IDataSet dataSet) throws Exception {
	IDatabaseTester databaseTester = new JdbcDatabaseTester(JDBC_DRIVER, JDBC_URL, USER, PASSWORD);
	databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	databaseTester.setDataSet(dataSet);
	databaseTester.onSetup();
    }
    
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(JDBC_URL);
        ds.setUser(USER);
        ds.setPassword(PASSWORD);
        return ds.getConnection();
    }
}
