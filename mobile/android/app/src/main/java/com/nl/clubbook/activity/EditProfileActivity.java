package com.nl.clubbook.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;

import com.nl.clubbook.R;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.datasource.UserPhotoDto;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.ImageUploader;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.helper.UiHelper;
import com.nl.clubbook.utils.NetworkUtils;
import com.nl.clubbook.utils.UIUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class EditProfileActivity extends BaseDateActivity implements View.OnClickListener {

    public static final int REQUEST_CODE = 6364;

    private UserPhotoDto selectedImageDto;
    private ImageUploader imageUploader;
    private UserDto profile;

    private ImageLoader mImageLoader;
    private DisplayImageOptions mOptions;
    private ImageLoadingListener mAnimateFirstListener = new SimpleImageLoadingListener();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_edit_profile);

        initImageHelpers();
        initView();

        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageUploader.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.imgAddNewPhoto:
                onAddNewPhotoClicked();
                break;
            case R.id.editBirthDate:
                onBirthDateClicked();
                break;
            case R.id.txtSetAsDefault:
                onSetAsDefaultClicked();
                break;
            case R.id.txtDeleteImage:
                onDeleteImageClicked();
                break;
            case R.id.txtSave:
                onSaveClicked();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        initActionBar(R.string.edit_user_profile);

        findViewById(R.id.imgAddNewPhoto).setOnClickListener(this);
        findViewById(R.id.editBirthDate).setOnClickListener(this);
        findViewById(R.id.txtSetAsDefault).setOnClickListener(this);
        findViewById(R.id.txtDeleteImage).setOnClickListener(this);
        findViewById(R.id.txtSave).setOnClickListener(this);
    }

    private void onSaveClicked() {
        String errorTitle = "Update profile failed";

        //username
        EditText editName = (EditText)findViewById(R.id.editName);
        final String userName = editName.getText().toString().trim();
        if (userName.length() < 2) {
            alert.showAlertDialog(EditProfileActivity.this, errorTitle, getString(R.string.name_incorrect));
            return;
        }

        //birth date
        EditText editBirthDate = (EditText) findViewById(R.id.editBirthDate);
        final String birthDate = editBirthDate.getText().toString().trim();
        if (birthDate.length() < 6) {
            alert.showAlertDialog(EditProfileActivity.this, errorTitle, getString(R.string.dob_incorrect));
            return;
        }

        //about me
        EditText editAboutMe = (EditText) findViewById(R.id.editAboutMe);
        final String aboutMe = editAboutMe.getText().toString().trim();

        //gender
        Spinner spinGender = (Spinner) findViewById(R.id.spinGender);
        UiHelper.TextValuePair dataGender = (UiHelper.TextValuePair) spinGender.getSelectedItem();
        final String gender = dataGender.getValue();

        //country
        Spinner spinCountry = (Spinner) findViewById(R.id.spinCountry);
        UiHelper.TextValuePair dataCountry = (UiHelper.TextValuePair) spinCountry.getSelectedItem();
        final String country = dataCountry.getValue();

        //upload data
        showProgress(getString(R.string.loading));
        DataStore.updateUserProfile(getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN),
                userName, gender, mServerFormat.format(mBirthDate), country, aboutMe, new DataStore.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {
                        if (failed) {
                            hideProgress(false);
                            return;
                        }

                        SessionManager sessionManager = getSession();
                        sessionManager.updateValue(SessionManager.KEY_NAME, userName);
                        sessionManager.updateValue(SessionManager.KEY_BIRTHDAY, birthDate);
                        sessionManager.updateValue(SessionManager.KEY_ABOUT_ME, aboutMe);
                        sessionManager.updateValue(SessionManager.KEY_GENDER, gender);
                        sessionManager.updateValue(SessionManager.KEY_COUNTRY, country);

                        hideProgress(true);

                        setResult(RESULT_OK);
                        finish();
                    }
                }
        );
    }

    private void initImageHelpers() {
        mImageLoader = ImageLoader.getInstance();

        mOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.ic_avatar_missing)
                .showImageForEmptyUri(R.drawable.ic_avatar_missing)
                .showImageOnFail(R.drawable.ic_avatar_unknown)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        imageUploader = new ImageUploader(this) {
            @Override
            public void startActivityForResultHolder(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }

            @Override
            public void onImageSelected(JSONObject imageObj) throws JSONException {
                addImage(imageObj);
            }
        };
    }

    protected void loadData() {
        if(!NetworkUtils.isOn(EditProfileActivity.this)) {
            showNoInternetActiity();
            return;
        }

        setLoading(true);

        DataStore.retrieveUser(getSession().getUserDetails().get(SessionManager.KEY_ACCESS_TOCKEN), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if(isFinishing()) {
                    return;
                }

                if (failed) {
                    showNoInternetActiity();
                    return;
                }

                setLoading(false);

                profile = (UserDto) result;

                // update UI components
                EditText editBirthDate = (EditText) findViewById(R.id.editBirthDate);
                EditText editName = (EditText) findViewById(R.id.editName);
                EditText editAboutMe = (EditText) findViewById(R.id.editAboutMe);

                String birthDate = profile.getDob();
                if(birthDate != null && birthDate.length() > 0) {
                    parseBirthDate(birthDate);

                    editBirthDate.setText(mDisplayFormat.format(mBirthDate));
                }

                UiHelper.createGenderSpinner((Spinner) findViewById(R.id.spinGender), EditProfileActivity.this, profile.getGender());
                UiHelper.createCountrySpinner((Spinner) findViewById(R.id.spinCountry), EditProfileActivity.this, profile.getCountry());
                editName.setText(profile.getName());
                editAboutMe.setText(profile.getBio());

                drawImageManager(profile.getPhotos());
            }
        });
    }

    private void drawImageManager(List<UserPhotoDto> userPhotoDtoList) {
        LinearLayout holderUsersPhotos = (LinearLayout) findViewById(R.id.holderUserPhotos);
        holderUsersPhotos.removeAllViews();
        for (UserPhotoDto userPhotoDto : userPhotoDtoList) {
            displayImageSmallPreview(holderUsersPhotos, userPhotoDto);
            if(userPhotoDto.getIsAvatar()) {
                displayImageBigPreview(userPhotoDto);
            }
        }
    }

    private void displayImageSmallPreview(LinearLayout holderUserPhotos, final UserPhotoDto imageDto) {
        int widthAndHeightInPixels = (int) UIUtils.dipToPixels(EditProfileActivity.this, 75f);
        LayoutParams layoutParams = new LayoutParams(widthAndHeightInPixels, widthAndHeightInPixels);

        ImageView image = new ImageView(EditProfileActivity.this);
        image.setLayoutParams(layoutParams);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holderUserPhotos.addView(image, 0);

        mImageLoader.displayImage(ImageHelper.getUserPhotoSmallPreview(imageDto.getUrl()), image, mOptions, mAnimateFirstListener);
        image.setTag(imageDto);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayImageBigPreview(imageDto);
            }
        });
    }

    private void displayImageBigPreview(UserPhotoDto imageDto) {
        selectedImageDto = imageDto;

        ImageView imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        mImageLoader.displayImage(ImageHelper.getUserPhotoBigPreview(imageDto.getUrl()), imgAvatar, mOptions, mAnimateFirstListener);

        if (selectedImageDto.getIsAvatar()) {
            findViewById(R.id.txtSetAsDefault).setVisibility(View.GONE);
            findViewById(R.id.txtDeleteImage).setVisibility(View.GONE);
        } else {
            findViewById(R.id.txtSetAsDefault).setVisibility(View.VISIBLE);
            findViewById(R.id.txtDeleteImage).setVisibility(View.VISIBLE);
        }
    }

    private void addImage(JSONObject imageJson) {
        if(!isProgressShow()) {
            showProgress(getString(R.string.upload_new_image));
        }

        HashMap<String, String> userDetails = getSession().getUserDetails();
        String accessToken = userDetails.get(SessionManager.KEY_ACCESS_TOCKEN);
        String userId = userDetails.get(SessionManager.KEY_ID);

        DataStore.profileAddImage(accessToken, userId, imageJson, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }
                hideProgress(true);

                UserPhotoDto imageDto = (UserPhotoDto) result;

                // add to small preview
                LinearLayout holderUsersPhotos = (LinearLayout) findViewById(R.id.holderUserPhotos);
                displayImageSmallPreview(holderUsersPhotos, imageDto);
                displayImageBigPreview(imageDto);
            }
        });
    }

    private void removeImage() {
        showProgress(getString(R.string.deleting_image));

        HashMap<String, String> userDetails = getSession().getUserDetails();
        String userId = userDetails.get(SessionManager.KEY_ID);
        String accessToken = userDetails.get(SessionManager.KEY_ACCESS_TOCKEN);

        DataStore.profileDeleteImage(EditProfileActivity.this, accessToken, userId, selectedImageDto.getId(), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }
                hideProgress(true);

                // remove from small preview
                LinearLayout holderUserPhotos = (LinearLayout) findViewById(R.id.holderUserPhotos);
                ImageView imageView = (ImageView) holderUserPhotos.findViewWithTag(selectedImageDto);
                holderUserPhotos.removeView(imageView);

                for (UserPhotoDto userPhotoDto : profile.getPhotos()) {
                    if (userPhotoDto.getIsAvatar()) {
                        selectedImageDto = userPhotoDto;
                        break;
                    }
                }

                // update big preview
                displayImageBigPreview(selectedImageDto);
            }
        });
    }

    private void setImageAsAvatar() {
        showProgress(getString(R.string.loading));
        HashMap<String, String> userDetails = getSession().getUserDetails();
        String userId = userDetails.get(SessionManager.KEY_ID);
        String accessToken = userDetails.get(SessionManager.KEY_ACCESS_TOCKEN);

        DataStore.profileUpdateImage(accessToken, userId, selectedImageDto.getId(), true, new DataStore.OnResultReady() {

            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }
                hideProgress(true);

                profile = (UserDto) result;

                drawImageManager(profile.getPhotos());

                getSession().updateValue(SessionManager.KEY_AVATAR, selectedImageDto.getUrl());
                setResult(RESULT_OK);
            }
        });
    }

    private void setLoading(boolean isLoading) {
        if(isLoading) {
            findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            findViewById(R.id.holderEditProfileScreen).setVisibility(View.GONE);
        } else {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            findViewById(R.id.holderEditProfileScreen).setVisibility(View.VISIBLE);
        }
    }

    private void onAddNewPhotoClicked() {
        AlertDialog dialog = imageUploader.selectPhoto();
        dialog.show();
    }

    private void onBirthDateClicked() {
        showDatePicker();
    }

    private void onSetAsDefaultClicked() {
        setImageAsAvatar();
    }

    private void onDeleteImageClicked() {
        removeImage();
    }
}
