package com.nl.clubbook.helper;

import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andrew on 5/27/2014.
 */
public class LocationCheckinHelper {

    public static final int MAX_RADIUS = 200;
    private static ScheduledExecutorService scheduleTaskExecutor;
    private static ClubDto current_club;
    private static int failed_checkin_count = 0;
    private static int max_failed_checkin_count = 3;
    private static int update_location_interval = 10*60; // every 10min.

    public static String getCurrentClubLat(Context context) {
        String result = null;
        if (current_club != null)
            result = current_club.getLat();

        return result;
    }

    public static String getCurrentClubLon(Context context) {
        String result = null;
        if (current_club != null)
            result = current_club.getLon();

        return result;
    }

    public static boolean isCheckinHere(Context context, ClubDto club) {
        if (current_club == null)
            return false;
        if (club == null)
            return false;

        if (club.getId().equalsIgnoreCase(current_club.getId()))
            return true;

        return false;
    }

    public static void checkin(final Context context, final ClubDto club, final CheckInOutCallbackInterface callback) {
        final SessionManager session = new SessionManager(context.getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();
        final Location current_location = getBestLocation(context);
        double distance = distanceBwPoints(current_location.getLatitude(), current_location.getLongitude(), Double.parseDouble(club.getLat()), Double.parseDouble(club.getLon()));

        if (distance > MAX_RADIUS) {
            callback.onCheckInOutFinished(false);
            return;
        }

        DataStore.checkin(club.getId(), user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    //hideProgress(false);
                    callback.onCheckInOutFinished(false);
                    return;
                }

                current_club = club;
                callback.onCheckInOutFinished(true);
                StartLocationUpdate(context);
            }
        });
    }

    public static void checkout(final Context context, final CheckInOutCallbackInterface callback) {
        final SessionManager session = new SessionManager(context.getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();

        DataStore.checkout(current_club.getId(), user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    //hideProgress(false);
                    callback.onCheckInOutFinished(false);
                    return;
                }
                callback.onCheckInOutFinished(true);
            }
        });

        current_club = null;
        if (scheduleTaskExecutor != null)
            scheduleTaskExecutor.shutdown();
    }

    private static double distanceBwPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat_a);
        loc1.setLongitude(lng_a);

        Location loc2 = new Location("");
        loc2.setLatitude(lat_b);
        loc2.setLongitude(lng_b);

        return loc1.distanceTo(loc2);
    }

    private static void StartLocationUpdate(final Context context) {
        final SessionManager session = new SessionManager(context.getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                final Location current_location = getBestLocation(context);
                final double distance = distanceBwPoints(current_location.getLatitude(), current_location.getLongitude(), Double.parseDouble(getCurrentClubLat(context)), Double.parseDouble(getCurrentClubLon(context)));

                ((BaseActivity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        if (distance > MAX_RADIUS) {
                            checkout(context, new CheckInOutCallbackInterface() {
                                @Override
                                public void onCheckInOutFinished(boolean result) {
                                    // Do something when finished
                                }
                            });
                        } else {
                            // update your UI component here.
                            DataStore.updateCheckin(current_club.getId(), user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
                                @Override
                                public void onReady(Object result, boolean failed) {
                                    if (failed) {
                                        failed_checkin_count += 1;
                                        if (failed_checkin_count >= max_failed_checkin_count)
                                            checkout(context, new CheckInOutCallbackInterface() {
                                                @Override
                                                public void onCheckInOutFinished(boolean result) {
                                                    // Do something when finished
                                                }
                                            });
                                    } else {
                                        failed_checkin_count = 0;
                                    }

                                }
                            });

                        }
                    }
                });
            }
        }, 0, update_location_interval, TimeUnit.SECONDS);
    }

    public static Location getBestLocation(Context context) {
        Location gpslocation = getLocationByProvider(context, LocationManager.GPS_PROVIDER);
        Location networkLocation =
                getLocationByProvider(context, LocationManager.NETWORK_PROVIDER);
        // if we have only one location available, the choice is easy
        if (gpslocation == null) {
            return networkLocation;
        }
        if (networkLocation == null) {
            return gpslocation;
        }
        // a locationupdate is considered 'old' if its older than the configured
        // update interval. this means, we didn't get a
        // update from this provider since the last check
        long old = System.currentTimeMillis() - 1000 * 60 * 10;
        boolean gpsIsOld = (gpslocation.getTime() < old);
        boolean networkIsOld = (networkLocation.getTime() < old);
        // gps is current and available, gps is better than network
        if (!gpsIsOld) {
            //Log.d(TAG, "Returning current GPS Location");
            return gpslocation;
        }
        // gps is old, we can't trust it. use network location
        if (!networkIsOld) {
            return networkLocation;
        }
        // both are old return the newer of those two
        if (gpslocation.getTime() > networkLocation.getTime()) {
            //Log.d(TAG, "Both are old, returning gps(newer)");
            return gpslocation;
        } else {
            // Log.d(TAG, "Both are old, returning network(newer)");
            return networkLocation;
        }
    }

    /**
     * get the last known location from a specific provider (network/gps)
     */
    private static Location getLocationByProvider(Context context, String provider) {
        Location location = null;
        LocationManager locationManager = (LocationManager) context.getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);

        try {
            if (locationManager.isProviderEnabled(provider)) {
                location = locationManager.getLastKnownLocation(provider);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            //Log.d(TAG, "Cannot acces Provider " + provider);
        }
        return location;
    }

    public static String calculateDistance(Context context, float distance) {
        Resources resources = context.getResources();
        String distanceResult = "?";

        if (distance < 1000) {
            int distanceBtwPointsInt = Math.round(distance / 10) * 10;
            distanceResult = String.valueOf(distanceBtwPointsInt);
            distanceResult += " " + resources.getString(R.string.m);
        } else {
            distance = Math.round(distance / 100);
            distanceResult = String.valueOf((double) distance / 10);
            distanceResult += " " + resources.getString(R.string.km);

        }
        return distanceResult;
    }

    public static float calculateDistance(Context context, double lat, double lon) {
        float distanceBtwPoints = 0;
        Location current_location = getBestLocation(context);
        if (current_location != null) {
            double mLat = current_location.getLatitude();
            double mLong = current_location.getLongitude();
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
}
