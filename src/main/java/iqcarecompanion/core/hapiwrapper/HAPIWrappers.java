/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.hapiwrapper;

import ca.uhn.hl7v2.HL7Exception;
import hapimodule.core.entities.PatientSource;
import hapimodule.core.entities.Person;
import hapimodule.core.hapi.ORUProcessor;
import hapimodule.core.hapi.models.MSHModel;
import hapimodule.core.hapi.models.OBXModel;
import iqcarecompanion.core.utils.ConstantProperties;
import static iqcarecompanion.core.utils.ConstantProperties.APPLICATION_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.CDS_APPLICATION_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.CDS_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.FACILITY_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ConstantProperties.MFL_CODE;
import static iqcarecompanion.core.utils.RuntimeDirectory.createHl7Dump;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Teddy Odhiambo
 */
public class HAPIWrappers {
    
    final static Logger logger = Logger.getLogger(HAPIWrappers.class.getName());
    
    public static OBXModel createOBX(String eventName, String eventValue, Date visitDate){
        OBXModel obxModel = null;
        if(!StringUtils.isEmpty(eventName) && !StringUtils.isEmpty(eventValue)){
            obxModel = new OBXModel();
            obxModel.setObservationIdentifier(null);
            obxModel.setCodingSystem("ICD10");
            obxModel.setObservationSubId(null);
            obxModel.setUnits(null);
            obxModel.setResultStatus("P");
            obxModel.setDateTimeOfObservation(visitDate);
            obxModel.setObservationIdentifierText(eventName);
            obxModel.setObservationValue(eventValue);
        }
        return obxModel;
    }

    
    public static void generateORUMsg(Person person, PatientSource patientSource, List<OBXModel> obxModels){
        String triggerEvent = "ORU-R01";
        MSHModel msh = new MSHModel(APPLICATION_NAME, FACILITY_NAME, MFL_CODE, CDS_NAME, CDS_APPLICATION_NAME);
        if(!obxModels.isEmpty()){
            try {
                ORUProcessor oruProcessor = new ORUProcessor(person, patientSource, obxModels, msh);
                
                String hl7 = oruProcessor.generateORU();
                oruProcessor.generateORU();
                
                createHl7Dump(hl7,triggerEvent);
                logger.log(Level.INFO, "{0} {1}", new Object[]{LOG_PREFIX, hl7});
            } catch (IOException ex) {
                StringBuilder sb = new StringBuilder();
                sb.append(LOG_PREFIX)
                        .append(" An error occurred during the generation of the hl7 file:\n");
                logger.log(Level.SEVERE,sb.toString(),ex);
            }
        }
    }
}
