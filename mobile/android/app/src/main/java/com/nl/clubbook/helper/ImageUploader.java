package com.nl.clubbook.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.cloudinary.Cloudinary;
import com.nl.clubbook.R;
import com.nl.clubbook.activity.BaseActivity;
import com.nl.clubbook.utils.L;

import org.jetbrains.annotations.Nullable;
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

    private BaseActivity mActivity;
    private ImageIntentData mImageData = new ImageIntentData();

    public Activity getActivity() {
        return mActivity;
    }

    public void setActivity(BaseActivity activity) {
        mActivity = activity;
    }

    public ImageUploader(BaseActivity activity) {
        mActivity = activity;
        cloudinary = new Cloudinary(mActivity.getApplicationContext());
    }

    public abstract void startActivityForResultHolder(android.content.Intent intent, int requestCode);

    public abstract void onImageSelected(@Nullable Bitmap bitmap);

    public abstract void onImageUploaded(@Nullable JSONObject imageObj);

    public AlertDialog selectPhoto() {
        final String[] items = new String[] {
                mActivity.getString(R.string.take_from_camera),
                mActivity.getString(R.string.select_from_gallery)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mActivity, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);

        builder.setTitle(mActivity.getString(R.string.select_image));
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
                    //pick from gallery
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResultHolder(
                            Intent.createChooser(intent, mActivity.getString(R.string.complete_action_using)),
                            PICK_FROM_FILE
                    );
                }
            }
        });

        return builder.create();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == PICK_FROM_CAMERA) {
            doCrop();
        } else {
            decodeStream(requestCode, imageReturnedIntent);
        }
    }

    public void uploadImage() {
        if(mImageData.requestCode == PICK_FROM_FILE) {
            pickFromFile(mImageData.imgIntent);
        } else {
            cropFromCamera(mImageData.imgIntent);
        }
    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = mActivity.getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(mActivity, mActivity.getString(R.string.can_not_find_image_crop_app), Toast.LENGTH_SHORT).show();
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
                    final CropOption cropOption = new CropOption();

                    cropOption.title = mActivity.getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    cropOption.icon = mActivity.getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    cropOption.appIntent = new Intent(intent);

                    cropOption.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(cropOption);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(mActivity.getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(mActivity.getString(R.string.choose_crop_app));
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResultHolder(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            mActivity.getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();

                alert.show();
            }
        }
    }

    private void pickFromFile(Intent imageReturnedIntent) {
        final Uri selectedImage = imageReturnedIntent.getData();

        new AsyncTask<Void, Void, JSONObject>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mActivity.showProgressDialog(mActivity.getString(R.string.upload_new_image));
            }

            @Override
            protected JSONObject doInBackground(Void... params) {
                JSONObject imageObj = null;

                try {
                    // TODO: rotate image
                    // http://stackoverflow.com/questions/3647993/android-bitmaps-loaded-from-gallery-are-rotated-in-imageview

                    Bitmap mBitmap = readBitmap(selectedImage);
                    Bitmap scaled = getResizedBitmap(mBitmap, 800);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    InputStream is = new ByteArrayInputStream(stream.toByteArray());
                    imageObj = cloudinary.uploader().upload(is, Cloudinary.asMap("format", "jpg"));

                } catch (Exception e) {
                    L.i("" + e);
                }

                return imageObj;
            }

            @Override
            protected void onPostExecute(JSONObject result) {
                onImageUploaded(result);
            }
        }.execute();
    }

    private void cropFromCamera(Intent imageReturnedIntent) {
        final Bundle extras = imageReturnedIntent.getExtras();

        if (extras != null) {

            new AsyncTask<Void, Void, JSONObject>() {
                @Override
                protected JSONObject doInBackground(Void... params) {
                    Bitmap photo = extras.getParcelable("data");

                    Bitmap scaled = getResizedBitmap(photo, 800);

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    scaled.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    InputStream is = new ByteArrayInputStream(stream.toByteArray());

                    JSONObject imageObj = null;

                    try {
                        imageObj = cloudinary.uploader().upload(is, Cloudinary.asMap("width", "1000", "height", "1000", "crop", "limit", "format", "jpg"));
                    } catch (IOException e) {
                        L.i("" + e);
                    }

                    return imageObj;
                }

                @Override
                protected void onPostExecute(JSONObject result) {
                    File f = new File(mImageCaptureUri.getPath());

                    if (f.exists()) {
                        f.delete();
                    }

                    onImageUploaded(result);
                }
            }.execute();

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
            fileDescriptor = mActivity.getContentResolver().openAssetFileDescriptor(selectedImage, "r");
        } catch (FileNotFoundException e) {
            L.i("" + e);
        } finally {
            if(fileDescriptor != null) {
                try {
                    bm = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
                    fileDescriptor.close();
                } catch (IOException e) {
                    L.i("" + e);
                }
            }
        }
        return bm;
    }

    private void decodeStream(final int requestCode, final Intent data) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {

                if(requestCode == CROP_FROM_CAMERA) {
                    return getBitmapFromCamera(data);
                } else {
                    return getBitmapFromFile(data);
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if(bitmap != null) {
                    mImageData.requestCode = requestCode;
                    mImageData.imgIntent = data;
                }

                onImageSelected(bitmap);
            }
        }.execute();
    }

    private Bitmap getBitmapFromCamera(Intent data) {
        Bundle extras = data.getExtras();

        return extras.getParcelable("data");
    }

    private Bitmap getBitmapFromFile(Intent data) {
        String filePath = getFilePath(data);

        if(filePath != null) {
            Bitmap result =  decodeSampledBitmapFromFile(filePath, 400, 400);

            try {
                ExifInterface exif = new ExifInterface(filePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                if(orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(270);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
                }else if(orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
                } else if(orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    result = Bitmap.createBitmap(result, 0, 0, result.getWidth(), result.getHeight(), matrix, true);
                }
            } catch (IOException e) {
                L.i("" + e);
            }

            return result;
        } else {
            return null;
        }
    }

    private Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private String getFilePath(Intent data) {
        Uri selectedImage = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = mActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);

        String filePath = null;
        if(cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return filePath;
    }

    private class ImageIntentData {
        int requestCode;
        Intent imgIntent;
    }
}
