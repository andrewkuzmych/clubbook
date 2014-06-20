package com.nl.clubbook.helper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainLoginActivity;
import com.nl.clubbook.activity.NoLocationActivity;
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
    private static int failed_checkin_count = 0;
    private static int max_failed_checkin_count = 3;
    private static int update_location_interval = 10 * 60; // every 10min.
    // current active club when user did checkin
    private static ClubDto currentClub;
    // current user location, updated every 10sec
    private static Location currentLocation;
    private static Boolean isLocationTrackerStarted = false;

    public static ClubDto getCurrentClub() {
        return LocationCheckinHelper.currentClub;
    }

    public static void setCurrentClub(ClubDto currentClub) {
        LocationCheckinHelper.currentClub = currentClub;
    }

    public static Location getCurrentLocation() {
        if (currentLocation == null)
            throw new RuntimeException("Current location is empty");
        return currentLocation;
    }

    public static void setCurrentLocation(Location currentLocation) {
        LocationCheckinHelper.currentLocation = currentLocation;
    }

    /**
     * Check if user is currently checkin in this club
     *
     * @param club
     * @return
     */
    public static boolean isCheckinHere(ClubDto club) {
        if (getCurrentClub() == null)
            return false;
        if (club == null)
            return false;

        if (club.getId().equalsIgnoreCase(getCurrentClub().getId()))
            return true;

        return false;
    }

    /**
     * Check location and distance to club to allow checkin
     *
     * @param club
     * @return
     */
    public static boolean canCheckinHere(ClubDto club) {
        if (club == null)
            return false;

        Double distance = distanceBwPoints(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude(), club.getLat(), club.getLon());

        return distance < MAX_RADIUS;
    }

    /**
     * Set current club
     *
     * @param context
     * @param club
     * @param callback
     */
    public static void checkin(final Context context, final ClubDto club, final CheckInOutCallbackInterface callback) {
        final SessionManager session = new SessionManager(context.getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();
        final Location current_location = getCurrentLocation();
        double distance = distanceBwPoints(current_location.getLatitude(), current_location.getLongitude(), club.getLat(), club.getLon());

        // location validation
        if (distance > MAX_RADIUS) {
            callback.onCheckInOutFinished(false);
            return;
        }

        DataStore.checkin(club.getId(), user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    // error occurred
                    callback.onCheckInOutFinished(false);
                } else {
                    setCurrentClub(club);
                    callback.onCheckInOutFinished(true);
                    StartLocationUpdate(context);
                }
            }
        });
    }

    /**
     * Chekout user from club and set currentClub to null
     *
     * @param context
     * @param callback
     */
    public static void checkout(final Context context, final CheckInOutCallbackInterface callback) {
        final SessionManager session = new SessionManager(context.getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();

        DataStore.checkout(getCurrentClub().getId(), user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    callback.onCheckInOutFinished(false);
                } else {
                    callback.onCheckInOutFinished(true);
                }
            }
        });

        setCurrentClub(null);

        if (scheduleTaskExecutor != null)
            scheduleTaskExecutor.shutdown();
    }

    /**
     * Measure distance between points
     *
     * @param lat_a
     * @param lng_a
     * @param lat_b
     * @param lng_b
     * @return
     */
    private static double distanceBwPoints(double lat_a, double lng_a, double lat_b, double lng_b) {
        Location loc1 = new Location("");
        loc1.setLatitude(lat_a);
        loc1.setLongitude(lng_a);

        Location loc2 = new Location("");
        loc2.setLatitude(lat_b);
        loc2.setLongitude(lng_b);

        return loc1.distanceTo(loc2);
    }

    /**
     * Every 10min check if user is near the club. If he is more 200m from the club execute checkout.
     *
     * @param context
     */
    private static void StartLocationUpdate(final Context context) {
        final SessionManager session = new SessionManager(context.getApplicationContext());
        final HashMap<String, String> user = session.getUserDetails();

        scheduleTaskExecutor = Executors.newScheduledThreadPool(5);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                final Location current_location = getCurrentLocation();
                final double distance = distanceBwPoints(current_location.getLatitude(), current_location.getLongitude(),
                        getCurrentClub().getLat(), getCurrentClub().getLon());

                ((BaseActivity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        if (distance > MAX_RADIUS) {
                            checkout(context, new CheckInOutCallbackInterface() {
                                @Override
                                public void onCheckInOutFinished(boolean result) {
                                    // user was checked out
                                }
                            });
                        } else {
                            // update time of last presence of user near a club at sever side
                            DataStore.updateCheckin(currentClub.getId(), user.get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
                                @Override
                                public void onReady(Object result, boolean failed) {
                                    if (failed) {
                                        failed_checkin_count += 1;
                                        if (failed_checkin_count >= max_failed_checkin_count) {
                                            // checkout user
                                            checkout(context, new CheckInOutCallbackInterface() {
                                                @Override
                                                public void onCheckInOutFinished(boolean result) {
                                                    // user was checked out
                                                }
                                            });
                                        }
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

    /**
     * Format distance
     *
     * @param context
     * @param distance
     * @return
     */
    public static String formatDistance(Context context, float distance) {
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

    /**
     * Calculate distance between my location and club
     *
     * @param lat
     * @param lon
     * @return
     */
    public static float calculateDistance(double lat, double lon) {
        float distanceBtwPoints = 0;
        Location current_location = getCurrentLocation();
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

    /**
     * Track user location
     *
     * @param application
     */
    public static void startSmartLocationTracker(final Context application) {
        // launch only once
        if (!isLocationTrackerStarted) {
            isLocationTrackerStarted = true;

            // http://developer.android.com/guide/topics/location/strategies.html
            // Acquire a reference to the system Location Manager
            final LocationManager locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);

            currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation == null)
                currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // Define a listener that responds to location updates
            LocationListener locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    if (currentLocation == null) {
                        currentLocation = location;
                    }

                    if (isBetterLocation(currentLocation, location)) {
                        currentLocation = location;
                    }

                    Log.d("LOCATION", String.valueOf(currentLocation.getLatitude()) + ":" + String.valueOf(currentLocation.getLongitude()));
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d("LOCATION", "status changed: " + provider);
                }

                public void onProviderEnabled(String provider) {
                    hideLocationErrorView(application);
                }

                public void onProviderDisabled(String provider) {
                    if (!isLocationProvidersEnabled(locationManager)) {
                        showLocationErrorView(application);
                    }
                }
            };

            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 200, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 200, locationListener);
        }
    }

    private static void showLocationErrorView(final Context application) {
        Intent i = new Intent(application, NoLocationActivity.class);
        application.startActivity(i);
        ((BaseActivity) application).finish();
    }

    private static void hideLocationErrorView(final Context application) {
        Intent i = new Intent(application, MainLoginActivity.class);
        application.startActivity(i);
        ((BaseActivity) application).finish();
    }

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /**
     * Determines whether one Location reading is better than the current Location fix
     *
     * @param location            The new Location that you want to evaluate
     * @param currentBestLocation The current Location fix, to which you want to compare the new one
     */
    protected static boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether two providers are the same
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    public static boolean isLocationEnabled(final Context application) {
        boolean isLocationEnabled = true;

        final LocationManager locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation == null)
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //if there is no providers redirect go to location error view
        if (!isLocationProvidersEnabled(locationManager) && currentLocation == null) {
            // stop app
            Log.d("LOCATION", "No location services enabled");
            // showLocationErrorView(application);
            isLocationEnabled = false;
        }

        return isLocationEnabled;
    }

    protected static boolean isLocationProvidersEnabled(LocationManager locationManager) {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
