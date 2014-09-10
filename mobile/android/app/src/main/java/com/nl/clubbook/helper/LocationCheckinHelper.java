package com.nl.clubbook.helper;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.activity.MainLoginActivity;
import com.nl.clubbook.activity.NoLocationActivity;
import com.nl.clubbook.datasource.ClubDto;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.utils.L;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Andrew on 5/27/2014.
 */
public class LocationCheckinHelper {

    private ScheduledExecutorService scheduleTaskExecutor;
    private int failedCheckInCount = 0;

    // current user location, updated every 10sec
    private Location currentLocation;
    private final int updateLocationInterval = 0;
    private Boolean isLocationTrackerStarted = false;
    private boolean mIsListenerRemoved = false;
    private LocationManager mLocationManager;

    private ClubDto mCurrentClub;
    private LocationListener mLocationListener;
    private boolean mShouldHideLocationErrorView = false;

    private static LocationCheckinHelper mCheckInHelper;

    public static void init() {
        if(mCheckInHelper == null) {
            mCheckInHelper = new LocationCheckinHelper();
        }

        mCheckInHelper.setCurrentClub(SessionManager.getInstance().getCheckedInClubInfo());
    }

    public static LocationCheckinHelper getInstance() {
        if(mCheckInHelper == null) {
            throw new IllegalArgumentException(LocationCheckinHelper.class.getSimpleName() + " is not initialized, call init() method in your application class");
        }

        return mCheckInHelper;
    }

    public static void clear() {
        mCheckInHelper.clearCheckedInClubInfo();
    }

    private LocationCheckinHelper() {
    }

    public ClubDto getCurrentClub() {
        return mCurrentClub;
    }

    public void setCurrentClub(ClubDto currentClub) {
        mCurrentClub = currentClub;
        SessionManager.getInstance().putCheckedInClubInfo(currentClub);
    }

