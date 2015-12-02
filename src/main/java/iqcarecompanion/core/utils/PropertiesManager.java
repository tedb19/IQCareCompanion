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

    final static Logger logger = Logger.getLogger(PropertiesManager.class.getName());
    public static final String _separator = File.separator;
    public static final String confPath = "resources" + _separator + "Conf" + _separator;
    private static final String iqcarecompanionPropertiesFile = "iqcarecompanion.properties";
    private static final String runtimePropertiesFile = "runtime.properties";
    private static Properties properties;

    public static String readConfigFile(String key) throws IOException {
        Properties prop = new Properties();
        String propFileLocation = confPath + runtimePropertiesFile;
        FileInputStream input = new FileInputStream(propFileLocation);
        prop.load(input);
        return prop.getProperty(key);
    }

    protected static synchronized Properties getProperties(){

        if (properties != null) {
            return properties;
        }
        String propFileLocation = confPath + iqcarecompanionPropertiesFile;
        try{
            FileInputStream input = new FileInputStream(propFileLocation);
            properties = new Properties();
            properties.load(input);
            logger.log(Level.INFO, "{0} The properties on {1} file were successfully loaded to memory.", 
                    new Object[]{LOG_PREFIX, iqcarecompanionPropertiesFile});
        } catch(IOException ex){
            logger.log(Level.SEVERE, "{0} Oooops! The {1} file could not be found at {2}. {3}", 
                    new Object[]{LOG_PREFIX,iqcarecompanionPropertiesFile, propFileLocation, ex});
        }
        return properties;
    }

    public static void modifyConfigFile(String key, String value) throws IOException {
        Properties prop = new Properties();
        FileInputStream fis = new FileInputStream(confPath + runtimePropertiesFile);
        prop.load(fis);
        prop.setProperty(key, value);

        FileOutputStream fos = new FileOutputStream(confPath + runtimePropertiesFile);
        prop.store(fos, null);

    }

}
