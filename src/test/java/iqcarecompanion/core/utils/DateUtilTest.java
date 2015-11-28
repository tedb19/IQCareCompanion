/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package iqcarecompanion.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Ted
 */
public class DateUtilTest {

    private String inputStr = "11-11-2012";

    @Before
    public void setUp() {
        inputStr = "2012-11-24 11:32:12";//yyyy-MM-dd hh:mm:ss
    }

    @After
    public void tearDown() {
        inputStr = "";
    }

    @Test
    public void testParseDate() throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date inputDate = dateFormat.parse(inputStr);
        String result = DateUtil.parseDate(inputDate);
        String expectedValue = "20121124113212";
        assertThat(expectedValue, is(result));
    }

}
