package iqcarecompanion.core.services;

import static iqcarecompanion.core.domain.LabResultFactory.getLabResults;
import static iqcarecompanion.core.domain.PersonFactory.getPerson;
import iqcarecompanion.core.hapiwrapper.HAPIWrappers;
import iqcarecompanion.core.entities.LabResult;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import iqcarecompanion.core.utils.ResourceManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.kemricdc.entities.Person;
import org.kemricdc.hapi.oru.OruFiller;

/**
 *
 * @author Teddy Odhiambo
 */
public class LabManager {

    final static Logger logger = Logger.getLogger(LabManager.class.getName());
    final static int TOTAL_LAB_RESULTS = 100;
    
    public static void generateLabResultsORU() {
        try {
            String labResultId = ResourceManager.readConfigFile("labResultId");
        
            List<LabResult> labResults = getLabResults(TOTAL_LAB_RESULTS, labResultId);

            if (!labResults.isEmpty()) {
                for (LabResult labResult : labResults) {
                    if (labResult != null) {
                        Person person = getPerson(labResult.getVisit().getPatientId());
                        OruFiller filler = HAPIWrappers.createOBX(labResult.getLabTest().getValue(), labResult.getResult(), labResult.getVisit().getVisitDate());
                        List<OruFiller> fillers = new ArrayList<>();
                        fillers.add(filler);
                        HAPIWrappers.generateORUMsg(fillers, person);
                    }
                }
            }

        } catch (SQLException | IOException ex) {
            logger.log(Level.SEVERE, "{0} {1}", new Object[]{LOG_PREFIX, ex});
        }
    }

}
