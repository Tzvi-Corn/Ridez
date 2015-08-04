package il.ac.huji.ridez;

import android.content.Context;

import java.util.List;

import il.ac.huji.ridez.sqlHelpers.GroupInfo;
import il.ac.huji.ridez.sqlHelpers.GroupsDataSource;
import il.ac.huji.ridez.sqlHelpers.RideInfo;
import il.ac.huji.ridez.sqlHelpers.SQLGroupsHelper;

/**
 * Created by Zahi on 04/08/2015.
 */
public class DB {
    private GroupsDataSource datasource;
    static List<GroupInfo> groups;
    static List<RideInfo> ridesHistory;

    public DB(Context context){
//        SQLGroupsHelper helper = new SQLGroupsHelper(context);
        context.deleteDatabase("groups.db");    //TODO delete after works
        datasource = new GroupsDataSource(context);
        datasource.open();
        DB.groups = datasource.getAllGroupInfos();

    }

    public void addGroup(GroupInfo group){
        groups.add(group);
        datasource.createGroupInfo(group.getName(), group.getDescription(), group.getIcon());
    }
}
