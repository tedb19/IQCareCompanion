/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.hapiwrapper;

import ca.uhn.hl7v2.HL7Exception;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.kemricdc.entities.Person;
import org.kemricdc.hapi.oru.OruFiller;
import org.kemricdc.hapi.oru.ProcessTransactions;

/**
 *
 * @author Teddy
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
                String bXString = bXSegment.generateORU();
                createHl7OnDisk(bXString,"ORU-R01");
                logger.log(Level.INFO, bXString);
            } catch (HL7Exception|IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }
    
    private static void createHl7OnDisk(String s, String msgType) throws FileNotFoundException, IOException{
        File hl7File=new File(System.getProperty("user.home")+ File.separator + "IQCare-Companion" + File.separator + "Dumps");
        Random random = new Random();
        if(!hl7File.exists()){
            hl7File.mkdir();
            logger.log(Level.INFO, 
                "Created the folder {0} to be used for hl7 backups",hl7File.getCanonicalPath());
        }

         File f = new File(hl7File, ((random.nextInt(Integer.MAX_VALUE) + 1) + "-" + msgType+".hl7"));
         logger.log(Level.INFO, 
                "Created {0} on disk to be sent to regional server at ..",f.getCanonicalPath());

        try (FileOutputStream fos = new FileOutputStream(f)) {
            byte[] b = s.getBytes();
            fos.write(b);
            fos.flush();
        }
    }
}
