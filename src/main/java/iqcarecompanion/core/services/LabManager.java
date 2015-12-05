package iqcarecompanion.core.services;

import static iqcarecompanion.core.domain.LabResultFactory.getLabResults;
import static iqcarecompanion.core.domain.PersonFactory.getPerson;
import iqcarecompanion.core.entities.LabResult;
import iqcarecompanion.core.hapiwrapper.HAPIWrappers;
import static iqcarecompanion.core.utils.PropertiesManager.readConfigFile;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import hapimodule.core.entities.Person;
import hapimodule.core.hapi.models.OBXModel;

/**
 *
 * @author Teddy Odhiambo
 */
public class LabManager {

    final static Logger logger = Logger.getLogger(LabManager.class.getName());
    final static int TOTAL_LAB_RESULTS = 100;
    final static String LAB_RESULT_ID_KEY = "labResultId";
    
    public static void generateLabResultsORU() {
        String labResultId = readConfigFile(LAB_RESULT_ID_KEY);

        List<LabResult> labResults = getLabResults(TOTAL_LAB_RESULTS, labResultId);

        if (!labResults.isEmpty()) {
            for (LabResult labResult : labResults) {
                if (labResult != null) {
                    Person person = getPerson(labResult.getVisit().getPatientId());
                    OBXModel filler = HAPIWrappers.createOBX(labResult.getLabTest().getValue(), labResult.getResult(), labResult.getVisit().getVisitDate());
                    List<OBXModel> fillers = new ArrayList<>();
                    fillers.add(filler);
                    HAPIWrappers.generateORUMsg(person, null, fillers);
                }
            }
        }  
    }
}
