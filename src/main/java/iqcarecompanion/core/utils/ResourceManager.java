/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import iqcarecompanion.core.jsonMapper.Event;
import org.kemricdc.constants.PropertiesManager;

/**
 *
 * @author Teddy
 */
public class ResourceManager extends PropertiesManager {

    final static Logger logger = Logger.getLogger(ResourceManager.class.getName());

    public static synchronized ArrayList<String> readKeys(String prop_file) throws IOException {
        Properties prop = new Properties();
        ArrayList<String> keyList = new ArrayList<>();

        try (FileInputStream input = new FileInputStream(confPath + prop_file)) {
            prop.load(input);
        }

        Enumeration en = prop.keys();

        while (en.hasMoreElements()) {
            keyList.add((String) en.nextElement());
        }

        return keyList;
    }

    public static List<Event> readJSONFile(String jsonFile) {
        List<Event> events = new ArrayList<>();
        try {
            byte[] jsonData = Files.readAllBytes(Paths.get(confPath + jsonFile));
            ObjectMapper mapper = new ObjectMapper();
            events = mapper.readValue(jsonData, new TypeReference<List<Event>>() {
            });
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
        }
        return events;
    }

}
