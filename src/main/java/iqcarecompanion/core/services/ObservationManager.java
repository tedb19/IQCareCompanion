package iqcarecompanion.core.services;

import iqcarecompanion.core.dao.VisitDao;
import iqcarecompanion.core.entities.Visit;
import static iqcarecompanion.core.services.VisitManager.generateVisitHl7s;
import static iqcarecompanion.core.utils.ConstantProperties.SENTINEL_EVENTS;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
import static iqcarecompanion.core.utils.PropertiesManager.readConfigFile;
import java.util.List;

/**
 *
 * @author Teddy Odhiambo
 */
public class ObservationManager {

    private static final String LAST_VISIT_ID_KEY = "last_visit_id";
    private static final int TOTAL_VISITS = 100;
    
    private ObservationManager(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    
    public static void mineEvents() {
        VisitDao dao = new VisitDao(connectionInstance());
        String lastVisitId = readConfigFile(LAST_VISIT_ID_KEY);
        List<Visit> visits = dao.getVisits(TOTAL_VISITS, lastVisitId);
        generateVisitHl7s(visits, SENTINEL_EVENTS);
    }
}
