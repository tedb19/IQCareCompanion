package iqcarecompanion.core.services;

import iqcarecompanion.core.dao.VisitDao;
import iqcarecompanion.core.entities.Visit;
import static iqcarecompanion.core.services.VisitManager.generateVisitHl7s;
import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.SENTINEL_EVENTS;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
import static iqcarecompanion.core.utils.PropertiesManager.readConfigFile;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class ObservationManager {

    private static final String LAST_VISIT_ID_KEY = "last_visit_id";
    private static final Logger LOGGER = Logger.getLogger(ObservationManager.class.getName());
    private static final int TOTAL_VISITS = 100;
    
    private ObservationManager(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    
    public static void mineEvents() {
        VisitDao dao = new VisitDao(connectionInstance());
        String lastVisitId = readConfigFile(LAST_VISIT_ID_KEY);
        List<Visit> visits = new ArrayList<>();
        try {
            visits = dao.getVisits(TOTAL_VISITS, lastVisitId, LAST_VISIT_ID_KEY, DB_NAME);
            
        } catch (SQLException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append("An error occurred while fetching the visits.")
                    .append(" The last visit id as recorded on properties file is: {1}\n")
                    .append(lastVisitId);
            LOGGER.log(Level.SEVERE, sb.toString() , ex);
        }
        generateVisitHl7s(visits, SENTINEL_EVENTS);
        
    }
}
