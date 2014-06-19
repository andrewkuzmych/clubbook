package com.nl.clubbook.activity;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.cloudinary.Cloudinary;
import com.nl.clubbook.R;
import com.nl.clubbook.adapter.NavDrawerListAdapter;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.fragment.*;
import com.nl.clubbook.helper.*;
import com.nl.clubbook.model.NavDrawerItem;
import com.pubnub.api.Callback;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.listeners.OnLogoutListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/19/14
 * Time: 1:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainActivity extends BaseActivity {

    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private SimpleFacebook mSimpleFacebook;
    private Cloudinary cloudinary;
    private Uri mImageCaptureUri;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int DEFOLT_VIEW = 1;

    private CharSequence mTitle;
    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;

    HashMap<Integer, BaseFragment> fragmentMap = new HashMap<Integer, BaseFragment>();

    Callback callback = new Callback() {
        @Override
        public void connectCallback(String channel, Object message) {
            System.out.println("SUBSCRIBE : CONNECT on channel:" + channel
                    + " : " + message.getClass() + " : "
                    + message.toString());
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                    + " : " + message.getClass() + " : "
                    + message.toString());
        }

        public void reconnectCallback(String channel, Object message) {
            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                    + " : " + message.getClass() + " : "
                    + message.toString());
        }

        @Override
        public void successCallback(String channel, final Object message) {
            System.out.println("SUBSCRIBE : " + channel + " : "
                    + message.getClass() + " : " + message.toString());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject messageJson = (JSONObject) message;
                    handleNotification(messageJson);

                }
            });
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                    + " : " + error.toString());
        }
    };

    @Override
    protected void loadData() {

        initNavigationMenu();

        updateMessagesCount();

        displayDefaultView();

    }

    private void initNavigationMenu() {
        // get user data from session
        HashMap<String, String> user = getSession().getUserDetails();

        String user_avatar_url = null;
        if (user.get(SessionManager.KEY_AVATAR) != null)
            user_avatar_url = ImageHelper.getUserAvatar(user.get(SessionManager.KEY_AVATAR));

        // initialize navigation menu
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        mDrawerList.requestFocusFromTouch();

        mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout,
                R.drawable.ic_drawer, //nav menu toggle icon
                R.string.app_name, // nav drawer open - description for accessibility
                R.string.app_name // nav drawer close - description for accessibility
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("");
                // calling onPrepareOptionsMenu() to hide action bar icons
                invalidateOptionsMenu();
            }
        };

        navDrawerItems = new ArrayList<NavDrawerItem>();
        navDrawerItems.add(new NavDrawerItem(user.get(SessionManager.KEY_NAME), user_avatar_url, true));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1), true, "0"));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(3, -1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(3, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();
        mDrawerList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(MainActivity.this, R.layout.drawer_list_item,
                navDrawerItems);
        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case android.R.id.home:
                navigateBack();
                return true;
            case R.id.action_photo:
                final AlertDialog dialog = selectPhoto();
                dialog.show();
                return true;
            case R.id.action_logout:
                getSession().logoutUser();
                mSimpleFacebook.logout(mOnLogoutListener);
                Intent in = new Intent(getApplicationContext(),
                        MainLoginActivity.class);
                startActivity(in);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        // Handle your other action bar items...
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            navigateBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSimpleFacebook = SimpleFacebook.getInstance(this);
    }

    @Override
    protected void navigateBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            current_fragment.backButtonWasPressed();
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String user_id = user.get(SessionManager.KEY_ID);
        subscribeToChannel("message_" + user_id);
    }

    @Override
    public void onStop() {
        super.onStop();
        SessionManager session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        String user_id = user.get(SessionManager.KEY_ID);
        NotificationHelper.pubnub.unsubscribe("message_" + user_id);
    }

    public BaseFragment getCurrentFragment() {
        return current_fragment;
    }

    private void handleNotification(JSONObject messageJson) {
        try {
            if (getCurrentFragment() instanceof ChatFragment && messageJson.getString("type").equalsIgnoreCase("chat")) {
                ChatFragment chatFragment = (ChatFragment) getCurrentFragment();
                String userTo = messageJson.getString("user_to");
                String userFrom = messageJson.getString("user_from");
                SessionManager session = new SessionManager(this);
                if (session.getConversationListner() != null && session.getConversationListner().equalsIgnoreCase(userFrom + "_" + userTo)) {
                    chatFragment.addComment(messageJson.getString("msg"));
                } else {
                    updateMessagesCount();
                }

            } else {
                updateMessagesCount();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentFragment(BaseFragment current_fragment) {

        this.current_fragment = current_fragment;
    }

    public DrawerLayout getDrawerLayout() {
        return mDrawerLayout;
    }

    public ActionBarDrawerToggle getDrawerToggle() {
        return mDrawerToggle;
    }

    public void setDefoltTitle() {
        setTitle(navMenuTitles[DEFOLT_VIEW]);
    }

    private OnLogoutListener mOnLogoutListener = new OnLogoutListener() {
        @Override
        public void onLogout() {

        }

        @Override
        public void onThinking() {

        }

        @Override
        public void onException(Throwable throwable) {

        }

        @Override
        public void onFail(String reason) {

        }
    };

    private void displayDefaultView() {
        Intent in = getIntent();
        int displayView = DEFOLT_VIEW;
        if (in.hasExtra("type")) {
            if (in.getStringExtra("type").equalsIgnoreCase("chat"))
                displayView = 3;
        }

        displayView(displayView);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // if user not logged - navigate to login activity
        if (!getSession().isLoggedIn())
        {
            Intent i = new Intent(getApplicationContext(), MainLoginActivity.class);
            startActivity(i);
            return;
        }

        init();

        cloudinary = new Cloudinary(getApplicationContext());
        mTitle = getTitle();
        fragmentMap.put(0, new ProfileFragment());
        fragmentMap.put(1, new HomeFragment());
        fragmentMap.put(2, new ClubFragment());
        fragmentMap.put(3, new MessagesFragment());
        fragmentMap.put(4, new FriendsFragment());
        fragmentMap.put(5, new SettingsFragment());

        getSupportActionBar().setIcon(
                new ColorDrawable(getResources().getColor(android.R.color.transparent)));

        loadData();
    }

    private AlertDialog selectPhoto() {
        final String[] items = new String[]{"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { //pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });

        return builder.create();
    }

    public void updateMessagesCount() {
        if (getCurrentFragment() instanceof MessagesFragment) {
            ((MessagesFragment) getCurrentFragment()).loadData(false);
        }

        DataStore.unread_messages_count(getCurrentUserId(), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (!failed) {
                    setMessageCount(Integer.parseInt((String) result));
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                doCrop();
                break;
            case PICK_FROM_FILE:
                // mImageCaptureUri = imageReturnedIntent.getData();
                //                doCrop();
                final Uri selectedImage = imageReturnedIntent.getData();
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            Bitmap mBitmap = readBitmap(selectedImage);
                            Bitmap scaled = getResizedBitmap(mBitmap, 800);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            InputStream is = new ByteArrayInputStream(stream.toByteArray());
                            cloudinary.uploader().upload(is, Cloudinary.asMap("public_id", "test6", "format", "jpg"));
                        } catch (Exception ex) {
                            //TODO: handle the exception
                        }
                    }
                });

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = imageReturnedIntent.getExtras();

                if (extras != null) {
                    Bitmap photo = extras.getParcelable("data");
                    //mImageView.setImageBitmap(photo);

                    // Bitmap  mBitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedImage);
                    Bitmap scaled = getResizedBitmap(photo, 800);//Bitmap.createScaledBitmap(mBitmap, 500, 500, true);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    InputStream is = new ByteArrayInputStream(stream.toByteArray());

                    try {
                        cloudinary.uploader().upload(is, Cloudinary.asMap("public_id", "test6", "width", "1000", "height", "1000", "crop", "limit", "format", "jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                File f = new File(mImageCaptureUri.getPath());

                if (f.exists())
                    f.delete();

                break;

        }
    }

    private Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 0);
            intent.putExtra("aspectY", 0);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    private void displayView(final int position) {
        // update the main content by replacing fragments
        Fragment fragment = fragmentMap.get(position);

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction mFragmentTransaction = fragmentManager.beginTransaction();


            mFragmentTransaction.replace(R.id.frame_container, fragment);
            mFragmentTransaction.commit();

            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);

        } else {
            // error in creating fragment
            Log.e("MainActivity", "Error in creating fragment");
        }
    }

    private Bitmap readBitmap(Uri selectedImage) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = this.getContentResolver().openAssetFileDescriptor(selectedImage, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                fileDescriptor.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

    private void subscribeToChannel(String channel_name) {
        try {
            NotificationHelper.pubnub.subscribe(channel_name, callback);
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }
    }

    private void setMessageCount(Integer count) {
        if (count == 0) {
            navDrawerItems.get(3).setCounterVisibility(false);
            return;
        } else {
            navDrawerItems.get(3).setCounterVisibility(true);
            navDrawerItems.get(3).setCount(String.valueOf(count));
            adapter = new NavDrawerListAdapter(this, R.layout.drawer_list_item,
                    navDrawerItems);
            mDrawerList.setAdapter(adapter);
        }
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements
            ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            view.setSelected(true);
            // display view for selected nav drawer item
            displayView(position);
        }
    }

}

