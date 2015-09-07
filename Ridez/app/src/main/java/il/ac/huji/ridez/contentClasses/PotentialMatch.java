package il.ac.huji.ridez.contentClasses;

import java.util.Date;

/**
 * Created by Tzvi on 24/08/2015.
 */
public class PotentialMatch {
    public String fullName, userEmail, fromAddress, toAddress;
    public Boolean iAmRequester = true;
    public boolean isConfirmed = false;
    public Date date;
    public String id = "";
}
