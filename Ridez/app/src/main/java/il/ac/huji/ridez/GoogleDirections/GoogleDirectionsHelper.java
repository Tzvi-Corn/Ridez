package il.ac.huji.ridez.GoogleDirections;

import android.content.Context;
import android.util.Log;
import android.widget.Filter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Tzvi on 26/08/2015.
 */
public class GoogleDirectionsHelper {
    private ArrayList resultList;
    private static final String LOG_TAG = "Directions API";
    private static final String DIRECTIONS_API_BASE = "https://maps.googleapis.com/maps/api/directions";
    private static final String OUT_JSON = "/json";

    private static final String API_KEY = "AIzaSyDQljX1PO_KSvCaunIkp0XptCkitMVBE_U";

    public static double getDuration(double fromLat, double fromLong, double toLat, double toLon) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(DIRECTIONS_API_BASE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            //sb.append("&components=country:gr");
            sb.append("&origin=" + fromLat + "," + fromLong);
            sb.append("&destination=" + toLat + "," + toLon);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with Directions API URL", e);
            return -1;
        } catch (IOException e) {
            Log.e(LOG_TAG, "connection err DirectionsAPI", e);
            return -1;
        }
        catch (Exception ex) {
            Log.v("v", "scijbcaskc;bj");
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        double duration = 0;
        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
             JSONArray legs= jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");

            for (int i = 0; i < legs.length(); ++i) {
                duration +=  legs.getJSONObject(i).getJSONObject("duration").getDouble("value");
            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return duration;
    }

    public static double getDuration(double fromLat, double fromLong, double wayPoint1Lat, double wayPoint1Lon, double wayPoint2Lat, double wayPoint2Lon, double toLat, double toLon) {

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(DIRECTIONS_API_BASE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            //sb.append("&components=country:gr");
            sb.append("&origin=" + fromLat + "," + fromLong);
            sb.append("&destination=" + toLat + "," + toLon);
            sb.append("&waypoints=" + wayPoint1Lat + "," + wayPoint1Lon + "|" + wayPoint2Lat + "," + wayPoint2Lon);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with Directions API URL", e);
            return -1;
        } catch (IOException e) {
            Log.e(LOG_TAG, "connection err DirectionsAPI", e);
            return -1;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray legs= jsonObj.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
            double duration = 0;
            for (int i = 0; i < legs.length(); ++i) {
                duration +=  legs.getJSONObject(i).getJSONObject("duration").getDouble("value");
            }


            return duration;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Cannot process JSON results", e);
        }

        return -1;
    }
}
