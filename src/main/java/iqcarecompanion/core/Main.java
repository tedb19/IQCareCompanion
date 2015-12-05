
package iqcarecompanion.core;

import iqcarecompanion.core.services.LabManager;
import iqcarecompanion.core.services.ObservationManager;
import static iqcarecompanion.core.utils.ResourceManager.setLoggingProperties;
import static iqcarecompanion.core.utils.RuntimeDirectory.createRuntimeDirs;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class Main {

     final static Logger logger = Logger.getLogger(Main.class.getName());
     
    public static void main(String[] args)  {
        createRuntimeDirs();
        setLoggingProperties();
        
        while(true){
            ObservationManager.mineEvents();
            //LabManager.generateLabResultsORU();
        }
    }
    
}
