package il.ac.huji.ridez.contentClasses;

import java.util.Date;

/**
 * Created by Tzvi on 24/08/2015.
 */
public class PotentialMatch {
    public String offerFullName, offerUserEmail, offerFromAddress, offerToAddress, requestFullName, requestUserEmail, requestFromAddress, requestToAddress;
    public Boolean iAmRequester = true;
    public Date offerDate, requestdate;
}
