
package iqcarecompanion.core.hapiwrapper;

import java.util.Date;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.kemricdc.hapi.oru.OruFiller;

/**
 *
 * @author Teddy Odhiambo
 */
public class HAPIWrappersTest {
    private String eventName = "";
    private String eventValue = ""; 
    
    @Before
    public void setUp() {
        eventName = "CD4_COUNT";
        eventValue = "56";
    }
    
    @After
    public void tearDown() {
        eventName = "";
        eventValue = "";
    }

    @Test
    public void testCreateOBXReturnsValidORUFiller() {
        OruFiller filler = HAPIWrappers.createOBX(eventName, eventValue, new Date());
        assertThat(filler, instanceOf(OruFiller.class));
        assertThat(eventName, is(filler.getObservationIdentifierText()));
        assertThat(eventValue, is(filler.getObservationValue()));
    }
    
    @Test
    public void testCreateOBXReturnsNullForNullInputs(){
        OruFiller filler = HAPIWrappers.createOBX(null, null, null);
        assertNull("ORUFiller object was not null for null inputs", filler);
    }
    
}
