package il.ac.huji.ridez.adpaters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import il.ac.huji.ridez.MyGroupsActivity;
import il.ac.huji.ridez.R;
import il.ac.huji.ridez.sqlHelpers.GroupInfo;

/**
 * Created by Zahi on 21/07/2015.
 */
public class GroupsListArrayAdapter extends ArrayAdapter<GroupInfo> {
    private final Context context;
    private List<GroupInfo> list;

    public GroupsListArrayAdapter(Context context, List<GroupInfo> objects) {
        super(context, -1, objects);
        this.context = context;
        list = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.group_list_item, parent, false);
        TextView nameTextView = (TextView) rowView.findViewById(R.id.textViewGroupName);
        TextView descriptionTextView = (TextView) rowView.findViewById(R.id.textViewGroupDescription);
        ImageView groupIconImageView = (ImageView) rowView.findViewById(R.id.groupIcon);

        GroupInfo item = list.get(position);

        nameTextView.setText(item.getName());
        descriptionTextView.setText(item.getDescription());

//        groupIconImageView.setImageBitmap(BitmapFactory.decodeFile(item.getIconPath()));
//        groupIconImageView.setImageResource(Drawable(item.getIconPath()));


        return rowView;
    }
}

