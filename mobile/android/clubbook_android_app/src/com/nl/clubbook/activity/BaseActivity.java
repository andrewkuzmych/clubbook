package com.nl.clubbook.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.*;
import com.nl.clubbook.R;
import com.nl.clubbook.helper.AlertDialogManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Created with IntelliJ IDEA.
 * User: Andrew
 * Date: 5/17/14
 * Time: 8:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class BaseActivity extends ActionBarActivity {
    LinearLayout failedView;
    RelativeLayout mainView;
    //ProgressDialog dialog;
    View contentView;
    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    protected ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();
    private boolean is_retry = false;
    protected AlertDialogManager alert = new AlertDialogManager();
    private ProgressDialog progressDialog;

    public void showProgress(final String string) {
        if(is_retry) {
            contentView.setVisibility(View.GONE);
            failedView.setVisibility(View.GONE);
        }
        BaseActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog = ProgressDialog.show(BaseActivity.this,string,
                        "Loading application View, please wait...", false, true);
            }
        });

    }

    public void hideProgress(boolean showContent) {
        if(is_retry) {
            if(showContent) {
                failedView.setVisibility(View.GONE);
                contentView.setVisibility(View.VISIBLE);
            } else {
                failedView.setVisibility(View.VISIBLE);
                contentView.setVisibility(View.GONE);
            }
        }
        //dialog.dismiss();

        BaseActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                progressDialog.dismiss();
            }
        });

    }

    protected void navigateBack() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // init progress view
        //dialog = new ProgressDialog(this);
       // dialog = new ProgressDialog(this);
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // init image loader
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

    }

    protected void init()
    {
        /*// style header title
        if(findViewById(R.id.header_title) != null) {
            TextView header_title = (TextView) findViewById(R.id.header_title);
            Typeface typefaceHeaderTitle = Typeface.createFromAsset(getAssets(), "fonts/azoft-sans.ttf");
            header_title.setTypeface(typefaceHeaderTitle);
        }

        View back_view = findViewById(R.id.back_button);
        if (back_view != null)
        {
            ImageButton back_button = (ImageButton) back_view;
            back_button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    navigateBack();
                }
            });
        }*/
    }

    protected void setRetryLayout() {
        is_retry = true;
        //dialog = new ProgressDialog(this);
        //mainView =  (RelativeLayout) findViewById(R.id.main_layout);
        failedView = (LinearLayout)getLayoutInflater().inflate(R.layout.retry, null);//new LinearLayout(this);
        //contentView = findViewById(R.id.content_layout);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL|RelativeLayout.CENTER_VERTICAL);

        failedView.setLayoutParams(params);
        failedView.setGravity(Gravity.CENTER);
        failedView.setOrientation(LinearLayout.VERTICAL);
        mainView.addView(failedView);

        setBaseHandlers();
    }

    protected void loadData() {

    }

    private void setBaseHandlers() {
      /*  // back button handler
        ImageButton back_button = (ImageButton) findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                navigateBack();
            }
        });*/

        // retry button handler
        Button retry_button = (Button) findViewById(R.id.retry_button);
        retry_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadData();
            }
        });

        /*// retry button handler
        ImageButton map_button = (ImageButton) findViewById(R.id.map);
        map_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent in = new Intent(getApplicationContext(),
                        EventMapActivity.class);
                in.putExtra("lon", eventDetail.getLon());
                in.putExtra("lat", eventDetail.getLat());
                in.putExtra("title", eventDetail.getTitle());
                startActivity(in);
            }
        });*/

      /*  // share button handler
        ImageButton share_button = (ImageButton) findViewById(R.id.share_button);
        share_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //create the send intent
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
                i.putExtra(Intent.EXTRA_TEXT, "http://www.eventinarea.com");
                startActivity(Intent.createChooser(i, "Share URL"));
            }
        });*/
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        //EasyTracker.getInstance(this).activityStart(this);  // Add this method.
    }

    @Override
    public void onStop() {
        super.onStop();
        //EasyTracker.getInstance(this).activityStop(this);  // Add this method.
    }
}