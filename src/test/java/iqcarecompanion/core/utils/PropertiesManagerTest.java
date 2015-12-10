package iqcarecompanion.core.utils;

import static iqcarecompanion.core.utils.PropertiesManager.modifyConfigFile;
import static iqcarecompanion.core.utils.PropertiesManager.readConfigFile;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Teddy Odhiambo
 */
public class PropertiesManagerTest {
    private String key;
    private String value;
    
    @Before
    public void setUp() {
        key = "labResultId";
        value = "40";
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void testModifyConfigFile() {
        String initialValue = readConfigFile(key);
        modifyConfigFile(key, value);
        String currentValue = readConfigFile(key);
        assertThat(currentValue, is(equalTo(value)));
        //revert the property value
        modifyConfigFile(key, initialValue);
    }
    
    @Test
    public void testReadConfigFile() {
        String currentValue = readConfigFile(key);
        assertThat(currentValue, is(allOf(not(nullValue()),instanceOf(String.class))));  
    }
    
}
