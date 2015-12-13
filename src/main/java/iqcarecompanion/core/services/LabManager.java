package iqcarecompanion.core.services;

import hapimodule.core.entities.Person;
import hapimodule.core.hapi.ORUProcessor;
import hapimodule.core.hapi.models.OBXSegment;
import hapimodule.core.utils.Hl7Dump;
import iqcarecompanion.core.dao.LabResultDao;
import iqcarecompanion.core.dao.PersonDao;
import iqcarecompanion.core.entities.LabResult;
import static iqcarecompanion.core.utils.ConstantProperties.DB_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.DUMPS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.MSH;
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
public class LabManager {
    
    private static final int TOTAL_LAB_RESULTS = 100;
    private static final String LAB_RESULT_ID_KEY = "labResultId";
    private static final Logger LOGGER = Logger.getLogger(LabManager.class.getName());
    
    private LabManager(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    
    public static void mineLabResults() {
        Person person = null;
        LabResultDao dao = new LabResultDao(connectionInstance());
        PersonDao personDao = new PersonDao(connectionInstance());
        String labResultId = readConfigFile(LAB_RESULT_ID_KEY);
        List<LabResult> labResults = new ArrayList<>();
        
        try {
            labResults = dao.getLabResults(
                    TOTAL_LAB_RESULTS,
                    labResultId,
                    LAB_RESULT_ID_KEY,
                    DB_NAME
            );
        } catch (SQLException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append("An error occurred while fetching the lab results.")
                    .append(" The last lab result as recorded on properties file is: {1}\n")
                    .append(labResultId);
            LOGGER.log(Level.SEVERE, sb.toString() , ex);
        }

        if (labResults.isEmpty()) {
            return;
        }
        for (LabResult labResult : labResults) {
            if (labResult == null) {
                continue;
            }
            try {
                person = personDao.getPerson(labResult.getVisit().getPatientId());
                if (person == null) {
                    continue;
                }
            } catch (SQLException ex) {
                StringBuilder sb = new StringBuilder();
                sb.append(LOG_PREFIX)
                    .append("An error occurred while fetching the patient demographic details.\n");
                LOGGER.log(Level.SEVERE, sb.toString(), ex);
            }
            OBXSegment obxSegment = new OBXSegment(
                    labResult.getLabTest().getValue(),
                    labResult.getResult(),
                    labResult.getVisit().getVisitDate()
            );
            List<OBXSegment> obxSegments = new ArrayList<>();
            obxSegments.add(obxSegment);

            ORUProcessor oruProcessor = new ORUProcessor(
                    person,
                    obxSegments,
                    MSH
            );
            Hl7Dump.dumpORU(oruProcessor, LOG_PREFIX, DUMPS_DIR);
        }
    }
}

