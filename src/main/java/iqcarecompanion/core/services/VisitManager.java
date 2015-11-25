package iqcarecompanion.core.services;

import static iqcarecompanion.core.domain.ObservationFactory.getObservation;
import static iqcarecompanion.core.domain.PersonFactory.getPerson;
import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.hapiwrapper.HAPIWrappers;
import iqcarecompanion.core.jsonMapper.Event;
import iqcarecompanion.core.utils.ResourceManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kemricdc.entities.Person;
import org.kemricdc.hapi.oru.OruFiller;

/**
 *
 * @author Teddy Odhiambo
 */
public class VisitManager {

    final static Logger logger = Logger.getLogger(VisitManager.class.getName());

    public static void generateVisitHl7s(List<Visit> visits, List<Event> events) {

        if (!visits.isEmpty()) {
            for (Visit visit : visits) {
                generateVisitHl7(visit, events);
            }
        }
    }

    private static void generateVisitHl7(Visit visit, List<Event> events) {
        if (visit != null) {
            List<OruFiller> fillers = new ArrayList<>();
            Person person = null;
            try {
                Observation observation;
                String dbName = ResourceManager.readConfigFile("db_name", "iqcarecompanion.properties");
                String symmetricKey = ResourceManager.readConfigFile("symmetric_key", "iqcarecompanion.properties");
                String enc_password = ResourceManager.readConfigFile("enc_password", "iqcarecompanion.properties");
                
                person = getPerson(visit.getPatientId(), symmetricKey, dbName, enc_password);
                if (person != null) {
                    for (Event event : events) {
                        observation = getObservation(event, visit);
                        if (observation != null && observation.getClass() == Observation.class) {
                            OruFiller filler = HAPIWrappers.createOBX(observation.getObservationName(),
                                    observation.getObservationValue(),
                                    visit.getVisitDate());
                            if (filler != null) {
                                fillers.add(filler);
                            }
                        }
                    }
                }
            } catch (SQLException|IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
            }
            if (!fillers.isEmpty() && person != null) {
                HAPIWrappers.generateORUMsg(fillers, person);
            }
        }

    }
}
