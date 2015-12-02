/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.hapiwrapper;

import ca.uhn.hl7v2.HL7Exception;
import iqcarecompanion.core.utils.ConstantProperties;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.RuntimeDirectory.createHl7Dump;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.kemricdc.entities.Person;
import org.kemricdc.hapi.oru.OruFiller;
import org.kemricdc.hapi.oru.ProcessTransactions;

/**
 *
 * @author Teddy Odhiambo
 */
public class HAPIWrappers {
    
    final static Logger logger = Logger.getLogger(HAPIWrappers.class.getName());
    
    public static OruFiller createOBX(String eventName, String eventValue, Date visitDate){
        OruFiller filler = null;
        if(!StringUtils.isEmpty(eventName) && !StringUtils.isEmpty(eventValue)){
            filler = new OruFiller();
            filler.setObservationIdentifier(null);
            filler.setCodingSystem("ICD10");
            filler.setObservationSubId(null);
            filler.setUnits(null);
            filler.setResultStatus("P");
            filler.setDateTimeOfObservation(visitDate);
            filler.setObservationIdentifierText(eventName);
            filler.setObservationValue(eventValue);
        }
        return filler;
    }

    
    public static void generateORUMsg(List<OruFiller> fillers, Person person){
        if(!fillers.isEmpty()){
            try {
                ProcessTransactions bXSegment = new ProcessTransactions(person,fillers);
                
                String bXString = bXSegment.generateORU(
                        ConstantProperties.APPLICATION_NAME,
                        ConstantProperties.FACILITY_NAME,
                        ConstantProperties.MFL_CODE,
                        ConstantProperties.CDS_NAME,
                        ConstantProperties.CDS_APPLICATION_NAME
                );
                
                createHl7Dump(bXString,"ORU-R01");
                logger.log(Level.INFO, "{0} {1}", new Object[]{LOG_PREFIX, bXString});
            } catch (HL7Exception|IOException ex) {
                logger.log(Level.SEVERE, "{0} {1}", new Object[]{LOG_PREFIX, ex});
            }
        }
    }
}
