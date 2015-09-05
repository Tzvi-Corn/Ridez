package il.ac.huji.ridez;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Zahi on 02/09/2015.
 */
public class PushReceiver extends ParsePushBroadcastReceiver {
    final static int MATCH_ACT = 1, ADDED_TOO_GROUP = 2;
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
                    return RequestDetails.class;

                case ADDED_TOO_GROUP:
                    intent.putExtra("groupAdded",pushData.getString("group_id"));
                    return MyGroupsActivity.class;

                default:
                    return MainMenuActivity.class;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return MainMenuActivity.class;
    }

}
