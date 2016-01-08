
package iqcarecompanion.core;

import iqcarecompanion.core.facade.LabFacade;
import iqcarecompanion.core.facade.VisitFacade;
import static iqcarecompanion.core.utils.ConstantProperties.DUMPS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOGS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import static iqcarecompanion.core.utils.ResourceManager.setLoggingProperties;
import static iqcarecompanion.core.utils.RuntimeDirectory.createRuntimeDir;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    
    private Main(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    
    public static void main(String[] args)  {
        try {
            createRuntimeDir(LOGS_DIR, "log files");
            createRuntimeDir(DUMPS_DIR, "hl7 files");
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred during the creation of the runtime directories:\n");
            LOGGER.log(Level.SEVERE,sb.toString(),ex);
        }
        setLoggingProperties();
        
        while(true){
            VisitFacade.mineEvents();
            //LabFacade.mineLabEvents();
        }
    }
    
}
