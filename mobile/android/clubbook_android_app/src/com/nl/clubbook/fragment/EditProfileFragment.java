package com.nl.clubbook.fragment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.activity.MainActivity;
import com.nl.clubbook.control.DatePickerFragment;
import com.nl.clubbook.datasource.DataStore;
import com.nl.clubbook.datasource.UserDto;
import com.nl.clubbook.datasource.UserPhotoDto;
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

import java.util.List;

public class EditProfileFragment extends BaseFragment {

    EditText user_text, dob_text, bio_text;
    Spinner gender_spinner, country_spinner;
    private Button saveButton, addNewPhotoButton, setAsAvatar, removeImage;
    private ImageView selectedImage;
    private UserPhotoDto selectedImageDto;
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
        setAsAvatar = (Button) rootView.findViewById(R.id.setAsAvatar);
        removeImage = (Button) rootView.findViewById(R.id.removeImage);
        addNewPhotoButton = (Button) rootView.findViewById(R.id.add_new_photo);
        user_text = (EditText) rootView.findViewById(R.id.name_text);
        dob_text = (EditText) rootView.findViewById(R.id.dob_text);
        bio_text = (EditText) rootView.findViewById(R.id.bio_text);
        selectedImage = (ImageView) rootView.findViewById(R.id.selectedImage);
        imageUploader = new ImageUploader(getActivity()) {
            @Override
            public void startActivityForResultHolder(Intent intent, int requestCode) {
                startActivityForResult(intent, requestCode);
            }

            @Override
            public void onImageSelected(JSONObject imageObj) throws JSONException {
                addImage(imageObj);
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
            }
        });


        dob_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(profile.getDob());
            }
        });

        setAsAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                setImageAsAvatar();
            }
        });

        removeImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View view) {
                removeImage();
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

                String bio = bio_text.getText().toString().trim();

                UiHelper.TextValuePair dataGender = (UiHelper.TextValuePair) gender_spinner.getSelectedItem();
                String gender = dataGender.getValue();

                UiHelper.TextValuePair dataCountry = (UiHelper.TextValuePair) country_spinner.getSelectedItem();
                String country = dataCountry.getValue();

                // update on server side
                ((BaseActivity) getActivity()).showProgress("Loading...");

                DataStore.updateUserProfile(thisInstance.getSession().getUserDetails().get(SessionManager.KEY_ID),
                        user_name, gender, dob, country, bio, new DataStore.OnResultReady() {
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

                // update UI components
                dob_text.setText(profile.getDob());
                gender_spinner = UiHelper.createGenderSpinner((Spinner) getActivity().findViewById(R.id.gender), getActivity(), profile.getGender());
                country_spinner = UiHelper.createCountrySpinner((Spinner) getActivity().findViewById(R.id.country), getActivity(), profile.getCountry());
                user_text.setText(profile.getName());
                bio_text.setText(profile.getBio());

                drawImageManager(profile.getPhotos());

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


    //----------------------------------------------------------------------------------------------
    // image manager

    private void drawImageManager(List<UserPhotoDto> userPhotoDtoList) {
        imagesHolder.removeAllViews();
        for (UserPhotoDto userPhotoDto : userPhotoDtoList) {
            displayImageSmallPreview(userPhotoDto);
            if (userPhotoDto.getIsAvatar())
                displayImageBigPreview(userPhotoDto);
        }
    }

    private void displayImageSmallPreview(final UserPhotoDto imageDto) {
        // add to small preview
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
        ImageView image = new ImageView(getActivity());
        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image.setLayoutParams(layoutParams);
        imagesHolder.addView(image, 0);
        imageLoader.displayImage(ImageHelper.getUserPhotoSmallPreview(imageDto.getUrl()), image, options, animateFirstListener);
        image.requestLayout();

        image.setTag(imageDto);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayImageBigPreview(imageDto);
            }
        });
    }

    private void displayImageBigPreview(UserPhotoDto imageDto) {
        this.selectedImageDto = imageDto;
        // display src
        imageLoader.displayImage(ImageHelper.getUserPhotoBigPreview(imageDto.getUrl()), selectedImage, options, animateFirstListener);

        // display actions
        if (this.selectedImageDto.getIsAvatar()) {
            setAsAvatar.setVisibility(View.GONE);
            removeImage.setVisibility(View.GONE);
        } else {
            setAsAvatar.setVisibility(View.VISIBLE);
            removeImage.setVisibility(View.VISIBLE);
        }
    }

    private void addImage(JSONObject imageJson) {
        showProgress();
        DataStore.profileAddImage(this.getSession().getUserDetails().get(SessionManager.KEY_ID), imageJson, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }
                hideProgress(true);

                UserPhotoDto imageDto = (UserPhotoDto) result;

                // add to small preview
                displayImageSmallPreview(imageDto);
                displayImageBigPreview(imageDto);
            }
        });
    }

    private void removeImage() {
        showProgress();
        DataStore.profileDeleteImage(this.getSession().getUserDetails().get(SessionManager.KEY_ID), selectedImageDto.getId(), new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }
                hideProgress(true);

                // remove from small preview
                ImageView imageView = (ImageView) imagesHolder.findViewWithTag(selectedImageDto);
                imageView.setVisibility(View.GONE);
                // update big preview
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
        showProgress();
        DataStore.profileUpdateImage(this.getSession().getUserDetails().get(SessionManager.KEY_ID), selectedImageDto.getId(), true, new DataStore.OnResultReady() {
            @Override
            public void onReady(Object result, boolean failed) {
                if (failed) {
                    hideProgress(false);
                    return;
                }
                hideProgress(true);

                profile = (UserDto) result;

                // update UI
                drawImageManager(profile.getPhotos());

                ((MainActivity) getActivity()).updateMyInformation(profile);
            }
        });
    }
}
