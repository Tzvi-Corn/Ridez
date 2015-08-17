package il.ac.huji.ridez.contentClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayInputStream;

@ParseClassName("Group")
public class RidezGroup extends ParseObject{

    public class Member {
        public String id, name;
        public boolean isRegistered;
    }

    private Bitmap icon;
    private Member members[];
    private boolean uploadIcon = false;

    public RidezGroup() {
    }

    public String getName() {
        return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }

    public String getDescription() {
        return getString("description");
    }

    public void setDescription(String description) {
        put("description", description);
    }

    public Bitmap getIcon() throws ParseException {
        ParseFile iconFile = getParseFile("icon");
        icon = BitmapFactory.decodeStream(new ByteArrayInputStream(iconFile.getData()));
        return icon;
    }

    public void getIconInBackground(final GetIconCallback callback)  {
        ParseFile iconFile = getParseFile("icon");
        iconFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    icon = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
                    callback.done(icon, e);
                } else {
                    callback.done(icon, e);
                }
            }
        });
    }

    public Bitmap getIconWithoutUpdating() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
        uploadIcon = true;
    }

    public Member[] getMembers() {
        return members;
    }

    public void setMembers(Member[] members) {
        this.members = members;
    }

    public interface GetIconCallback {
        void done(Bitmap icon, ParseException e);
    }
}
