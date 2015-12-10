
package iqcarecompanion.core.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Teddy Odhiambo
 */
public class DateUtilTest {

    private String inputStr = "";

    @Before
    public void setUp() {
        inputStr = "2012-11-24 11:32:12";//yyyy-MM-dd hh:mm:ss
    }

    @After
    public void tearDown() {
        inputStr = "";
    }

    @Test 
    public void ParseDate_ValidDateAsParam_ReturnsHl7Date() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date inputDate = dateFormat.parse(inputStr);
        String result = DateUtil.parseDate(inputDate);
        String expectedValue = "20121124113212";
        assertThat(expectedValue, is(result));
    }
    
    @Test
    public void DateUtil_ConstructorIsPrivate(){
        final Constructor<?>[] constructors = DateUtil.class.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        }
    }

}
