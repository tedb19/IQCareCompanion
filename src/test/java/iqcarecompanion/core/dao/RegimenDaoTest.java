/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.dao;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import static org.h2.engine.Constants.UTF8;
import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.RunScript;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ted
 */
public class RegimenDaoTest {
    
    private static final String JDBC_DRIVER = org.h2.Driver.class.getName();
    private static final String JDBC_URL = "jdbc:h2:mem:IQCare;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";
    private static final String RESOURCES_PATH = "src/test/resources/";
    RegimenDao dao;
    
    public RegimenDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        createSchema();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        importDataSet();
        dao = new RegimenDao(getConnection());
    }
    
    @After
    public void tearDown() {
    }
    
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
    
    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(JDBC_DRIVER);
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }
    
    @Test
    public void GetCurrentRegimen_ExistingVisitIdParameter_RegimenReturned() throws Exception {
        String regimen = dao.getCurrentRegimen(123,"");
        assertThat(regimen, is(allOf(not(nullValue()),instanceOf(String.class), is("D4T/3TC/NVP"))));
    }
    
    @Test
    public void GetCurrentRegimen_NoneExistantVisitIdParameter_EmptyStringReturned() throws Exception {
        String regimen = dao.getCurrentRegimen(111,"");
        assertThat(regimen, is(allOf(not(nullValue()),instanceOf(String.class), is(""))));
    }
}
