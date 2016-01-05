/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.dao;

import iqcarecompanion.core.entities.LabResult;
import iqcarecompanion.core.entities.LabTest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

/**
 *
 * @author Teddy Odhiambo
 */
public class LabResultDaoTest extends AbstractDaoTest{
    private LabResultDao dao;
    private String lastLabId = "0";
    
    public LabResultDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        prepareDb();
    }
    
    @Before
    public void setUp() throws Exception {
        dao = new LabResultDao(getConnection(), "");
        lastLabId = "0";
    }
    
    @Test
    public void GetLabResults_ValidParams_ListReturned() throws Exception {
        List<LabResult> labResults = dao.getLabResults(5, lastLabId,"");
        for(LabResult labResult:labResults){
            assertThat(labResult.getResult(), is("200.00"));
        }
        assertThat(labResults.size(), is(1));
    }
    
    @Test
    public void GetLabResults_LastLabIdGreaterThanCurrentLabIdInDb_EmptyListReturned() throws Exception {
        List<LabResult> labResults = dao.getLabResults(5, "7", "noneExistentPropertyKey");
        assertTrue(labResults.isEmpty());
    }
    
    @Test
    public void GetLabResults_NoLabResult_EmptyListReturned() throws Exception {
        List<LabResult> labResults = dao.getLabResults(5, "3", "noneExistentPropertyKey");
        assertTrue(labResults.isEmpty());
    }
    
    @Test
    public void GetLabResults_NullLabResult_EmptyListReturned() throws Exception {
        List<LabResult> labResults = dao.getLabResults(5, "7", "noneExistentPropertyKey");
        assertTrue(labResults.isEmpty());
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none(); 
    
    @Test
    public void GetLabResults_InvalidParams_SQLExceptionThrown() throws SQLException, ClassNotFoundException {
        LabResultDao badDao = new LabResultDao(getConnection(), "invalidDBName");
        thrown.expect(SQLException.class);
        thrown.expectMessage("Syntax error in SQL statement");
        badDao.getLabResults(5,lastLabId, "noneExistentPropertyKey");
    }
    
    @Test
    public void SetLabTest_ValidParams_LabResultObjectReturned() throws SQLException{
        ResultSet rsMock = Mockito.mock(ResultSet.class);
        when(rsMock.getInt("LabTestID")).thenReturn(1).thenReturn(2).thenReturn(3);
        LabResult inputLabResult = new LabResult();
        LabResult outputLabResult = dao.setLabTest(rsMock, inputLabResult);
        assertThat(outputLabResult.getLabTest(), is(LabTest.CD4_COUNT));
        assertThat(dao.setLabTest(rsMock, inputLabResult).getLabTest(), is(LabTest.CD4_PERCENT));
        assertThat(dao.setLabTest(rsMock, inputLabResult).getLabTest(), is(LabTest.VIRAL_LOAD));
        
    } 
    
    @Test
    public void SetLabTest_InvalidParams_LabResultObjectReturned() throws SQLException{
        ResultSet rsMock = Mockito.mock(ResultSet.class);
        when(rsMock.getInt("LabTestID")).thenReturn(6);
        LabResult inputLabResult = new LabResult();
        LabResult outputLabResult = dao.setLabTest(rsMock, inputLabResult);
        assertThat(outputLabResult.getLabTest(), is(nullValue()));
    } 
}
