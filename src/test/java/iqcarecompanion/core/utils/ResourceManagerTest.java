
package iqcarecompanion.core.utils;

import iqcarecompanion.core.jsonMapper.Event;
import static iqcarecompanion.core.utils.ResourceManager.modifyConfigFile;
import static iqcarecompanion.core.utils.ResourceManager.readConfigFile;
import static iqcarecompanion.core.utils.ResourceManager.readJSONFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.isIn;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Teddy Odhiambo
 */
public class ResourceManagerTest {
    private List<Event> events;

    @Before
    public void setUp() throws IOException {
        events = ResourceManager.readJSONFile();
    }
    
    @After
    public void tearDown() {
        events.clear();
    }

    @Test
    public void testReadJSONFile() throws IOException {
        assertNotNull(events);
    }
    
    @Test
    public void testMandatoryEventFields(){
        for(Event event:events){
            assertNotNull(event.eventName);
            assertNotNull(event.tableName); 
            assertNotNull(event.eventValueColumn);
            assertNotNull(event.eventValueDataType);
            assertNotNull(event.tableName);
            assertNotNull(event.visitIdColumn);
        }
    }
    
    @Test
    public void testValidDataTypesSupplied(){
        List<String> validDataTypes = new ArrayList<>();
        validDataTypes.add("INT");
        validDataTypes.add("STRING");
        validDataTypes.add("DATE");
        validDataTypes.add("DOUBLE");
        validDataTypes.add("DECIMAL");
        for(Event event:events){
            assertThat(event.eventValueDataType, isIn(validDataTypes));
        }
    }    
}
