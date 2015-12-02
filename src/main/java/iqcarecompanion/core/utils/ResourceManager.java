
package iqcarecompanion.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import iqcarecompanion.core.jsonMapper.Event;
import static iqcarecompanion.core.utils.ConstantProperties.LOG_PREFIX;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

    final static Logger resourceMngrlogger = Logger.getLogger(ResourceManager.class.getName());
    private static List<Event> events;
    private static final String jsonFile = "events.txt";
    
    public static List<Event> readJSONFile() throws IOException {
        
        if(events != null){
            return events;
        }
        byte[] jsonData = Files.readAllBytes(Paths.get(confPath + jsonFile));
        ObjectMapper mapper = new ObjectMapper();
        events = mapper.readValue(jsonData, new TypeReference<List<Event>>() {
        });
        return events;
    }

    public static void updateLastId(int finalId, String key) {
        if (finalId != 0) {
            try {
                modifyConfigFile(key, Integer.toString(finalId));
                resourceMngrlogger.log(Level.INFO, "{0} {1} successfully updated to {2} in runtime.properties",
                        new Object[]{LOG_PREFIX, key, finalId});
            } catch (IOException ex) {
                StringBuilder sb = new StringBuilder();
                sb.append(LOG_PREFIX)
                    .append(" An error occurred while modifying the runtime properties:\n");
                resourceMngrlogger.log(Level.SEVERE,sb.toString(),ex);
            }
        }
    }
    
    public static void setLoggingProperties() throws FileNotFoundException, IOException{
            Logger globalLogger = Logger.getLogger("global");
            Handler[] handlers = globalLogger.getHandlers();
            for(Handler handler : handlers) {
                globalLogger.removeHandler(handler);
            }
            String propFileLocation = confPath + "logger.properties";
            FileInputStream fis =  new FileInputStream(propFileLocation);
            LogManager.getLogManager().readConfiguration(fis);
    }
}
