package iqcarecompanion.core.services;

import hapimodule.core.entities.Person;
import hapimodule.core.hapi.ORUProcessor;
import hapimodule.core.hapi.models.OBXSegment;
import hapimodule.core.utils.Hl7Dump;
import iqcarecompanion.core.dao.ObservationDao;
import static iqcarecompanion.core.dao.PersonDao.getPerson;
import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.jsonmapper.Event;
import static iqcarecompanion.core.utils.ConstantProperties.DUMPS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.MSH;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Teddy Odhiambo
 */
public class VisitManager {

    private VisitManager(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    
    public static void generateVisitHl7s(List<Visit> visits, List<Event> events) {

        if (!visits.isEmpty()) {
            for (Visit visit : visits) {
                generateVisitHl7(visit, events);
            }
        }
    }

    private static void generateVisitHl7(Visit visit, List<Event> events) {
        ObservationDao dao = new ObservationDao(connectionInstance());
        if (visit == null) {
            return;
        }
        List<OBXSegment> obxSegments = new ArrayList<>();
        Person person;
        Observation observation;
        person = getPerson(visit.getPatientId());
        if (person == null) {
            return;
        }
        for (Event event : events) {
            observation = dao.getObservation(event, visit);
            if (observation == null) {
                continue;
            }
            OBXSegment obxSegment = new OBXSegment(
                    observation.getObservationName(), 
                    observation.getObservationValue(),
                    visit.getVisitDate()
            );
            obxSegments.add(obxSegment);
        }
        
        ORUProcessor oruProcessor = new ORUProcessor(
                    person,
                    obxSegments,
                    MSH
        );
        Hl7Dump.dumpORU(oruProcessor, LOG_PREFIX, DUMPS_DIR);
    }
}