    public void clearCheckedInClubInfo() {
        mCurrentClub = null;
        SessionManager.getInstance().clearCheckInClubInfo();
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public boolean isCheckInHere(ClubDto club) {
        if (mCurrentClub == null || club == null) {
            return false;
        }

        if (club.getId().equalsIgnoreCase(mCurrentClub.getId())) {
            return true;
        }

        return false;
    }

    public boolean isCheckIn() {
        return (mCurrentClub != null && mCurrentClub.getId() != null);
    }

    /**
     * Check location and distance to club to allow check in
     */
    public boolean canCheckInHere(ClubDto club) {
        if (club == null || currentLocation == null) {
            return false;
        }

        Double distance = distanceBwPoints(currentLocation.getLatitude(), currentLocation.getLongitude(), club.getLat(), club.getLon());
        int maxCheckInDistance = SessionManager.getInstance().getCheckInMaxDistance();

        return distance < maxCheckInDistance;
    }

    /**
     * Set current club
     */
    public void checkIn(final Context context, final ClubDto club, final CheckInOutCallbackInterface callback) {
        int maxCheckInDistance = SessionManager.getInstance().getCheckInMaxDistance();

        // location validation
        final Location current_location = getCurrentLocation();
        double distance = distanceBwPoints(current_location.getLatitude(), current_location.getLongitude(), club.getLat(), club.getLon());
        if (distance > maxCheckInDistance) {
            callback.onCheckInOutFinished(false);
            return;
        }

        SessionManager sessionManager = SessionManager.getInstance();
        DataStore.checkin(club.getId(), sessionManager.getAccessToken(), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    // error occurred
                    callback.onCheckInOutFinished(false);
                } else {
                    setCurrentClub(club);
                    callback.onCheckInOutFinished(true);
                    startLocationUpdate(context);

                    //sent broadcast
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_CHECK_IN_CHECK_OUT);
                    context.sendBroadcast(intent);
                }
            }
        });
    }

    /**
     * Check out user from club and set currentClub to null
     */
    public void checkOut(final Context context, final CheckInOutCallbackInterface callback) {
        L.d("Try to Check out user");

        SessionManager sessionManager = SessionManager.getInstance();
        DataStore.checkout(mCurrentClub.getId(), sessionManager.getAccessToken(),
                new DataStore.OnResultReady() {

            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    callback.onCheckInOutFinished(false);
                } else {
                    L.d("Checked out from club");
                    clearCheckedInClubInfo();
                    callback.onCheckInOutFinished(true);

                    //sent broadcast
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_CHECK_IN_CHECK_OUT);
                    context.sendBroadcast(intent);

                    cancelLocationUpdates(context);
                }
            }
        });

        // stop check in task
        scheduleTaskExecutor.shutdown();
    }

    /**
     * Measure distance between points
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
     */
    public void startLocationUpdate(final Context context) {
        final SessionManager sessionManager = SessionManager.getInstance();
        int updateCheckInStatusInterval = sessionManager.getUpdateCheckInStatusInterval();

        scheduleTaskExecutor = Executors.newScheduledThreadPool(1);
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                final Location current_location = getCurrentLocation();
                final double distance = distanceBwPoints(current_location.getLatitude(), current_location.getLongitude(),
                        getCurrentClub().getLat(), getCurrentClub().getLon());

                final int maxCheckInDistance = sessionManager.getCheckInMaxDistance();
                final int maxFailedCheckInCount = sessionManager.getMaxFailedCheckInCount();

                ((BaseActivity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        if (distance > maxCheckInDistance) {
                            // checkout user
                            checkOut(context, new CheckInOutCallbackInterface() {
                                @Override
                                public void onCheckInOutFinished(boolean result) {
                                    // user was checked out
                                }
                            });
                        } else {
                            // update time of last presence of user near a club at sever side
                            DataStore.updateCheckin(mCurrentClub.getId(), sessionManager.getAccessToken(), new DataStore.OnResultReady() {
                                @Override
                                public void onReady(Object result, boolean failed) {
                                    if (failed) {
                                        failedCheckInCount += 1;
                                        if (failedCheckInCount >= maxFailedCheckInCount) {
                                            // checkout user
                                            checkOut(context, new CheckInOutCallbackInterface() {
                                                @Override
                                                public void onCheckInOutFinished(boolean result) {
                                                    // user was checked out
                                                }
                                            });
                                        }
                                    } else {
                                        failedCheckInCount = 0;
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }, 0, updateCheckInStatusInterval, TimeUnit.SECONDS);
    }

    /**
     * Format distance
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
     */
    public float calculateDistance(double lat, double lon) {
        float distanceBtwPoints = 0;
        Location currentLocation = getCurrentLocation();
        if (currentLocation != null) {
            double mLat = currentLocation.getLatitude();
            double mLong = currentLocation.getLongitude();
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
     */
    public void startSmartLocationTracker(final Context context) {
        // launch only once
        if (!isLocationTrackerStarted) {
            isLocationTrackerStarted = true;
            mIsListenerRemoved = false;

            // http://developer.android.com/guide/topics/location/strategies.html
            // Acquire a reference to the system Location Manager
            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

            currentLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (currentLocation == null)
                currentLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            // Define a listener that responds to location updates
            mLocationListener = new LocationListener() {


                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    if (currentLocation == null) {
                        currentLocation = location;
                    }

                    if (isBetterLocation(currentLocation, location)) {
                        currentLocation = location;
                    }

                    if(mShouldHideLocationErrorView) {
                        hideLocationErrorView(context);
                    }

                    L.d("LOCATION - " + String.valueOf(currentLocation.getLatitude()) + ":" + String.valueOf(currentLocation.getLongitude()));
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    L.d("LOCATION - " + "status changed: " + provider);
                }

                public void onProviderEnabled(String provider) {
                    Intent intent = new Intent(NoLocationActivity.ACTION_LOCATION_PROVIDER_ENABLED);
                    context.sendBroadcast(intent);
                }

                public void onProviderDisabled(String provider) {
                    if (!isLocationProvidersEnabled(mLocationManager) && !mIsListenerRemoved) {
                        showLocationErrorView(context, false);
                    }
                }
            };

            // Register the listener with the Location Manager to receive location updates
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, updateLocationInterval, 200, mLocationListener);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateLocationInterval, 200, mLocationListener);
        }
    }

    public void cancelLocationUpdates(Context context) {
        mIsListenerRemoved = true;

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(mLocationListener != null) {
            locationManager.removeUpdates(mLocationListener);
            mLocationListener = null;
        }
    }

    public  void showLocationErrorView(Context application, boolean showProgress) {
        Intent intent = new Intent(application, NoLocationActivity.class);
        intent.putExtra(NoLocationActivity.EXTRA_IS_PROGRESS_ENABLED, showProgress);
        application.startActivity(intent);
        ((BaseActivity) application).finish();

        mShouldHideLocationErrorView = true;
    }

    private void hideLocationErrorView(final Context application) {
        mShouldHideLocationErrorView = false;

        Intent actionCloseIntent = new Intent();
        actionCloseIntent.setAction(NoLocationActivity.ACTION_CLOSE);
        application.sendBroadcast(actionCloseIntent);

        Intent intent = new Intent(application, MainLoginActivity.class);
        application.startActivity(intent);
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

    public boolean isLocationEnabled(final Context application) {
        boolean isLocationEnabled = true;

        final LocationManager locationManager = (LocationManager) application.getSystemService(Context.LOCATION_SERVICE);
        currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (currentLocation == null)
            currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        //if there is no providers redirect go to location error view
        if (!isLocationProvidersEnabled(locationManager) && currentLocation == null) {
            // stop app
            L.d("LOCATION - " + "No location services enabled");
            // showLocationErrorView(application);
            isLocationEnabled = false;
        }

        return isLocationEnabled;
    }

    public boolean isLocationProvidersEnabled() {
        return isLocationProvidersEnabled(mLocationManager);
    }

    public boolean isLocationProvidersEnabled(LocationManager locationManager) {
        if(locationManager == null) {
            return false;
        }

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
