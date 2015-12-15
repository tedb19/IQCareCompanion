package iqcarecompanion.core.services;

import hapimodule.core.entities.Person;
import hapimodule.core.hapi.ORUProcessor;
import hapimodule.core.hapi.models.OBXSegment;
import hapimodule.core.utils.Hl7Dump;
import iqcarecompanion.core.dao.LabResultDao;
import iqcarecompanion.core.dao.PersonDao;
import iqcarecompanion.core.entities.LabResult;
import static iqcarecompanion.core.utils.ConstantProperties.DUMPS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.MSH;
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
public class LabService {
    
    private static final int TOTAL_LAB_RESULTS = 100;
    private static final String LAB_RESULT_ID_KEY = "labResultId";
    private static final Logger LOGGER = Logger.getLogger(LabService.class.getName());
    private final LabResultDao labResultDao;
    private final PersonDao personDao;
    
    public LabService(LabResultDao labResultDao, PersonDao personDao){
        this.labResultDao = labResultDao;
        this.personDao = personDao;
    }
    
    public void mineLabResults() {
        Person person = null;
        String labResultId = readConfigFile(LAB_RESULT_ID_KEY);
        List<LabResult> labResults = new ArrayList<>();
        
        try {
            labResults = labResultDao.getLabResults(
                    TOTAL_LAB_RESULTS,
                    labResultId,
                    LAB_RESULT_ID_KEY
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

