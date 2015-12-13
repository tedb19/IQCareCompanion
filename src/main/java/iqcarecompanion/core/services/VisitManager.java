package iqcarecompanion.core.services;

import hapimodule.core.entities.Person;
import hapimodule.core.hapi.ORUProcessor;
import hapimodule.core.hapi.models.OBXSegment;
import hapimodule.core.utils.Hl7Dump;
import iqcarecompanion.core.dao.ObservationDao;
import iqcarecompanion.core.dao.PersonDao;
import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.jsonmapper.Event;
import static iqcarecompanion.core.utils.ConstantProperties.DUMPS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.MSH;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class VisitManager {

    private static final Logger LOGGER = Logger.getLogger(LabManager.class.getName());
    
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
        Person person = null;
        ObservationDao dao = new ObservationDao(connectionInstance());
        PersonDao personDao = new PersonDao(connectionInstance());
        
        if (visit == null) {
            return;
        }
        List<OBXSegment> obxSegments = new ArrayList<>();
        Observation observation;
        try {
            person = personDao.getPerson(visit.getPatientId());
            if (person == null) {
                return;
            }
        } catch (SQLException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                .append("An error occurred while fetching the patient demographic details.\n");
            LOGGER.log(Level.SEVERE, sb.toString(), ex);
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

