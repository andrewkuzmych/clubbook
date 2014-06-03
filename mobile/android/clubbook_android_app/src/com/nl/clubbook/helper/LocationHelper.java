package com.nl.clubbook.helper;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import com.nl.clubbook.R;

import java.util.List;

/**
 * Created by Andrew on 5/27/2014.
 */
public class LocationHelper {

    public static String calculateDistanceWithTitle(Context context, double lat, double lon) {
        String distance = "?";
        GPSTracker mGPS = new GPSTracker(context);
        Resources resources = context.getResources();//.getString(R.string.)
        if(mGPS.canGetLocation ){
            double mLat=mGPS.getLatitude();
            double mLong=mGPS.getLongitude();
            double eventLat = Double.valueOf(lat);
            double eventLon = Double.valueOf(lon);

            Location loc1 = new Location("");
            loc1.setLatitude(mLat);
            loc1.setLongitude(mLong);

            Location loc2 = new Location("");
            loc2.setLatitude(eventLat);
            loc2.setLongitude(eventLon);

            float distanceBtwPoints = loc1.distanceTo(loc2);

            if(distanceBtwPoints < 1000)
            {
                int distanceBtwPointsInt = Math.round(distanceBtwPoints/10)*10;
                distance = String.valueOf(distanceBtwPointsInt);
                distance += " " + resources.getString(R.string.m);
            }
            else
            {
                distanceBtwPoints = Math.round(distanceBtwPoints/100);
                distance = String.valueOf((double)distanceBtwPoints/10);
                distance += " " + resources.getString(R.string.km);

            }
        }
        return distance;
    }

    public static String calculateDistance(Context context, float distance)
    {
        Resources resources = context.getResources();
        String distanceResult = "?";

        if(distance < 1000)
        {
            int distanceBtwPointsInt = Math.round(distance/10)*10;
            distanceResult = String.valueOf(distanceBtwPointsInt);
            distanceResult += " " + resources.getString(R.string.m);
        }
        else
        {
            distance = Math.round(distance/100);
            distanceResult = String.valueOf((double)distance/10);
            distanceResult += " " + resources.getString(R.string.km);

        }

        return distanceResult;

    }

    public static float calculateDistance(Context context, double lat, double lon) {
        float distanceBtwPoints = 0;
        GPSTracker mGPS = new GPSTracker(context);
        Resources resources = context.getResources();//.getString(R.string.)
        if(mGPS.canGetLocation ){
            double mLat=mGPS.getLatitude();
            double mLong=mGPS.getLongitude();
            double eventLat = Double.valueOf(lat);
            double eventLon = Double.valueOf(lon);

            Location loc1 = new Location("");
            loc1.setLatitude(mLat);
            loc1.setLongitude(mLong);

            Location loc2 = new Location("");
            loc2.setLatitude(eventLat);
            loc2.setLongitude(eventLon);

            distanceBtwPoints = loc1.distanceTo(loc2);
        }
        return distanceBtwPoints;
    }

    public static Location getCurrentLocation(Context context) {
        GPSTracker mGPS = new GPSTracker(context);
        if(mGPS.canGetLocation ){
            return mGPS.location;
        }
        return null;
    }

}
