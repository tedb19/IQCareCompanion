package iqcarecompanion.core.services;

import static iqcarecompanion.core.domain.VisitFactory.getVisits;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import iqcarecompanion.core.jsonMapper.Event;
import static iqcarecompanion.core.services.VisitManager.generateVisitHl7s;
import iqcarecompanion.core.utils.ResourceManager;
import java.io.IOException;

/**
 *
 * @author Teddy Odhiambo
 */
public class ObservationManager {

    final static Logger logger = Logger.getLogger(ObservationManager.class.getName());
    final static int TOTAL_VISITS = 100;
    
    public static void mineEvents() {
        try {
            String lastVisitId = ResourceManager.readConfigFile("last_visit_id", "runtime.properties");
            String dbName = ResourceManager.readConfigFile("db_name", "iqcarecompanion.properties");
            List<Event> events = ResourceManager.readJSONFile("events.txt");
            generateVisitHl7s(getVisits(TOTAL_VISITS, lastVisitId, dbName), events);
        } catch (SQLException | IOException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
        }
    }

}