package il.ac.huji.ridez;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zahi on 02/09/2015.
 */
public class PushReceiver extends ParsePushBroadcastReceiver {
    final static int MATCH_ACT = 1, ADDED_TOO_GROUP = 2, ASSIGNED_ADMIN = 3, DELETED_FROM_GROUP = 4;
    @Override

    protected Class<? extends Activity> getActivity(Context context, Intent intent) {
        super.getActivity(context, intent);
        try {
            JSONObject pushData = new JSONObject(intent.getStringExtra("com.parse.Data"));
            int type  = pushData.getInt("type");
            intent.putExtra("fromPush", true);

            switch(type) {
                case MATCH_ACT:
                    intent.putExtra("rideId",pushData.getString("ride_id"));
                    intent.putExtra("matchId",pushData.getString("match_id"));
                    intent.putExtra("isRequest", pushData.getBoolean("is_request"));
                    return RideDetailsActivity.class;

                case ADDED_TOO_GROUP:
                    intent.putExtra("groupAdded",pushData.getString("group_id"));
                    return GroupDetailsActivity.class;

                default:
                    return MainMenuActivity.class;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MainMenuActivity.class;
    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.ridezicon);
        int width = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_width);
        int height = context.getResources().getDimensionPixelSize(android.R.dimen.notification_large_icon_height);
        Bitmap c = Bitmap.createScaledBitmap(b, width, height, false);
        return c;
    }
}
