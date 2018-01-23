package com.duzieblo.example.takepicture;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHOTO = 2;

    private static final String PHOTO_FILENAME = "photo.jpg";

    @BindView(R.id.imageView) ImageView mImageView;
    @BindView(R.id.fabTakePhoto) FloatingActionButton mFab;

    private Context mContext;
    private Unbinder mUnbinder;

    private File mPhotoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUnbinder = ButterKnife.bind(this);


        mContext = this;

        Log.d(getClass().getName(), "onCreate() Szerokość obrazka: " + mImageView.getWidth());

        mImageView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.d(getClass().getName(), "Widok imageView został dołączony");
                Log.d(getClass().getName(), "onWindowAttached - Szerokość obrazka: " + mImageView.getWidth());
                mImageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

        });

        Log.d(getClass().getName(), "Koniec wywołania metody onCreate()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUnbinder.unbind();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = FileProvider.getUriForFile(this,
                        "com.duzieblo.example.takepicture.fileprovider",
                        mPhotoFile);
                revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                if (mPhotoFile != null && mPhotoFile.exists()) {
                    Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),
                            this);
                    mImageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    @OnClick(R.id.fabTakePhoto)
    public void takePhoto() {
        Log.d(getClass().getName(), "FAB click" + mImageView.getWidth());
        mPhotoFile = getPhotoFile(PHOTO_FILENAME);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (mPhotoFile != null && canTakePhoto(captureImage)) {
            Uri uri = FileProvider.getUriForFile(this,
                    "com.duzieblo.example.takepicture.fileprovider",
                    mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

            //zwraca aktywności które obsługują intencję robienia zdjęć
            List<ResolveInfo> cameraActivities = getPackageManager().
                    queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo activity : cameraActivities) {
                //przydzielenie uprawnienia do zapisu pod wskazanym URI dla aktywnosci,
                //które obsługują robienia zdjęć
                grantUriPermission(activity.activityInfo.packageName,
                        uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }

            startActivityForResult(captureImage, REQUEST_PHOTO);
        } else {
            Toast.makeText(this, R.string.no_take_photo, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.imageView)
    public void showPhoto() {
        if (mPhotoFile != null && mPhotoFile.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("image/jpg");
            Uri uri = FileProvider.getUriForFile(this,
                    "com.duzieblo.example.takepicture.fileprovider",
                    mPhotoFile);
            intent.setData(uri);
            List<ResolveInfo> previewPhotoActivities = getPackageManager().
                    queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo activity : previewPhotoActivities) {
                //przydzielenie uprawnienia do zapisu pod wskazanym URI dla aktywnosci,
                //które obsługują robienia zdjęć
                grantUriPermission(activity.activityInfo.packageName,
                        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            startActivity(intent);
        }

    }

    private File getPhotoFile(String name) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, name);
    }

    /**
     * Metoda sprawdza czy istnieje jakiekolwiek aktywność która obsługuję robienie zdjęcia.
     *
     * @param intent obiekt intencji robienia zdjęcia.
     * @return
     */
    private boolean canTakePhoto(Intent intent) {
        return intent.resolveActivity(getPackageManager()) != null;
    }
}
