/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import iqcarecompanion.core.jsonMapper.Event;
import org.kemricdc.constants.PropertiesManager;

/**
 *
 * @author Teddy Odhiambo
 */
public class ResourceManager extends PropertiesManager {

    final static Logger logger = Logger.getLogger(ResourceManager.class.getName());

    public static List<Event> readJSONFile(String jsonFile) throws IOException {
        byte[] jsonData = Files.readAllBytes(Paths.get(confPath + jsonFile));
        ObjectMapper mapper = new ObjectMapper();
        List<Event> events = mapper.readValue(jsonData, new TypeReference<List<Event>>() {
        });

        return events;
    }

    public static void updateLastId(int finalId, String key) {
        if (finalId != 0) {
            try {
                ResourceManager.modifyConfigFile(key, Integer.toString(finalId), "runtime.properties");
                logger.log(Level.INFO, "{0} successfully updated to {1} in runtime.properties", new Object[]{key, finalId});
            } catch (IOException ex) {
                logger.log(Level.SEVERE, ex.toString(), ex);
            }
        }
    }

}
