package iqcarecompanion.core.facade;

import iqcarecompanion.core.dao.ObservationDao;
import iqcarecompanion.core.dao.PersonDao;
import iqcarecompanion.core.dao.VisitDao;
import iqcarecompanion.core.entities.Visit;
import iqcarecompanion.core.services.VisitService;
import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.SENTINEL_EVENTS;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
import static iqcarecompanion.core.utils.PropertiesManager.readConfigFile;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class VisitFacade {

    private static final String LAST_VISIT_ID_KEY = "last_visit_id";
    private static final Logger LOGGER = Logger.getLogger(VisitFacade.class.getName());
    private static final int TOTAL_VISITS = 100;
    
    private VisitFacade(){
        
    }
    
    public static void mineEvents() {
        String lastVisitId = readConfigFile(LAST_VISIT_ID_KEY);
        Connection connection = connectionInstance();
        VisitDao visitDao = new VisitDao(connection, DB_NAME);
        ObservationDao obsDao = new ObservationDao(connection, DB_NAME);
        PersonDao personDao = new PersonDao(connection, DB_NAME);
        VisitService service = new VisitService(obsDao, personDao);
        List<Visit> visits = new ArrayList<>();
        try {
            visits = visitDao.getVisits(TOTAL_VISITS, lastVisitId, LAST_VISIT_ID_KEY);
            
        } catch (SQLException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append("An error occurred while fetching the visits.")
                    .append(" The last visit id as recorded on properties file is: {1}\n")
                    .append(lastVisitId);
            LOGGER.log(Level.SEVERE, sb.toString() , ex);
        }
        service.generateVisitHl7s(visits, SENTINEL_EVENTS);
    }
    
    
}
