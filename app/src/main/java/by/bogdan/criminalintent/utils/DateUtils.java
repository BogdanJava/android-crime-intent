package by.bogdan.criminalintent.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static int get24formatHours(Date date) {
        return Integer.parseInt(new SimpleDateFormat("HH", Locale.US).format(date));
    }

    public static Date fixTime(int hours, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if(hours > 12) {
            calendar.set(Calendar.AM_PM, Calendar.PM);
        } else {
            calendar.set(Calendar.AM_PM, Calendar.AM);
        }
        return calendar.getTime();
    }

    public static String getFormattedDate(Date date) {
        final SimpleDateFormat df = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.US);
        return df.format(date);
    }

}
