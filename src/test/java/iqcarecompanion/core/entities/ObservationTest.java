/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package iqcarecompanion.core.entities;

import iqcarecompanion.core.jsonmapper.Event;
import java.util.Date;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
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
public class ObservationTest {
    Observation instance = new Observation();
    
    public ObservationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        
        Visit visit = new Visit();
        visit.setPatientId(3);
        visit.setVisitId(3);
        visit.setVisitDate(new Date());
        instance.setObservationName("WHO_STAGE");
        instance.setObservationValue("90");
        instance.setEventDate("2015-08-20 14:45:18.907");
        instance.setObservationDate("2015-10-20 14:45:18.907");
        instance.setVisit(visit);
        
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testObservationObject() {
        assertThat(instance.getObservationDate(), is("2015-10-20 14:45:18.907"));
        assertThat(instance.getObservationName(), is("WHO_STAGE"));
        assertThat(instance.getEventDate(), is("2015-08-20 14:45:18.907"));
        assertThat(instance.getObservationValue(), is("90"));
        assertThat(instance.getVisit().getPatientId(), is(3));
        assertThat(instance.getVisit().getVisitId(), is(3));
    }
    
    @Test
    public void testToString() {
        String expectedValue = "WHO_STAGE 90";
        assertThat(instance.toString(), is(expectedValue));
    }

    @Test
    public void SetTransformations_NoTransformations_ObservationValueUnchanged() {
        String[] transformations = new String[0];
        Event event = new Event("WHO_STAGE", "dtl_PatientStage", "WHOStage", "", "Visit_Pk", transformations);
        String originalValue = instance.getObservationValue();
        instance.setTransformations(event);
        String currentValue = instance.getObservationValue();
        assertThat(originalValue, is(currentValue));
    }
    
    @Test
    public void SetTransformations_TransformationsListed_ObservationValueTransformed() {
        String originalValue = instance.getObservationValue();
        String[] transformations = new String[4];
        transformations[0] = "87:1";
        transformations[1] = "88:2";
        transformations[2] = "89:3";
        transformations[3] = "90:4";
        Event event = new Event("WHO_STAGE", "dtl_PatientStage", "WHOStage", "", "Visit_Pk", transformations);
        instance.setTransformations(event);
        String currentValue = instance.getObservationValue();
        assertThat(currentValue, is(allOf(is("4"),not(originalValue))));
    }
    
    @Test
    public void SetTransformations_SingleTransformationListed_ObservationValueTransformed() {
        String originalValue = instance.getObservationValue();
        String[] transformations = new String[1];
        transformations[0] = "1";
        Event event = new Event("WHO_STAGE", "dtl_PatientStage", "WHOStage", "", "Visit_Pk", transformations);
        instance.setTransformations(event);
        String currentValue = instance.getObservationValue();
        assertThat(currentValue, is(allOf(is(instance.getEventDate()),not(originalValue))));
    }
    
}
