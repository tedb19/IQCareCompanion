/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.dao;

import iqcarecompanion.core.entities.Visit;
import java.sql.SQLException;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Teddy Odhiambo
 */
public class VisitDaoTest extends AbstractDaoTest{
    
    private VisitDao dao;
    public VisitDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        prepareDb();
    }
    
    @Before
    public void setUp() throws Exception {
        dao = new VisitDao(getConnection(),"");
    }
    
        
    @Rule
    public ExpectedException thrown = ExpectedException.none(); 
    
    @Test
    public void GetVisits_InvalidParams_SQLExceptionThrown() throws SQLException, ClassNotFoundException {
        
        thrown.expect(SQLException.class);
        thrown.expectMessage("Syntax error in SQL statement");
        VisitDao badDao = new VisitDao(getConnection(), "zzz");
        badDao.getVisits(100, "0", "");
    }
    
    @Test 
    public void GetVisits_ValidParams_VisitsReturned() throws Exception {
        List<Visit> visits = dao.getVisits(100, "0", "");
        assertThat(visits.size(), is(2));
    }
    
    @Test 
    public void GetVisits_VisitIdGreaterThanCurrentVisitIdInDb_EmptyListReturned() throws Exception {
        List<Visit> visits = dao.getVisits(6, "6","");
        assertTrue(visits.isEmpty());
    }
    
    @Test 
    public void GetVisit_ValidParams_VisitReturned() throws Exception {
        Visit visit = dao.getVisit(1);
        assertThat(visit.getPatientId(), is(1));
        assertThat(visit.getVisitId(), is(1));
        assertThat(visit.getVisitDate(), is(not(nullValue())));
    }
    
    @Test 
    public void GetVisit_NonExistentVisitIdParams_NullReturned() throws Exception {
        Visit visit = dao.getVisit(6);
        assertThat(visit, is(nullValue()));
    }
    
    @Test
    public void GetVisit_InvalidParams_SQLExceptionThrown() throws SQLException, ClassNotFoundException {
        thrown.expect(SQLException.class);
        thrown.expectMessage("Syntax error in SQL statement");
        VisitDao badDao = new VisitDao(getConnection(), "zzz");
        badDao.getVisit(6);
    }    
}
