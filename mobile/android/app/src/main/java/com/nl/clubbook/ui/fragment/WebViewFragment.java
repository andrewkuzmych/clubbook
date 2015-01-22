package com.nl.clubbook.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 20.08.2014.
 */
public class WebViewFragment extends BaseInnerFragment {

    public static final String TAG = "WebViewFragment";
    private static final String ARG_SCREEN_TITLE = "ARG_SCREEN_TITLE";
    private static final String ARG_URL = "ARG_URL";

    public static Fragment newInstance(Fragment targetFragment, String screenTitle, String url) {
        Fragment fragment = new WebViewFragment();
        fragment.setTargetFragment(targetFragment, 0);

        Bundle args = new Bundle();
        args.putString(ARG_SCREEN_TITLE, screenTitle);
        args.putString(ARG_URL, url);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_web_view, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        View view = getView();
        Bundle args = getArguments();
        if(args == null || view == null) {
            return;
        }

        String screenTitle = args.getString(ARG_SCREEN_TITLE);
        String url = args.getString(ARG_URL);

        initActionBarTitle(screenTitle != null ? screenTitle : "");

        if(url != null) {
            WebView webView = (WebView) view.findViewById(R.id.webView);
            webView.loadUrl(url);
        }
    }
}
