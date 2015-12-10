package iqcarecompanion.core.services;

import hapimodule.core.entities.Person;
import hapimodule.core.hapi.ORUProcessor;
import hapimodule.core.hapi.models.OBXSegment;
import hapimodule.core.utils.Hl7Dump;
import iqcarecompanion.core.dao.LabResultDao;
import static iqcarecompanion.core.dao.PersonDao.getPerson;
import iqcarecompanion.core.entities.LabResult;
import static iqcarecompanion.core.utils.ConstantProperties.DUMPS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.MSH;
import static iqcarecompanion.core.utils.DBConnector.connectionInstance;
import static iqcarecompanion.core.utils.PropertiesManager.readConfigFile;
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
        LabResultDao dao = new LabResultDao(connectionInstance());
        String labResultId = readConfigFile(LAB_RESULT_ID_KEY);
        List<LabResult> labResults = dao.getLabResults(TOTAL_LAB_RESULTS, labResultId);

        if (labResults.isEmpty()) {
            LOGGER.log(Level.SEVERE, "{0} No lab results returned! The last lab Id retrieved from the properties file is {1}",
                    new Object[]{LOG_PREFIX, labResultId});
            return;
        }
        for (LabResult labResult : labResults) {
            if (labResult == null) {
                continue;
            }
            Person person = getPerson(labResult.getVisit().getPatientId());
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

