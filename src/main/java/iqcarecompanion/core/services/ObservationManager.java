package iqcarecompanion.core.services;

import static iqcarecompanion.core.domain.VisitFactory.getVisits;
import static iqcarecompanion.core.services.VisitManager.generateVisitHl7s;
import static iqcarecompanion.core.utils.ConstantProperties.SENTINEL_EVENTS;
import static iqcarecompanion.core.utils.PropertiesManager.readConfigFile;
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
        generateVisitHl7s(getVisits(TOTAL_VISITS, lastVisitId), SENTINEL_EVENTS);
    }
}
