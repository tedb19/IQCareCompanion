
package iqcarecompanion.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Teddy Odhiambo
 */
public class DateUtil {

    public static String parseDate(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmss");
        String startDate = ft.format(date);
        return startDate;
    }  
}
