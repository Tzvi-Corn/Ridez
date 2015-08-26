package il.ac.huji.ridez.contentClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ParseClassName("Group")
public class RidezGroup extends ParseObject{

    public class Member {
        public String id, name, email;
        public boolean isAdmin = false;
        public ParseUser parseUser;
    }

    private Bitmap icon;
    private Map<String, Member> members;
    private boolean membersReady = false;

    public RidezGroup() {
        super();
        members = new HashMap<>();
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

    public Bitmap getIconInBackground(final GetIconCallback callback)  {
        ParseFile iconFile = getParseFile("icon");
        iconFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if (e == null) {
                    icon = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes));
                }
                callback.done(icon, e);
            }
        });
        return icon;
    }

    public Bitmap getLocalIcon() {
        return icon;
    }

    public void setLocalIcon(Bitmap icon) {
        this.icon = icon;
    }

    public void uploadLocalIconInBackground(SaveCallback callback) {
        setIconInBackground(icon, callback);
    }

    public void setIconInBackground(Bitmap icon, final SaveCallback callback) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        final ParseFile iconFile;
        if (icon == null) {
            iconFile = new ParseFile("icon.PNG", "".getBytes());
        } else {
            icon.compress(Bitmap.CompressFormat.PNG, 50, bs);
            iconFile = new ParseFile("icon.PNG", bs.toByteArray());
        }
        iconFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    put("icon", iconFile);
                }
                callback.done(e);
            }
        });
        this.icon = icon;
    }

    public void updateMembersInBackground(final SaveCallback callback) {
        ParseRelation<ParseUser> users = getRelation("users");
        users.getQuery().findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    for (ParseUser parseUser : list) {
                        Member newMember = new Member();
                        newMember.id = parseUser.getObjectId();
                        newMember.email = parseUser.getEmail();
                        newMember.name = parseUser.getString("fullname");
                        newMember.parseUser = parseUser;
                        members.put(newMember.email, newMember);
                    }
                    ParseRelation<ParseUser> admins = getRelation("admins");
                    admins.getQuery().findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> list, ParseException e) {
                            if (e == null) {
                                for (ParseUser parseUser : list) {
                                    Member member = members.get(parseUser.getEmail());
                                    if (member != null) {
                                        member.isAdmin = true;
                                    }
                                }
                                membersReady = true;
                            }
                            callback.done(e);
                        }
                    });
                } else {
                    callback.done(e);
                }
            }
        });
    }

    public boolean isMembersReady() {
        return membersReady;
    }

    public Map<String, Member> getMembers() {
        return members;
    }

    public void addUser(ParseUser parseUser) {
        addUser(parseUser, false);
    }

    public void addUser(ParseUser parseUser, boolean isAdmin) {
        Member newMember = new Member();
        newMember.email = parseUser.getEmail();
        newMember.id = parseUser.getObjectId();
        newMember.name = parseUser.getString("fullname");
        newMember.parseUser = parseUser;
        ParseRelation<ParseUser> users = getRelation("users");
        users.add(parseUser);
        members.put(newMember.email, newMember);
        if (isAdmin) {
            setAdmin(newMember, true);
        }
    }

    public void removeUser(ParseUser parseUser) {
        ParseRelation<ParseUser> users = getRelation("users");
        ParseRelation<ParseUser> admins = getRelation("admins");
        users.remove(parseUser);
        members.remove(parseUser.getEmail());
        admins.remove(parseUser);
    }

    public void setAdmin(Member member, boolean isAdmin) {
        ParseRelation<ParseUser> admins = getRelation("admins");
        if (isAdmin) {
            admins.add(member.parseUser);
        } else {
            admins.remove(member.parseUser);
        }
        member.isAdmin = isAdmin;
    }

    public void addUserInBackground(final String email, final GetCallback<ParseUser> callback) {
        ParseUser.getQuery().whereEqualTo("email", email).getFirstInBackground(new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e == null) {
                    addUser(parseUser);
                } else {
                    if (e.getCode() == ParseException.OBJECT_NOT_FOUND) {
                        Member newMember = new Member();
                        newMember.email = email;
                        addUnique("waitingUsers", email);
                        members.put(email, newMember);
                        e = null;
                    }
                }
                callback.done(parseUser, e);
            }
        });
    }

    public void addUsersInBackground(final ArrayList<String> emails, final SaveCallback callback) {
        class myCallback implements GetCallback<ParseUser> {
            private int i = -1;
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                int next = i + 1;
                if (e == null && next < emails.size()) {
                    addUserInBackground(emails.get(next), new myCallback().init(next));
                } else {
                    callback.done(e);
                }
            }
            public myCallback init(int i) {
                this.i = i;
                return this;
            }
        }
        new myCallback().init(-1).done(null, null);
    }

    public interface GetIconCallback {
        void done(Bitmap icon, ParseException e);
    }
}
