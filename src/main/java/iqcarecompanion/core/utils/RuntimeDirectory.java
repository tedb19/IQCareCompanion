package iqcarecompanion.core.utils;

import static iqcarecompanion.core.utils.ConstantProperties.DUMPS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOGS_DIR;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class RuntimeDirectory {

    private static final Logger LOGGER = Logger.getLogger(RuntimeDirectory.class.getName());

    //Hide the implicit public constructor
    private RuntimeDirectory(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    
    public static void createRuntimeDirs(){
        try {
            createRuntimeDir(LOGS_DIR, "log files");
            createRuntimeDir(DUMPS_DIR, "hl7 files");
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred during the creation of the runtime directories:\n");
            LOGGER.log(Level.SEVERE,sb.toString(),ex);
        }
    }
     
    private static void createRuntimeDir(File dir, String usage) throws IOException {
        if (!dir.exists()) {
            boolean isCreated = dir.mkdirs();
            if(isCreated){
            LOGGER.log(Level.INFO,
                    "{0} Created the folder {1} to be used for the {2}",
                    new Object[]{LOG_PREFIX, dir.getCanonicalPath(), usage});
            } else {
             LOGGER.log(Level.INFO,
                    "{0} Unable to create the folder {1} for the {2}",
                    new Object[]{LOG_PREFIX, dir.getCanonicalPath(), usage});   
            }
        } else {
            LOGGER.log(Level.INFO,
                    "{0} The {1} are stored at {2}", 
                    new Object[]{LOG_PREFIX, usage, dir.getCanonicalPath()});
        }
    }

}
