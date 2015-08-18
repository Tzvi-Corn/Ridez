package il.ac.huji.ridez.adpaters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.ParseException;

import java.lang.reflect.Array;
import java.sql.Ref;
import java.util.List;

import il.ac.huji.ridez.R;
import il.ac.huji.ridez.contentClasses.RidezGroup;
import il.ac.huji.ridez.sqlHelpers.GroupInfo;

public class RidezGroupArrayAdapter extends ArrayAdapter<RidezGroup> {
    private final Context context;
    private List<RidezGroup> list;

    public RidezGroupArrayAdapter(Context context, List<RidezGroup> objects) {
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
        final ImageView groupIconImageView = (ImageView) rowView.findViewById(R.id.groupIcon);

        RidezGroup item = list.get(position);

        nameTextView.setText(item.getName());
        descriptionTextView.setText(item.getDescription());
        Bitmap icon = item.getIconInBackground(new RidezGroup.GetIconCallback() {
            @Override
            public void done(Bitmap icon, ParseException e) {
                if (e == null && icon != null) {
                    groupIconImageView.setImageBitmap(icon);
                }
            }
        });
        if ( icon != null) {
            groupIconImageView.setImageBitmap(icon);
        }
        //groupIconImageView.setImageResource(Drawable(item.getIconPath()));


        return rowView;
    }
}

