
package iqcarecompanion.core.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import static org.h2.engine.Constants.UTF8;
import org.h2.tools.RunScript;

/**
 *
 * @author Teddy Odhiambo
 */
public class AbstractDaoTest {
    private static final String JDBC_DRIVER = org.h2.Driver.class.getName();
    private static final String JDBC_URL = "jdbc:h2:mem:IQCare;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String RESOURCES_PATH = "src/test/resources/";
    
    public static void createSchema() throws Exception{
        RunScript.execute(JDBC_URL, USER,PASSWORD,
                          RESOURCES_PATH+"schema.sql",
                          UTF8, false);
    }
    public void importDataSet() throws Exception {
	IDataSet dataSet = readDataSet();
	cleanlyInsertDataset(dataSet);
    }
    
    private IDataSet readDataSet() throws Exception {
	return new FlatXmlDataSetBuilder().build(new File(RESOURCES_PATH + "dataset.xml"));
    }
    
    private void cleanlyInsertDataset(IDataSet dataSet) throws Exception {
	IDatabaseTester databaseTester = new JdbcDatabaseTester(JDBC_DRIVER, JDBC_URL, USER, PASSWORD);
	databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
	databaseTester.setDataSet(dataSet);
	databaseTester.onSetup();
    }
    
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
}
