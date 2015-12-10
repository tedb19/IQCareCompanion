package iqcarecompanion.core.utils;

import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class PropertiesManager {
    
    private static final Logger LOGGER = Logger.getLogger(PropertiesManager.class.getName());
    public static final String FILE_SEPARATOR = File.separator;
    public static final String CONF_PATH = "resources" + FILE_SEPARATOR + "Conf" + FILE_SEPARATOR;
    private static final String STATIC_PROPERTIES_FILE = "iqcarecompanion.properties";
    private static final String RUNTIME_PROPERTIES_FILE = "runtime.properties";
    private static Properties properties;

    /*
    * Reads a single property from the runtime property file.
    * Hits the property file each time, since we need the latest
    * value
    */
    public static String readConfigFile(String key) {
        FileInputStream input = null;
        String value = null;
        try {
            Properties prop = new Properties();
            String propFileLocation = CONF_PATH + RUNTIME_PROPERTIES_FILE;
            input = new FileInputStream(propFileLocation);
            prop.load(input);
            value = prop.getProperty(key);
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred while reading the iqcarecompanion properties:\n");
            LOGGER.log(Level.SEVERE,sb.toString(),ex);
        } finally {
            if(input != null){
                try {
                    input.close();
                } catch (IOException ex) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(LOG_PREFIX)
                        .append("The following issue is preventing the FileInputStream from closing:\n");
                    LOGGER.log(Level.SEVERE, sb.toString() , ex);
                }
            }
        }
        return value;
    }

    /*
    * Reads all the properties from the iqcarecompanion file,
    * and stores them in memory.
    * Hits the properties file only once
    */
    protected static synchronized Properties getProperties(){

        if (properties != null) {
            return properties;
        }
        String propFileLocation = CONF_PATH + STATIC_PROPERTIES_FILE;
        try{
            FileInputStream input = new FileInputStream(propFileLocation);
            properties = new Properties();
            properties.load(input);
            LOGGER.log(Level.INFO, "{0} The properties on {1} file were successfully loaded to memory.", 
                    new Object[]{LOG_PREFIX, STATIC_PROPERTIES_FILE});
        } catch(IOException ex){
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX).append(" Oooops! The ")
                    .append(STATIC_PROPERTIES_FILE)
                    .append(" file could not be found at ")
                    .append(CONF_PATH);
            LOGGER.log(Level.SEVERE,sb.toString(),ex);
        }
        return properties;
    }

    public static void modifyConfigFile(String key, String value) {
        FileInputStream fis = null;
        try {
            Properties prop = new Properties();
            fis = new FileInputStream(CONF_PATH + RUNTIME_PROPERTIES_FILE);
            prop.load(fis);
            prop.setProperty(key, value);
            FileOutputStream fos = new FileOutputStream(CONF_PATH + RUNTIME_PROPERTIES_FILE);
            prop.store(fos, null);
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred while modifying the runtime properties:\n");
            LOGGER.log(Level.SEVERE,sb.toString(),ex);
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "The following issue is preventing the FileInputStream from closing:\n", ex);
                }
            }
        }

    }

}
