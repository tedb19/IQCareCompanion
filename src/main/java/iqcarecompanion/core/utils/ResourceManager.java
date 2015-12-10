package iqcarecompanion.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import iqcarecompanion.core.jsonmapper.Event;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Teddy Odhiambo
 */
public class ResourceManager extends PropertiesManager {

    private static final Logger LOGGER = Logger.getLogger(ResourceManager.class.getName());
    private static final String JSON_FILE = "events.txt";
    private static List<Event> events;
    
    //Hide the implicit public constructor
    private ResourceManager(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    
    public static List<Event> readJSONFile(){
        if(events == null){
            try {

                byte[] jsonData = Files.readAllBytes(Paths.get(CONF_PATH + JSON_FILE));
                ObjectMapper mapper = new ObjectMapper();
                events = mapper.readValue(jsonData, new TypeReference<List<Event>>() {
                });
                return events;
            } catch (IOException ex) {
                StringBuilder sb = new StringBuilder();
                sb.append(LOG_PREFIX)
                        .append(" An error occurred while reading the json file:\n");
                LOGGER.log(Level.SEVERE,sb.toString(),ex);
            }
        }
        return events;
    }

    public static void updateLastId(int finalId, String key) {
        if (finalId != 0) {
            modifyConfigFile(key, Integer.toString(finalId));
            LOGGER.log(Level.INFO, "{0} {1} successfully updated to {2} in runtime.properties",
                    new Object[]{LOG_PREFIX, key, finalId});
        }
    }
    
    public static void setLoggingProperties() {
        FileInputStream fis = null;
        try {
            Logger globalLogger = Logger.getLogger("global");
            Handler[] handlers = globalLogger.getHandlers();
            for(Handler handler : handlers) {
                globalLogger.removeHandler(handler);
            }
            String propFileLocation = CONF_PATH + "logger.properties";
            fis = new FileInputStream(propFileLocation);
            LogManager.getLogManager().readConfiguration(fis);
        } catch (IOException ex) {
            StringBuilder sb = new StringBuilder();
            sb.append(LOG_PREFIX)
                    .append(" An error occurred while reading the iqcarecompanion properties:\n");
            LOGGER.log(Level.SEVERE,sb.toString(),ex);
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException ex) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(LOG_PREFIX)
                            .append(" The following issue is preventing the FileInputStream from closing:\n");
                    LOGGER.log(Level.SEVERE,sb.toString(),ex);
                }
            }
        }
    }
}
