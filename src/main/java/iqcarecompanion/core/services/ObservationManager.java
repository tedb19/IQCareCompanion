package iqcarecompanion.core.services;

import static iqcarecompanion.core.domain.VisitFactory.getVisits;
import iqcarecompanion.core.entities.Visit;
import static iqcarecompanion.core.services.VisitManager.generateVisitHl7s;
import static iqcarecompanion.core.utils.ConstantProperties.SENTINEL_EVENTS;
import static iqcarecompanion.core.utils.PropertiesManager.readConfigFile;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class ObservationManager {

    final static Logger logger = Logger.getLogger(ObservationManager.class.getName());
    final static String LAST_VISIT_ID_KEY = "last_visit_id";
    final static int TOTAL_VISITS = 100;
    
    public static void mineEvents() {
        String lastVisitId = readConfigFile(LAST_VISIT_ID_KEY);
        List<Visit> visits = getVisits(TOTAL_VISITS, lastVisitId);
        generateVisitHl7s(visits, SENTINEL_EVENTS);
    }
}
