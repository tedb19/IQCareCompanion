package iqcarecompanion.core.services;

import hapimodule.core.entities.Person;
import hapimodule.core.hapi.ORUProcessor;
import hapimodule.core.hapi.models.OBXSegment;
import hapimodule.core.utils.Hl7Dump;
import iqcarecompanion.core.dao.ObservationDao;
import iqcarecompanion.core.dao.PersonDao;
import iqcarecompanion.core.dao.RegimenDao;
import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.jsonmapper.Event;
import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.DUMPS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.MSH;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class VisitService {

    private static final Logger LOGGER = Logger.getLogger(LabService.class.getName());
    private final ObservationDao observationDao;
    private final PersonDao personDao;
    
    public VisitService(ObservationDao observationDao, PersonDao personDao){
        this.observationDao = observationDao;
        this.personDao = personDao;
    }
    
    public void generateVisitHl7s(List<Visit> visits, List<Event> events) {

        if (!visits.isEmpty()) {
            for (Visit visit : visits) {
                generateVisitHl7(visit, events);
            }
        }
    }

    private void generateVisitHl7(Visit visit, List<Event> events) {
        Person person = null;
        
        if (visit == null) {
            return;
        }
        List<OBXSegment> obxSegments = new ArrayList<>();
        Observation observation = new Observation();
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
            try {
                observation = observationDao.getObservation(event, visit);
                if (observation == null) {
                    continue;
                }
            } catch (SQLException ex) {
                StringBuilder sb = new StringBuilder();
                sb.append(LOG_PREFIX)
                    .append("An error occurred while fetching the observations for visit id ")
                    .append(visit.getVisitId());
                LOGGER.log(Level.SEVERE, sb.toString(), ex);
            }
            observation.setTransformations(event);
            observation = checkRegimenEvent(event, observation);
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
    
    private static Observation checkRegimenEvent(Event event, Observation observation){
        if (StringUtils.equals(event.eventName, "FIRST_LINE_REGIMEN") ||
                StringUtils.equals(event.eventName, "SECOND_LINE_REGIMEN")) {
            RegimenDao regimenDao = new RegimenDao(connectionInstance(), DB_NAME);
            String regimen = null;
            try {
                regimen = regimenDao.getCurrentRegimen(observation.getVisit().getVisitId());
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "An error occurred while checking the regimen for this patient:" , ex);
            }
            observation.setObservationValue(regimen);
        }
        return observation;
    }
}

