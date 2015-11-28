
package iqcarecompanion.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author Teddy Odhiambo
 */
public class DateUtil {
    public static String parseDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd hh:mm:ss",
                Locale.ENGLISH);
        
        Date parsedDate = sdf.parse(dateString);
        SimpleDateFormat print = new SimpleDateFormat("yyyyMMddhhmmss");
        return print.format(parsedDate);
    }

    public static String parseDate(Date date) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmss");
        String startDate = ft.format(date);
        return startDate;
    }  
}
