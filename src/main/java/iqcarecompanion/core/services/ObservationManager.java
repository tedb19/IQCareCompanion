package iqcarecompanion.core.services;

import static iqcarecompanion.core.domain.VisitFactory.getVisits;
import java.util.List;
import java.util.logging.Logger;
import iqcarecompanion.core.jsonMapper.Event;
import static iqcarecompanion.core.services.VisitManager.generateVisitHl7s;
import iqcarecompanion.core.utils.ResourceManager;

/**
 *
 * @author Teddy Odhiambo
 */
public class ObservationManager {

    final static Logger logger = Logger.getLogger(ObservationManager.class.getName());
    final static int TOTAL_VISITS = 100;
    
    public static void mineEvents() {
        String lastVisitId = ResourceManager.readConfigFile("last_visit_id");
        List<Event> events = ResourceManager.readJSONFile();
        generateVisitHl7s(getVisits(TOTAL_VISITS, lastVisitId), events);
    }

}
