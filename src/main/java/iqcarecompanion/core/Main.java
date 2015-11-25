/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core;

import iqcarecompanion.core.services.LabManager;
import iqcarecompanion.core.services.ObservationManager;
import iqcarecompanion.core.utils.ResourceManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 *
 * @author Teddy
 */
public class Main {

     final static Logger logger = Logger.getLogger(Main.class.getName());
     
    public static void main(String[] args) throws FileNotFoundException, IOException {
        Logger globalLogger = Logger.getLogger("global");
            Handler[] handlers = globalLogger.getHandlers();
            for(Handler handler : handlers) {
                globalLogger.removeHandler(handler);
            }
            FileInputStream fis =  new FileInputStream(ResourceManager.confPath+ ResourceManager.confSeparator+ "logger.properties");
            LogManager.getLogManager().readConfiguration(fis);
        
        while(true){
            ObservationManager.mineEvents();
            LabManager.generateLabResultsORU();
        }
    }
    
}
