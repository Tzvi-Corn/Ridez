package il.ac.huji.ridez;

import android.content.Context;
import android.widget.Toast;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Zahi on 02/09/2015.
 */
public class Toolbox {
    private static SimpleDateFormat dateShortFormat = new SimpleDateFormat("dd/MM/yy");
    private static SimpleDateFormat dateLongFormat = new SimpleDateFormat("EEE dd-MMM-yyyy");
    private static SimpleDateFormat timeForamt = new SimpleDateFormat("HH:mm");

    public static String dateToShortDateString(Date date) {
        return dateShortFormat.format(date);
    }

    public static String dateToLongDateString(Date date) {
        return dateLongFormat.format(date);
    }

    public static String dateToTimeString(Date date) {
        return timeForamt.format(date);
    }

    public static String dateToShortDateAndTimeString(Date date) {
        return dateShortFormat.format(date) + " - " + timeForamt.format(date);
    }

    public static String dateToLongDateAndTimeString(Date date) {
        return dateLongFormat.format(date) + " - " + timeForamt.format(date);
    }

    public static String dateToShortDateAndTimeString(Date date, double timeInterval) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.add(Calendar.MINUTE, (int)(-timeInterval));
        Date startDate = cal.getTime();
        cal.add(Calendar.MINUTE, 2 * (int) timeInterval);
        Date endDate = cal.getTime();
        return dateLongFormat.format(date) + " \n " + Toolbox.dateToTimeString(startDate) + " to " + Toolbox.dateToTimeString(endDate);
    }

}
