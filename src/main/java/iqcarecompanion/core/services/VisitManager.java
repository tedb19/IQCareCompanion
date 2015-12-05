package iqcarecompanion.core.services;

import static iqcarecompanion.core.domain.ObservationFactory.getObservation;
import static iqcarecompanion.core.domain.PersonFactory.getPerson;
import iqcarecompanion.core.entities.Observation;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.hapiwrapper.HAPIWrappers;
import iqcarecompanion.core.jsonMapper.Event;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import hapimodule.core.entities.Person;
import hapimodule.core.hapi.models.OBXModel;

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
            List<OBXModel> fillers = new ArrayList<>();
            Person person;
            Observation observation;
            person = getPerson(visit.getPatientId());
            if (person != null) {
                for (Event event : events) {
                    observation = getObservation(event, visit);
                    if (observation != null && observation.getClass() == Observation.class) {
                        OBXModel filler = HAPIWrappers.createOBX(observation.getObservationName(),
                                observation.getObservationValue(),
                                visit.getVisitDate());
                        if (filler != null) {
                            fillers.add(filler);
                        }
                    }
                }
            }
            if (!fillers.isEmpty() && person != null) {
                HAPIWrappers.generateORUMsg(person, null, fillers);
            }
        }

    }
}
