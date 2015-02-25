package com.nl.clubbook.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.nl.clubbook.R;
import com.nl.clubbook.model.ClubbookPreferences;
import com.nl.clubbook.ui.adapter.UserPhotosAdapter;
import com.nl.clubbook.model.httpclient.HttpClientManager;
import com.nl.clubbook.model.data.User;
import com.nl.clubbook.model.data.UserPhoto;
import com.nl.clubbook.ui.fragment.dialog.MessageDialog;
import com.nl.clubbook.helper.ImageUploader;
import com.nl.clubbook.helper.UiHelper;
import com.nl.clubbook.ui.fragment.dialog.SelectImageDialogFragment;
import com.nl.clubbook.ui.view.HorizontalListView;
import com.nl.clubbook.utils.NetworkUtils;
import com.nl.clubbook.utils.ParseUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.List;

public class EditProfileActivity extends BaseDateActivity implements View.OnClickListener,
        MessageDialog.MessageDialogListener, AdapterView.OnItemClickListener, SelectImageDialogFragment.OnPickOptionSelectedListener {

    public static final int REQUEST_CODE = 6364;

    private UserPhoto mSelectedImageDto;
    private ImageUploader mImageUploader;
    private User mProfile;
    private UserPhotosAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_edit_profile);

        sendScreenStatistic(R.string.profile_screen_android);

        setupToolBar();
        initImageHelpers();
        initView();

        loadData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mImageUploader.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        Picasso.with(this).cancelRequest(mTarget);

        super.onDestroy();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mAdapter == null) {
            return;
        }

        displayImageBigPreview(mAdapter.getItem(position));
    }

    @Override
    public void onPositiveButtonClick(MessageDialog dialogFragment) {
        removeImage();
    }

    @Override
    public void onNegativeButtonClick(MessageDialog dialogFragment) {
        dialogFragment.dismissAllowingStateLoss();
    }

    @Override
    public void onPickFromCamera() {
        mImageUploader.onPickFromCamera();
    }

    @Override
    public void onPickFromGallery() {
        mImageUploader.onPickFromGallery();
    }

    private void initView() {
        initActionBar(R.string.edit_user_profile);

        findViewById(R.id.imgAddNewPhoto).setOnClickListener(this);
        findViewById(R.id.editBirthDate).setOnClickListener(this);
        findViewById(R.id.txtSetAsDefault).setOnClickListener(this);
        findViewById(R.id.txtDeleteImage).setOnClickListener(this);
        findViewById(R.id.txtSave).setOnClickListener(this);

        HorizontalListView listView = (HorizontalListView)findViewById(R.id.listUserPhotos);
        listView.setOnItemClickListener(this);
    }

    private void onSaveClicked() {
        //username
        EditText editName = (EditText)findViewById(R.id.editName);
        final String userName = editName.getText().toString().trim();
        if (userName.length() < 2) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.name_incorrect));
            return;
        }

        //birth date
        EditText editBirthDate = (EditText) findViewById(R.id.editBirthDate);
        final String birthDate = editBirthDate.getText().toString().trim();
        if (birthDate.length() < 6) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.dob_incorrect));
            return;
        }

        //age
        final String strAge = getAge(birthDate);
        int age = ParseUtils.parseInt(strAge);
        if(age < 18) {
            showMessageDialog(getString(R.string.app_name), getString(R.string.you_must_be_18_age_to_be_able_use_clubbook));
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
        showProgressDialog(getString(R.string.loading));

        final ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        HttpClientManager.getInstance().updateUserProfile(preferences.getAccessToken(), userName,
                gender, mServerFormat.format(mBirthDate), country, aboutMe, new HttpClientManager.OnResultReady() {
                    @Override
                    public void onReady(Object result, boolean failed) {

                        hideProgressDialog();
                        if (failed) {
                            showToast(R.string.something_went_wrong_please_try_again);
                            return;
                        }

                        preferences.updateValue(ClubbookPreferences.KEY_NAME, userName);
                        preferences.updateValue(ClubbookPreferences.KEY_BIRTHDAY, birthDate);
                        preferences.updateValue(ClubbookPreferences.KEY_ABOUT_ME, aboutMe);
                        preferences.updateValue(ClubbookPreferences.KEY_GENDER, gender);
                        preferences.updateValue(ClubbookPreferences.KEY_AGE, strAge);
                        preferences.updateValue(ClubbookPreferences.KEY_COUNTRY, country);

                        showToast(R.string.user_information_saved);

                        setResult(RESULT_OK);
                        finish();
                    }
                }
        );
    }

    private void initImageHelpers() {
        mImageUploader = new ImageUploader(this) {
            @Override
            public void startActivityForResultHolder(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }

            @Override
            public void onImageUploaded(JSONObject imageObj) {
                addImage(imageObj);
            }

            @Override
            public void onImageSelected(@Nullable Bitmap bitmap) {
                uploadImage();
            }

            @Override
            public void onShowProgress() {
                if(!isProgressShow()) {
                    showProgressDialog(getString(R.string.upload_new_image));
                }
            }
        };
    }

    protected void loadData() {
        if(!NetworkUtils.isOn(EditProfileActivity.this)) {
            findViewById(R.id.progressBar).setVisibility(View.GONE);
            showToast(R.string.no_connection);
            return;
        }

        setLoading(true);

        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        HttpClientManager.getInstance().retrieveUser(preferences.getAccessToken(), new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (isFinishing()) {
                    return;
                }

                findViewById(R.id.progressBar).setVisibility(View.GONE);

                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    return;
                }

                setLoading(false);

                mProfile = (User) result;

                // update UI components
                EditText editBirthDate = (EditText) findViewById(R.id.editBirthDate);
                EditText editName = (EditText) findViewById(R.id.editName);
                EditText editAboutMe = (EditText) findViewById(R.id.editAboutMe);

                String birthDate = mProfile.getBirthday();
                if (birthDate != null && birthDate.length() > 0) {
                    parseBirthDate(birthDate);

                    editBirthDate.setText(mDisplayFormat.format(mBirthDate));
                }

                UiHelper.createGenderSpinner((Spinner) findViewById(R.id.spinGender), EditProfileActivity.this, mProfile.getGender());
                UiHelper.createCountrySpinner((Spinner) findViewById(R.id.spinCountry), EditProfileActivity.this, mProfile.getCountry());
                editName.setText(mProfile.getName());
                editAboutMe.setText(mProfile.getAboutMe());

                drawImageManager(mProfile.getPhotos());
            }
        });
    }

    private void drawImageManager(@Nullable List<UserPhoto> userPhotoList) {
        if(userPhotoList == null) {
            return;
        }

        HorizontalListView listUserPhotos = (HorizontalListView)findViewById(R.id.listUserPhotos);
        mAdapter = new UserPhotosAdapter(EditProfileActivity.this, userPhotoList);
        listUserPhotos.setAdapter(mAdapter);

        for (UserPhoto userPhoto : userPhotoList) {
            if(userPhoto.getIsAvatar()) {
                displayImageBigPreview(userPhoto);
                break;
            }
        }
    }

    private void displayImageBigPreview(UserPhoto imageDto) {
        mSelectedImageDto = imageDto;

        ImageView imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        Picasso.with(getBaseContext()).load(imageDto.getUrl()).error(R.drawable.ic_avatar_unknown).into(imgAvatar);

        if (mSelectedImageDto.getIsAvatar()) {
            findViewById(R.id.txtSetAsDefault).setVisibility(View.GONE);
            findViewById(R.id.txtDeleteImage).setVisibility(View.GONE);
        } else {
            findViewById(R.id.txtSetAsDefault).setVisibility(View.VISIBLE);
            findViewById(R.id.txtDeleteImage).setVisibility(View.VISIBLE);
        }
    }

    private void addImage(JSONObject imageJson) {
        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        String accessToken = preferences.getAccessToken();
        String userId = preferences.getUserId();

        HttpClientManager.getInstance().profileAddImage(accessToken, userId, imageJson, new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                hideProgressDialog();
                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    return;
                }

                UserPhoto imageDto = (UserPhoto) result;

                // add to small preview
                if (mAdapter != null) {
                    mAdapter.addNewImage(imageDto, mAdapter.getCount());
                }
                displayImageBigPreview(imageDto);
            }
        });
    }

    private void removeImage() {
        showProgressDialog(getString(R.string.deleting_image));

        ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        String accessToken = preferences.getAccessToken();
        String userId = preferences.getUserId();

        HttpClientManager.getInstance().profileDeleteImage(EditProfileActivity.this, accessToken, userId, mSelectedImageDto.getId(), new HttpClientManager.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                hideProgressDialog();
                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    return;
                }

                // remove from small preview
                if (mAdapter != null) {
                    mAdapter.removePhoto(mSelectedImageDto);
                }

                for (UserPhoto userPhoto : mProfile.getPhotos()) {
                    if (userPhoto.getIsAvatar()) {
                        mSelectedImageDto = userPhoto;
                        break;
                    }
                }

                // update big preview
                displayImageBigPreview(mSelectedImageDto);
            }
        });
    }

    private void setImageAsAvatar() {
        showProgressDialog(getString(R.string.loading));

        final ClubbookPreferences preferences = ClubbookPreferences.getInstance(getBaseContext());
        String accessToken = preferences.getAccessToken();
        String userId = preferences.getUserId();

        HttpClientManager.getInstance().profileUpdateImage(accessToken, userId, mSelectedImageDto.getId(), true, new HttpClientManager.OnResultReady() {

            @Override
            public void onReady(Object result, boolean failed) {
                hideProgressDialog();
                if (failed) {
                    showToast(R.string.something_went_wrong_please_try_again);
                    return;
                }

                mProfile = (User) result;

                drawImageManager(mProfile.getPhotos());

                String url = mSelectedImageDto.getUrl();
                preferences.updateValue(ClubbookPreferences.KEY_AVATAR, url);

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
        DialogFragment dialogFragment = new SelectImageDialogFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(dialogFragment, SelectImageDialogFragment.TAG);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private void onBirthDateClicked() {
        showDatePicker();
    }

    private void onSetAsDefaultClicked() {
        setImageAsAvatar();
    }

    private void onDeleteImageClicked() {
        showMessageDialog(
                getString(R.string.app_name),
                getString(R.string.are_you_sure_you_want_delete_this_photo),
                getString(R.string.delete),
                getString(R.string.cancel)
        );
    }

    private Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setIcon(new BitmapDrawable(getResources(), bitmap));
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setIcon(R.drawable.ic_transparent);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }
    };
}
