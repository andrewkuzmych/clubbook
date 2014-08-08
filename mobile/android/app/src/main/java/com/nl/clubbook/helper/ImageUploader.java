package com.nl.clubbook.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cloudinary.Cloudinary;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by odats on 23/07/2014.
 */
public abstract class ImageUploader {

    // http://cloudinary.com/documentation/java_image_upload
    private Cloudinary cloudinary;
    private Uri mImageCaptureUri;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private static final int DEFAULT_VIEW = 1;

    private Activity activity;

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public ImageUploader(Activity activity) {
        setActivity(activity);
        cloudinary = new Cloudinary(getActivity().getApplicationContext());
    }

    public abstract void startActivityForResultHolder(android.content.Intent intent, int requestCode);

    public abstract void onImageSelected(JSONObject imageObj) throws JSONException;

    public AlertDialog selectPhoto() {
        final String[] items = new String[]{"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    //pick from camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                    intent.putExtra("return-data", true);
                    startActivityForResultHolder(intent, PICK_FROM_CAMERA);

                } else {
                    //pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResultHolder(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });

        return builder.create();
    }

    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageReturnedIntent) {

        if (resultCode != getActivity().RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                doCrop();
                break;

            case PICK_FROM_FILE:
                // mImageCaptureUri = imageReturnedIntent.getData();
                // doCrop();
                final Uri selectedImage = imageReturnedIntent.getData();

                new AsyncTask<Void, Void, JSONObject>() {
                    @Override
                    protected JSONObject doInBackground(Void... params) {
                        org.json.JSONObject imageObj = null;

                        try {
                            // TODO: rotate image
                            // http://stackoverflow.com/questions/3647993/android-bitmaps-loaded-from-gallery-are-rotated-in-imageview

                            Bitmap mBitmap = readBitmap(selectedImage);
                            Bitmap scaled = getResizedBitmap(mBitmap, 800);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            InputStream is = new ByteArrayInputStream(stream.toByteArray());
                            imageObj = cloudinary.uploader().upload(is, Cloudinary.asMap("format", "jpg"));

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                        return imageObj;
                    }

                    @Override
                    protected void onPostExecute(JSONObject result) {
                        if (result == null) {
                            //TODO error
                            return;
                        }

                        try {
                            onImageSelected(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();

                break;

            case CROP_FROM_CAMERA:
                final Bundle extras = imageReturnedIntent.getExtras();

                if (extras != null) {

                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            Bitmap photo = extras.getParcelable("data");
                            //mImageView.setImageBitmap(photo);

                            // Bitmap  mBitmap = MediaStore.Images.Media.getBitmap(MainActivity.this.getContentResolver(), selectedImage);
                            Bitmap scaled = getResizedBitmap(photo, 800);//Bitmap.createScaledBitmap(mBitmap, 500, 500, true);

                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            InputStream is = new ByteArrayInputStream(stream.toByteArray());

                            try {
                                cloudinary.uploader().upload(is, Cloudinary.asMap("width", "1000", "height", "1000", "crop", "limit", "format", "jpg"));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            File f = new File(mImageCaptureUri.getPath());

                            if (f.exists()) {
                                f.delete();
                            }
                        }
                    }.execute();

                }

                break;

        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(getActivity(), "Can not find image crop app", Toast.LENGTH_SHORT).show();

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

                startActivityForResultHolder(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getActivity().getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);

                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getActivity().getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Crop App");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResultHolder(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getActivity().getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
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

    private Bitmap readBitmap(Uri selectedImage) {
        Bitmap bm = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 5;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = getActivity().getContentResolver().openAssetFileDescriptor(selectedImage, "r");
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

}
