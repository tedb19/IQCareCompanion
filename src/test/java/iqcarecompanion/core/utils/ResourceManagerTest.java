
package iqcarecompanion.core.utils;

import iqcarecompanion.core.jsonmapper.Event;
import static iqcarecompanion.core.utils.ConstantProperties.SENTINEL_EVENTS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Teddy Odhiambo
 */
public class ResourceManagerTest {

    @Test
    public void testReadJSONFile() throws IOException {
        assertNotNull(SENTINEL_EVENTS);
    }
    
    @Test
    public void testMandatoryEventFields(){
        for(Event event:SENTINEL_EVENTS){
            assertNotNull(event.eventName);
            assertNotNull(event.tableName); 
            assertNotNull(event.eventValueColumn);
            assertNotNull(event.tableName);
            assertNotNull(event.visitIdColumn);
        }
    }
        
}
