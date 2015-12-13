
package iqcarecompanion.core;

import iqcarecompanion.core.services.LabManager;
import iqcarecompanion.core.services.ObservationManager;
import static iqcarecompanion.core.utils.ResourceManager.setLoggingProperties;
import static iqcarecompanion.core.utils.RuntimeDirectory.createRuntimeDirs;

/**
 *
 * @author Teddy Odhiambo
 */
public class Main {

    private Main(){
        throw new UnsupportedOperationException("This operation is forbidden!");
    }
    
    public static void main(String[] args)  {
        createRuntimeDirs();
        setLoggingProperties();
        
        while(true){
            ObservationManager.mineEvents();
            LabManager.mineLabResults();
        }
    }
    
}
