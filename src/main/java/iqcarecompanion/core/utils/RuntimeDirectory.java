package iqcarecompanion.core.utils;

import static iqcarecompanion.core.utils.ConstantProperties.CDS_NAME;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class RuntimeDirectory {

    final static Logger logger = Logger.getLogger(RuntimeDirectory.class.getName());
    private static final File dumpsDir = new File(System.getProperty("user.home") + File.separator + "IQCare-Companion" + File.separator + "Dumps");
    private static final File logsDir = new File(System.getProperty("user.home") + File.separator + "IQCare-Companion" + File.separator + "Logs");

    public static void createRuntimeDir() throws IOException {
        if (!logsDir.exists()) {
            boolean isCreated = logsDir.mkdirs();
            if(isCreated == true){
            logger.log(Level.INFO,
                    "{0} Created the folder {1} to be used for the log files",
                    new Object[]{LOG_PREFIX, logsDir.getCanonicalPath()});
            } else {
             logger.log(Level.INFO,
                    "{0} Unable to create the folder {1} for the log files",
                    new Object[]{LOG_PREFIX, logsDir.getCanonicalPath()});   
            }
        } else {
            logger.log(Level.INFO,
                    "The log files are stored at {0}", logsDir.getCanonicalPath());
        }
        if (!dumpsDir.exists()) {
            boolean isCreated = dumpsDir.mkdirs();
            if(isCreated == true){
                logger.log(Level.INFO,
                    "{0} Created the folder {1} to be used for the hl7 backups",
                    new Object[]{LOG_PREFIX, dumpsDir.getCanonicalPath()});
            } else {
                logger.log(Level.INFO,
                    "{0} Unable to create the folder {1} for the hl7 backups",
                    new Object[]{LOG_PREFIX, dumpsDir.getCanonicalPath()});
            }
        } else {
            logger.log(Level.INFO,
                "{0} The hl7 dump files are stored at {1}",
                new Object[]{LOG_PREFIX, dumpsDir.getCanonicalPath()});
        }
    }

    public static void createHl7Dump(String hl7, String msgType) throws FileNotFoundException, IOException {
        Random random = new Random();

        File hl7File = new File(dumpsDir, ((random.nextInt(Integer.MAX_VALUE) + 1) + "-" + msgType + ".hl7"));
        logger.log(Level.INFO,
                "{0} Created {1} on disk to be sent to the {2}",
                new Object[]{LOG_PREFIX, hl7File.getCanonicalPath(), CDS_NAME});

        try (FileOutputStream fos = new FileOutputStream(hl7File)) {
            byte[] b = hl7.getBytes();
            fos.write(b);
            fos.flush();
        }
    }

}
