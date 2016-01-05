/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.sql.Connection;
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
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Ted
 */
public class DBConnectorTest {
    
    public DBConnectorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testConstructorIsPrivate() throws Exception {
      Constructor constructor = DBConnector.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      constructor.newInstance();
    }
    
    @Test
    @Ignore
    public void connectionInstance_ValidConnectionReturned() throws Exception {
      Connection connection = DBConnector.connectionInstance();
      assertThat(connection, is(allOf(instanceOf(Connection.class), is(not(nullValue())))));
    }
    
}
