/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.dao;

import static iqcarecompanion.core.dao.AbstractDaoTest.prepareDb;
import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.jsonmapper.Event;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import org.junit.AfterClass;
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
public class ObservationDaoTest extends AbstractDaoTest {
    private ObservationDao dao;
    
    public ObservationDaoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        prepareDb();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        dao = new ObservationDao(getConnection(), "");
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void GetObervation_EventWithoutTransformations_ObservationReturned() throws Exception {
    
        Event event = new Event("TB_DIAGNOSIS", "dtl_PatientHivPrevCareIE", "longTermTBStartDate", "", "Visit_pk", null);
        Visit visit = new Visit();
        visit.setPatientId(2);
        visit.setVisitId(2);
        visit.setVisitDate(new Date());
        Observation observation = dao.getObservation(event, visit);
        
        assertThat(observation.getObservationName(), is("TB_DIAGNOSIS"));
        assertThat(observation.getObservationValue(), is("2006-01-07"));
        assertThat(observation.getVisit().getPatientId(), is(2));
        assertThat(observation.getVisit().getVisitId(), is(2));
    }
    
    @Test
    public void GetObervation_EventWithTransformations_ObservationReturned() throws Exception {
        String[] transformations = new String[10];
        transformations[0] = "87:1";
        transformations[1] = "88:2";
        transformations[2] = "89:3";
        transformations[3] = "90:4";
        Event event = new Event("WHO_STAGE", "dtl_PatientStage", "WHOStage", "", "Visit_Pk", transformations);
        Visit visit = new Visit();
        visit.setPatientId(2);
        visit.setVisitId(2);
        visit.setVisitDate(new Date());
        Observation observation = dao.getObservation(event, visit);
        
        assertThat(observation.getObservationName(), is("WHO_STAGE"));
        assertThat(observation.getObservationValue(), is("89"));
        assertThat(observation.getVisit().getPatientId(), is(2));
        assertThat(observation.getVisit().getVisitId(), is(2));
    }
    
    @Test
    public void GetObervation_EmptyObservationValue_NullReturned() throws Exception {
        String[] transformations = new String[10];
        transformations[0] = "87:1";
        transformations[1] = "88:2";
        Event event = new Event("WHO_STAGE", "dtl_PatientStage", "WHOStage", "", "Visit_Pk", transformations);
        Visit visit = new Visit();
        visit.setPatientId(3);
        visit.setVisitId(3);
        visit.setVisitDate(new Date());
        Observation observation = dao.getObservation(event, visit);
        
        assertThat(observation, is(nullValue()));
    }
          
    @Test
    public void GetObervation_SingletonEvent_ObservationReturned() throws Exception {
        String[] transformations = new String[1];
        transformations[0] = "2";
        Event event = new Event("HIV_CARE_INITIATION", "Lnk_PatientProgramStart", "ModuleId", "StartDate", "", transformations);
        Visit visit = new Visit();
        visit.setPatientId(2);
        visit.setVisitId(2);
        visit.setVisitDate(new Date());
        Observation observation = dao.getObservation(event, visit);
        
        assertThat(observation.getObservationName(), is("HIV_CARE_INITIATION"));
        assertThat(observation.getObservationValue(), is("2"));
        assertThat(observation.getVisit().getPatientId(), is(2));
        assertThat(observation.getVisit().getVisitId(), is(2));
    }
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();
    
    @Test
    public void GetObservation_InvalidParams_SQLExceptionThrown() throws SQLException, ClassNotFoundException {
        ObservationDao badDao = new ObservationDao(getConnection(), "invalidDBName");
        thrown.expect(SQLException.class);
        thrown.expectMessage("Syntax error in SQL statement");
        String[] transformations = new String[1];
        transformations[0] = "2";
        Event event = new Event("HIV_CARE_INITIATION", "Lnk_PatientProgramStart", "ModuleId", "StartDate", "", transformations);
        badDao.getObservation(event, new Visit());
    }
          
    
}
