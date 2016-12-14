package sanna.z;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;

/**
 * Created by Sanna on 11-12-2016.
 */

public class AnalyticsApplication extends Application {
    // The following line should be changed to include the correct property id.
    private static final String PROPERTY_ID = "UA-88740987-1";

    //Logging TAG
    private static final String TAG = "z";

    public static int GENERAL_TRACKER = 0;

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    public AnalyticsApplication() {
        super();
        Log.v("Analytics","check");
    }

    synchronized Tracker getTracker(TrackerName trackerId) {
        Log.v("Analytics","check1");
        if (!mTrackers.containsKey(trackerId)) {
            Log.v("Analytics","check2");
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(R.xml.app_tracker)
                    : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(PROPERTY_ID)
                    : analytics.newTracker(R.xml.app_tracker);
            mTrackers.put(trackerId, t);

        }
        return mTrackers.get(trackerId);
    }
}
