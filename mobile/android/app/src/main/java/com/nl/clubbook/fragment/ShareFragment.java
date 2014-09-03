package com.nl.clubbook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nl.clubbook.R;

/**
 * Created by Volodymyr on 21.08.2014.
 */
public class ShareFragment extends BaseFragment implements View.OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fr_share, null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initActionBarTitle(getString(R.string.clubs));
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtShareOnFacebook:
                onShareOnFacebookClicked();
                break;
            case R.id.txtTweetAboutUs:
                onTweetAboutUsClicked();
                break;
            case R.id.txtLikeUsOnFacebook:
                onLikeUsOnFacebookClicked();
                break;
            case R.id.txtFollowOurTweets:
                onFollowOurTweetsClicked();
                break;
        }
    }

    private void initView() {
        View view = getView();
        if(view == null) {
            return;
        }

        view.findViewById(R.id.txtShareOnFacebook).setOnClickListener(this);
        view.findViewById(R.id.txtTweetAboutUs).setOnClickListener(this);
        view.findViewById(R.id.txtLikeUsOnFacebook).setOnClickListener(this);
        view.findViewById(R.id.txtFollowOurTweets).setOnClickListener(this);
    }

    private void onShareOnFacebookClicked() {
        //TODO
    }

    private void onTweetAboutUsClicked() {
        //TODO
    }

    private void onLikeUsOnFacebookClicked() {
        //TODO
    }

    private void onFollowOurTweetsClicked() {
        //TODO
    }
}
