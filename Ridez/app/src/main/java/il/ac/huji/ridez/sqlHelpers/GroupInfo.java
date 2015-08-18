package il.ac.huji.ridez.sqlHelpers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;

/**
 * Created by Zahi on 04/08/2015.
 */
public class GroupInfo {
    private long id;
    private String name, description;//, iconPath;
    private Bitmap icon;
    private ArrayList<String> members;

    public GroupInfo(String name, String description, Bitmap icon){
        this.name = name;
        this.description = description;
        this.icon = icon;
    }
    public GroupInfo(String name, String description, String iconPath){
        this.name = name;
        this.description = description;
        this.icon = BitmapFactory.decodeFile(iconPath);
    }

    public GroupInfo(String[] nameNDesc, Bitmap icon) {
        this(nameNDesc[0], nameNDesc[1], icon);
    }
    public GroupInfo(String[] atts) {
        this(atts[0], atts[1], atts[2]);
    }
    public GroupInfo(String name, String description, byte[] bitmapArray) {
        this(name, description, BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length));
    }
    public GroupInfo(String[] nameNDesc, byte[] bitmapArray) {
        this(nameNDesc[0], nameNDesc[1], bitmapArray);
    }
//    public GroupInfo(String name, String description, String iconPath){
//        this.name = name;
//        this.description = description;
//        this.iconPath = iconPath;
//    }


//    public GroupInfo(String[] info){
//        this.name = info[0];
//        this.description = info[1];
//        this.iconPath = info[2];
//    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public String getDescription(){
        return description;
    }
//    public String getIconPath(){
//        return iconPath;
//    }
    public Bitmap getIcon(){
        return icon;
    }
//    public String[] getInfo(){
//        return new String[]{name, description, iconPath};
//    }

    @Override
    public String toString() {
        return name + " " + description;
    }
}
