package il.ac.huji.ridez;
import android.app.Application;

import com.parse.Parse;

public class ridezApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "8VFSK81d3JofZNkzQ1V9pWWGxYFiQEaSk57HM8BR", "lhGtlfFbe2AAd3KFhF3kpj75PP37UkYHEbK1NTiM");
    }
}
