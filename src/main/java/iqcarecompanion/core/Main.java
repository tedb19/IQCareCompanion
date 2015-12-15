
package iqcarecompanion.core;

import iqcarecompanion.core.facade.LabFacade;
import iqcarecompanion.core.facade.VisitFacade;
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
            VisitFacade.mineEvents();
            LabFacade.mineLabEvents();
        }
    }
    
}
