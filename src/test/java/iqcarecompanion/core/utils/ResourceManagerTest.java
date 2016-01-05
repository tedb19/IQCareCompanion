
package iqcarecompanion.core.utils;

import iqcarecompanion.core.jsonmapper.Event;
import static iqcarecompanion.core.utils.ConstantProperties.SENTINEL_EVENTS;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
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
    
    @Test
    public void UpdateLastId_FinalIdEqualZero_ValueNotModified(){
      String key = "labResultId";
      String originalValue = ResourceManager.readConfigFile(key);
      ResourceManager.updateLastId(0, key);
      String currentValue = ResourceManager.readConfigFile(key);
      assertThat(currentValue, is(equalTo(originalValue)));
    }
    
    @Test
    public void UpdateLastId_FinalIdNotEqualZero_ValueModified(){
      String key = "labResultId";
      String originalValue = ResourceManager.readConfigFile(key);
      ResourceManager.updateLastId(5, key);
      String updatedValue = ResourceManager.readConfigFile(key);
      ResourceManager.modifyConfigFile(key, originalValue);
      assertThat(updatedValue, is(equalTo("5")));
    }
    
    @Test
    public void UpdateLastId_KeyIsNull_ValueModified(){
      String key = "labResultId";
      String originalValue = ResourceManager.readConfigFile(key);
      ResourceManager.updateLastId(5, null);
      String updatedValue = ResourceManager.readConfigFile(key);
      assertThat(updatedValue, is(equalTo(originalValue)));
    }
    
    @Test
    public void testConstructorIsPrivate() throws Exception {
      Constructor constructor = ResourceManager.class.getDeclaredConstructor();
      assertTrue(Modifier.isPrivate(constructor.getModifiers()));
      constructor.setAccessible(true);
      constructor.newInstance();
    }
    
    @Test
    public void SetLoggingProperties_LoggingPropertiesSet() throws Exception{
      ResourceManager.setLoggingProperties();
      Logger globalLogger = Logger.getLogger("global");
      assertThat(globalLogger.getHandlers().length, is(equalTo(0)));
    }
        
}
