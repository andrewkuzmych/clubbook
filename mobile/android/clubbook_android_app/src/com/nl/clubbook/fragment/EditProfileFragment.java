package com.nl.clubbook.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.control.DatePickerFragment;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.helper.AlertDialogManager;
import com.nl.clubbook.helper.ImageHelper;
import com.nl.clubbook.helper.ImageUploader;
import com.nl.clubbook.helper.SessionManager;
import com.nl.clubbook.helper.UiHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

public class EditProfileFragment extends BaseFragment {

    EditText user_text, dob_text;
    Spinner gender_spinner;
    private Button saveButton, addNewPhotoButton;
    private ImageView selectedImage;
    private ImageUploader imageUploader;
    private UserDto profile;
    AlertDialogManager alert = new AlertDialogManager();
    private LinearLayout imagesHolder;

    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;
    protected ImageLoadingListener animateFirstListener = new SimpleImageLoadingListener();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_list_image)
                .showImageForEmptyUri(R.drawable.default_list_image)
                .showImageOnFail(R.drawable.default_list_image)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        getActivity().setTitle(getString(R.string.header_profile));

        View rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        imagesHolder = (LinearLayout) rootView.findViewById(R.id.imagesHolder);
        saveButton = (Button) rootView.findViewById(R.id.save_profile_button);
        addNewPhotoButton = (Button) rootView.findViewById(R.id.add_new_photo);
        user_text = (EditText) rootView.findViewById(R.id.name_text);
        dob_text = (EditText) rootView.findViewById(R.id.dob_text);
        selectedImage = (ImageView) rootView.findViewById(R.id.selectedImage);
        imageUploader = new ImageUploader(getActivity()) {
            @Override
            public void startActivityForResultHolder(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }

            @Override
            public void onImageSelected(JSONObject imageObj) throws JSONException {
                String image_url = ImageHelper.getUserPhotoPreview(imageObj.getString("url"));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
                ImageView image = new ImageView(getActivity());
                image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                image.setLayoutParams(layoutParams);
                imagesHolder.addView(image);
                imageLoader.displayImage(image_url, image, options, animateFirstListener);
                image.requestLayout();

                Log.v("ImageUploadActivity", imageObj.getString("public_id"));
            }
        };

        loadData();

        return rootView;
    }

    private void setHandlers() {
        final BaseFragment thisInstance = this;

        addNewPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog dialog = imageUploader.selectPhoto();
                dialog.show();
                Log.i("test", "test");
            }
        });


        dob_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(profile.getDob());
            }
        });

        // save user data
        saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                // validate
                String errorTitle = "Update profile failed.";

                String user_name = user_text.getText().toString().trim();
                if (user_name.trim().length() < 2) {
                    alert.showAlertDialog(getActivity(), errorTitle, getString(R.string.name_incorrect), false);
                    return;
                }

                String dob = dob_text.getText().toString().trim();
                if (dob.trim().length() < 6) {
                    alert.showAlertDialog(getActivity(), errorTitle, getString(R.string.dob_incorrect), false);
                    return;
                }

                UiHelper.GenderPair data = (UiHelper.GenderPair) gender_spinner.getSelectedItem();
                String gender = data.getValue();

                // update on server side
                ((BaseActivity) getActivity()).showProgress("Loading...");

                DataStore.updateUserProfile(thisInstance.getSession().getUserDetails().get(SessionManager.KEY_ID),
                        user_name, gender, dob, new DataStore.OnResultReady() {
                            @Override
                            public void onReady(Object result, boolean failed) {
                                if (failed) {
                                    ((BaseActivity) getActivity()).hideProgress(false);
                                    return;
                                }

                                ((BaseActivity) getActivity()).hideProgress(true);

                                profile = (UserDto) result;

                                // update UI
                                ((MainActivity) getActivity()).updateMyInformation(profile);
                            }
                        }
                );
            }
        });
    }

    protected void loadData() {
        showProgress();

        DataStore.retrieveUser(this.getSession().getUserDetails().get(SessionManager.KEY_ID), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }

                hideProgress(true);

                profile = (UserDto) result;

                dob_text.setText(profile.getDob());
                //init gender
                gender_spinner = UiHelper.createGenderSpinner((Spinner) getActivity().findViewById(R.id.gender), getActivity(), profile.getGender());
                // update UI components
                user_text.setText(profile.getName());

                setHandlers();
            }
        });
    }

    private void showDatePicker(String dob) {
        String[] dates = dob.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]) - 1;
        int day = Integer.parseInt(dates[2]);

        DatePickerFragment date = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        date.setArguments(args);
        // Set Call back to capture selected date
        date.setCallBack(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dob_text.setText(String.format("%02d", dayOfMonth) + "." + String.format("%02d", monthOfYear + 1) + "." + String.valueOf(year));
            }
        });
        date.show(getActivity().getSupportFragmentManager(), "Date Picker");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        imageUploader.onActivityResult(requestCode, resultCode, data);
    }

}
